package com.dongnao.jack.handlerAdapter;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
    
    public Object[] hand(HttpServletRequest request,
            HttpServletResponse response, Method method,
            Map<String, Object> beans);
}
