package com.dongnao.jack.argumentResolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dongnao.jack.annotation.Service;

@Service("httpSessionArgumentResolver")
public class HttpSessionArgumentResolver implements ArgumentResolver {
    
    public boolean support(Class<?> type, int paramIndex, Method method) {
        return HttpSession.class.isAssignableFrom(type);
    }
    
    public Object argumentResolver(HttpServletRequest request,
            HttpServletResponse response, Class<?> type, int paramIndex,
            Method method) {
        return request.getSession();
    }
    
}
