package com.vz.encrypt.demo.encrypt;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vz.encrypt.demo.common.SysMessage;
import com.vz.encrypt.demo.config.EncryptProperties;
import com.vz.encrypt.demo.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author visy.wang
 * @description: 请求解密过滤器
 * @date 2023/7/25 10:18
 */
@Slf4j
@Component
public class DecryptRequestFilter extends OncePerRequestFilter {
    private static final Gson GSON = new Gson();
    //请求中的加密参数名
    private static final String REQ_PARAMS_NAME = "req";
    @Autowired
    private EncryptProperties encryptProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("已进入请求解密过滤器，URL: [{}]{}, ContentType: {}", request.getMethod(), request.getRequestURI(), request.getContentType());

        try{
            //优先从Parameter中获取密文
            String ciphertext = request.getParameter(REQ_PARAMS_NAME);
            if(StringUtils.hasText(ciphertext)){
                log.info("已从Parameter读取到密文");
            }else{ //获取不到再从Body中读取
                log.info("尝试从Body读取密文...");
                byte[] bodyBytes = getBody(request.getInputStream());
                ciphertext = getCiphertext(bodyBytes);
            }

            log.info("原始参数：{}", ciphertext);
            if(!StringUtils.hasText(ciphertext)){
                writeError(response, "参数不能为空");
                return;
            }

            //解密
            byte[] ciphertextBytes = ciphertext.getBytes(StandardCharsets.UTF_8);
            byte[] newBodyBytes = AESUtil.decrypt(ciphertextBytes, encryptProperties.getKey());
            //解密后的JSON字符串
            String newBodyStr = new String(newBodyBytes, StandardCharsets.UTF_8);
            log.info("解密参数：{}", newBodyStr);

            //把JSON中的所有参数同时加到Parameter中，兼容非@RequestBody方式的接口
            Map<String, Object> otherParams = new HashMap<>();
            JsonObject newBodyJson = GSON.fromJson(newBodyStr, JsonObject.class);
            newBodyJson.entrySet().forEach(entry -> {
                JsonElement value = entry.getValue();
                if(value.isJsonPrimitive()){//只处理基本类型字段
                    otherParams.put(entry.getKey(), value.getAsString());
                }
            });

            //改写request
            RequestWrapper wrapper = new RequestWrapper(request, newBodyBytes, otherParams);
            chain.doFilter(wrapper, response);
        }catch (Exception e){
            log.info("请求参数解密出错：{}", e.getMessage(), e);
            writeError(response, "系统异常，请稍后再试");
        }
    }

    //获取Request的body数据
    private byte[] getBody(InputStream in) throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int len;
        byte[] buffer = new byte[1024];

        while((len = in.read(buffer)) != -1){
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    private String getCiphertext(byte[] bodyBytes){
        try{
            String bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);
            if(StringUtils.hasText(bodyStr)){
                JsonObject bodyJson = GSON.fromJson(bodyStr, JsonObject.class);
                JsonElement paramsElement = bodyJson.get(REQ_PARAMS_NAME);
                if(Objects.nonNull(paramsElement)){
                    return paramsElement.getAsString();
                }
            }
        }catch (Exception e){
            log.info("从Body读取密文出错：{}", e.getMessage(), e);
        }
        return null;
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        SysMessage result = SysMessage.failure(message);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().println(GSON.toJson(result));
    }
}
