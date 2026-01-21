package com.janetfilter.core.rulers;

public class ContainsAnyRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        if (rule == null || content == null) {
            return false;
        }
        
        // 分割规则字符串，支持逗号分隔的多个关键字
        String[] keywords = rule.split(",");
        for (String keyword : keywords) {
            if (keyword == null || keyword.isEmpty()) {
                continue;
            }
            
            // 如果内容包含任何一个关键字，返回true
            if (content.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
}