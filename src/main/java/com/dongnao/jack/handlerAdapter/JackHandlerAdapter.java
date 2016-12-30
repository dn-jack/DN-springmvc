package com.dongnao.jack.handlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dongnao.jack.annotation.Service;
import com.dongnao.jack.argumentResolver.ArgumentResolver;

@Service("jackHandlerAdapter")
public class JackHandlerAdapter implements HandlerAdapter {
    
    public Object[] hand(HttpServletRequest request,
            HttpServletResponse response, Method method,
            Map<String, Object> beans) {
        
        Class<?>[] paramClazzs = method.getParameterTypes();
        
        Object[] args = new Object[paramClazzs.length];
        
        //1、要拿到所有实现了ArgumentResolver这个接口的实现类
        Map<String, Object> argumentResolvers = getBeansOfType(beans,
                ArgumentResolver.class);
        
        int paramIndex = 0;
        int i = 0;
        for (Class<?> paramClazz : paramClazzs) {
            for (Map.Entry<String, Object> entry : argumentResolvers.entrySet()) {
                ArgumentResolver ar = (ArgumentResolver)entry.getValue();
                
                if (ar.support(paramClazz, paramIndex, method)) {
                    args[i++] = ar.argumentResolver(request,
                            response,
                            paramClazz,
                            paramIndex,
                            method);
                }
            }
            paramIndex++;
        }
        
        return args;
    }
    
    private Map<String, Object> getBeansOfType(Map<String, Object> beans,
            Class<?> intfType) {
        
        Map<String, Object> resultBeans = new HashMap<String, Object>();
        
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class<?>[] intfs = entry.getValue().getClass().getInterfaces();
            
            if (intfs != null && intfs.length > 0) {
                for (Class<?> intf : intfs) {
                    if (intf.isAssignableFrom(intfType)) {
                        resultBeans.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        
        return resultBeans;
    }
    
}
