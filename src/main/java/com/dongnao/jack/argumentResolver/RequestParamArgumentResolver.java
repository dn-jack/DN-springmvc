package com.dongnao.jack.argumentResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dongnao.jack.annotation.RequestParam;
import com.dongnao.jack.annotation.Service;

@Service("requestParamArgumentResolver")
public class RequestParamArgumentResolver implements ArgumentResolver {
    
    public boolean support(Class<?> type, int paramIndex, Method method) {
        
        Annotation[][] an = method.getParameterAnnotations();
        
        Annotation[] paramAns = an[paramIndex];
        
        for (Annotation paramAn : paramAns) {
            if (RequestParam.class.isAssignableFrom(paramAn.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    public Object argumentResolver(HttpServletRequest request,
            HttpServletResponse response, Class<?> type, int paramIndex,
            Method method) {
        
        Annotation[][] an = method.getParameterAnnotations();
        
        Annotation[] paramAns = an[paramIndex];
        
        for (Annotation paramAn : paramAns) {
            if (RequestParam.class.isAssignableFrom(paramAn.getClass())) {
                RequestParam rp = (RequestParam)paramAn;
                
                String value = rp.value();
                return request.getParameter(value);
            }
        }
        
        return null;
    }
    
}
