package com.github.passerr.idea.plugins.base.utils;

import com.google.gson.Gson;

/**
 * @author xiehai
 * @date 2022/06/29 11:00
 */
public interface GsonUtil {
    Gson GSON = new Gson();

    /**
     * gson序列化
     * @param o 任意对象
     * @return json字符串
     */
    static String serialize(Object o) {
        return GSON.toJson(o);
    }

    /**
     * gson反序列化
     * @param json  json字符串
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return {@link T}
     */
    static <T> T deserialize(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    static <T> T deepCopy(T t, Class<T> clazz) {
        return GsonUtil.deserialize(GsonUtil.serialize(t), clazz);
    }
}
