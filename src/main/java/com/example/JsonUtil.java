package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    public static Map<String, Map<String, String>> readJsonFile(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, HashMap.class);
    }
}
