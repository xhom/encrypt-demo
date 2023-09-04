package com.vz.encrypt.demo.encrypt;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author visy.wang
 * @description: 自定义请求包装器（用户改写请求参数）
 * @date 2023/7/25 14:05
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    private final Map<String, String[]> parameters = new HashMap<>();

    public RequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        this.body = body;
        parameters.putAll(request.getParameterMap());
    }

    public RequestWrapper(HttpServletRequest request, byte[] body, Map<String, Object> otherParams) {
        super(request);
        this.body = body;
        addAllParameters(otherParams);
    }

    //改写Parameter需要重写以下4个方法
    //=============================================================

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameters.get(name);
        return Objects.nonNull(values) && values.length>0 ? values[0] : null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    //改写Body需要重写以下2个方法
    //=============================================================

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return !isReady();
            }

            @Override
            public boolean isReady() {
                return inputStream.available() > 0;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    //内部方法
    //===============================================================

    private void addAllParameters(Map<String, Object> otherParams) {
        otherParams.forEach(this::addParameter);
    }

    public void addParameter(String name, Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        if (value instanceof String[]) {
            parameters.put(name, (String[]) value);
        } else if (value instanceof String) {
            parameters.put(name, new String[]{(String) value});
        } else {
            parameters.put(name, new String[]{String.valueOf(value)});
        }
    }
}
