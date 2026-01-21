package com.janetfilter.core.rulers;

public class ContainsAnyICRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        if (rule == null || content == null) {
            return false;
        }
        
        // 转换为小写，实现不区分大小写匹配
        String lowercaseContent = content.toLowerCase();
        
        // 分割规则字符串，支持逗号分隔的多个关键字
        String[] keywords = rule.split(",");
        for (String keyword : keywords) {
            if (keyword == null || keyword.isEmpty()) {
                continue;
            }
            
            // 如果内容包含任何一个关键字（不区分大小写），返回true
            if (lowercaseContent.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
}