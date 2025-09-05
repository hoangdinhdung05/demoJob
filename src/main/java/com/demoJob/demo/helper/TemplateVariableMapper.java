package com.demoJob.demo.helper;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateVariableMapper {

    public Map<String, Object> toMapTemplate(Object... objects) {
        Map<String, Object> result = new HashMap<>();
        for (Object obj : objects) {
            if (obj != null) {
                String key = obj.getClass().getSimpleName();
                //vd: User -> "user", userProfile -> "userProfile"
                key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                result.put(key, obj);
            }
        }
        return result;
    }
}
