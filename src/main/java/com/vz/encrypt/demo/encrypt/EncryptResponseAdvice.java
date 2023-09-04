package com.vz.encrypt.demo.encrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vz.encrypt.demo.config.EncryptProperties;
import com.vz.encrypt.demo.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author visy.wang
 * @description: 响应加密
 * @date 2023/7/25 10:19
 */
@Slf4j
@ControllerAdvice
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {
    //响应中的加密参数名
    private static final String RES_PARAMS_NAME = "res";
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private EncryptProperties encryptProperties;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //对所有响应加密
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try{
            //将返回结果转换为JSON
            Map<String, Object> result = new HashMap<>();
            String json = mapper.writeValueAsString(body);
            log.info("原始返回：{}", json);
            String encrypt = AESUtil.encrypt(json.getBytes(StandardCharsets.UTF_8), encryptProperties.getKey());
            log.info("加密返回：{}", encrypt);
            result.put(RES_PARAMS_NAME, encrypt);
            return result;
        }catch (Exception e){
            log.info("响应结果加密出错：{}", e.getMessage(), e);
        }
        return body;
    }
}
