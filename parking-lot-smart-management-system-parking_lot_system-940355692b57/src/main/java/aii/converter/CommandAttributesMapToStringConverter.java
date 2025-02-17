package aii.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class CommandAttributesMapToStringConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attributes) {
        try {
            return attributes == null ? null : objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}
