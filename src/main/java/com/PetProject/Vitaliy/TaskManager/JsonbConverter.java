package com.PetProject.Vitaliy.TaskManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

public class JsonbConverter implements AttributeConverter<Map<String,Object>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        try{
            return mapper.writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String string) {
        try{
            return mapper.readValue(string, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e){
            return new HashMap<>();
        }

    }
}
