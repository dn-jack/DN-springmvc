package com.dongnao.jack.argumentResolver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dongnao.jack.annotation.Service;

@Service("mapArgumentResolver")
public class MapArgumentResolver implements ArgumentResolver {
    
    public boolean support(Class<?> type, int paramIndex, Method method) {
        return Map.class.isAssignableFrom(type);
    }
    
    public Object argumentResolver(HttpServletRequest request,
            HttpServletResponse response, Class<?> type, int paramIndex,
            Method method) {
        Map<String, String[]> params = request.getParameterMap();
        
        Map<String, String> result = new HashMap<String, String>();
        
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            result.put(entry.getKey(), entry.getValue()[0]);
        }
        
        return result;
    }
    
}
