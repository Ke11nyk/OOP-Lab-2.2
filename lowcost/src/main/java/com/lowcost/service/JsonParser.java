package com.lowcost.service;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
    public static String toJsonObject(Object entity) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(entity);
    }
}