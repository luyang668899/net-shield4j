package com.janetfilter.sample;

import com.janetfilter.core.Environment;
import com.janetfilter.core.plugin.MyTransformer;
import com.janetfilter.core.plugin.PluginConfig;
import com.janetfilter.core.plugin.PluginEntry;

import java.util.Collections;
import java.util.List;

public class SamplePluginEntry implements PluginEntry {
    private PluginConfig config;
    
    @Override
    public void init(Environment environment, PluginConfig config) {
        // 初始化插件，这里可以读取配置文件
        this.config = config;
        System.out.println("Sample plugin initialized!");
        System.out.println("Plugin config data: " + config.getData());
    }

    @Override
    public String getName() {
        return "sample";
    }

    @Override
    public String getAuthor() {
        return "ja-netfilter team";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "A sample plugin for ja-netfilter";
    }

    @Override
    public List<MyTransformer> getTransformers() {
        // 返回插件的转换器列表
        return Collections.singletonList(new SampleTransformer(config));
    }
}