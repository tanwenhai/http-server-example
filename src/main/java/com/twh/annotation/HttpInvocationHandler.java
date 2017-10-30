package com.twh.annotation;

import com.twh.util.URLBuilder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

public class HttpInvocationHandler implements InvocationHandler {
    private final String host;
    private final String path;
    private final RestTemplate restTemplate;

    private String subPath = "";
    private RequestMethod requestMethod = RequestMethod.POST;

    public HttpInvocationHandler(String host, String path, RestTemplate restTemplate) {
        this.host = host;
        this.path = path;
        this.restTemplate = restTemplate;
    }

    private void init(Method method) {
        HttpRequest httpRequest = method.getAnnotation(HttpRequest.class);
        if (Objects.nonNull(httpRequest)) {
            requestMethod = httpRequest.method();
            subPath = httpRequest.path();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        init(method);
        String url = new URLBuilder().origin(host).addPath(path).addPath(subPath).buildString();

        Map<String, Object> uriVariables = null;
        if (args.length > 0) {
            // 有参数  设置url变量或者查询参数
            Parameter[] parameters = method.getParameters();
            // POST请求 第一个参数是body内容 后续是url变量或者查询参数
            int offset = requestMethod == RequestMethod.POST ? 1 : 0;
            for (int i = 0, size = parameters.length; i < size; i++) {
                Parameter parameter = parameters[i];

                // FIXME 可能会发生数组越界
                uriVariables.put(parameter.getName(), args[i + offset]);
            }
        }

        Class<?> returnType = method.getReturnType();
        switch (requestMethod) {
            case GET:
                return restTemplate.getForObject(url, returnType, uriVariables);
            case POST:
                return restTemplate.postForObject(url, args.length > 0 ? args[0] : null, returnType, uriVariables);
            default:
                throw new MethodNotSupportedException("不支持的请求类型代理 " + requestMethod);
        }
    }
}
