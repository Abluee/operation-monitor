package com.monitor.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

/**
 * JSON utility class using fastjson
 */
public class JsonUtils {

    private JsonUtils() {
    }

    /**
     * Convert object to JSON string
     */
    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }

    /**
     * Convert object to JSON string with pretty format
     */
    public static String toJSONStringPretty(Object obj) {
        return JSON.toJSONString(obj, true);
    }

    /**
     * Parse JSON string to object
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * Parse JSON string to TypeReference
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        return JSON.parseObject(json, typeReference);
    }

    /**
     * Parse JSON string to JSONObject
     */
    public static JSONObject parseObject(String json) {
        return JSON.parseObject(json);
    }

    /**
     * Parse JSON string to JSONArray
     */
    public static JSONArray parseArray(String json) {
        return JSON.parseArray(json);
    }

    /**
     * Parse JSON string to list
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * Parse JSON string to list of Map
     */
    public static List<Map<String, Object>> parseArrayToMapList(String json) {
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Parse JSON string to Map
     */
    public static Map<String, Object> parseToMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Check if string is valid JSON
     */
    public static boolean isValidJson(String json) {
        try {
            JSON.parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get value from JSONObject by path
     */
    public static <T> T getObject(JSONObject jsonObject, String path, Class<T> clazz) {
        return jsonObject.getObject(path, clazz);
    }

    /**
     * Convert object to JSONObject
     */
    public static JSONObject toJSONObject(Object obj) {
        return (JSONObject) JSON.toJSON(obj);
    }

    /**
     * Convert object to JSONArray
     */
    public static JSONArray toJSONArray(Object obj) {
        return (JSONArray) JSON.toJSON(obj);
    }
}
