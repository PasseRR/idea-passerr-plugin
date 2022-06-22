package com.github.passerr.idea.plugins.database.generator.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * 配置文件信息
 * @author xiehai
 * @date 2022/06/22 15:17
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigInfo {
    String entity;
    String mapper;
    String mapperXml;
    String service;
    String serviceImpl;
    String controller;

    /**
     * 是否是相对路径配置
     * @param s 路径
     * @return true/false
     */
    public static boolean isRelativePath(String s) {
        // 相对路径必须以/..开头
        return Objects.nonNull(s) && s.startsWith("/..");
    }
}
