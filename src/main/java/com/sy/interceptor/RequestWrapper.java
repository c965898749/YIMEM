package com.sy.interceptor;

import org.springframework.util.StreamUtils;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 请求体包装类，解决request.getReader()流只能读取一次的问题
 * 核心作用：将请求体内容缓存到字节数组，可重复读取，Controller层正常接收参数
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    // 缓存请求体的字节数组
    private final byte[] body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 读取请求体并缓存
        this.body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 返回缓存的字节数组流
        ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    /**
     * 对外提供方法，获取缓存的请求体字符串
     */
    public String getBodyString() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
