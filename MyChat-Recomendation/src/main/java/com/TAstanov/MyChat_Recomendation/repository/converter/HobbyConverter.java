package com.TAstanov.MyChat_Recomendation.repository.converter;

import com.TAstanov.MyChat_Recomendation.domain.user.HOBBY;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class HobbyConverter implements AttributeConverter<List<HOBBY>, String> {
    @Override
    public String convertToDatabaseColumn(List<HOBBY> attribute) {
        StringBuilder databaseString = new StringBuilder();
        for (HOBBY hobby : attribute) {
            databaseString.append(hobby);
            databaseString.append(" ");
        }
        return String.valueOf(databaseString);
    }

    @Override
    public List<HOBBY> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(" "))
                .map(HOBBY::valueOf)
                .collect(Collectors.toList());
    }
}
