package com.TAstanov.MyChat_Recomendation.repository.converter;


import com.TAstanov.MyChat_Recomendation.domain.user.PROFESSION;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ProfessionsConverter implements AttributeConverter<List<PROFESSION>, String> {

    @Override
    public String convertToDatabaseColumn(List<PROFESSION> attribute) {
        StringBuilder dbData = new StringBuilder();
        for(PROFESSION profession: attribute){
            dbData.append(profession);
        }
        return String.valueOf(dbData);
    }

    @Override
    public List<PROFESSION> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(" ")).map(PROFESSION::valueOf).collect(Collectors.toList());
    }
}
