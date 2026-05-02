package com.saferoute.common.config;

import com.saferoute.common.entity.StudentGuardianEntity.Relationship;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convierte strings a Relationship enum aceptando español y diferentes casos.
 */
@Component
public class RelationshipEnumConverter implements Converter<String, Relationship> {

    @Override
    public Relationship convert(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String normalized = value.trim().toLowerCase();
        
        return switch (normalized) {
            case "padre", "father" -> Relationship.father;
            case "madre", "mother" -> Relationship.mother;
            case "acudiente", "guardian" -> Relationship.guardian;
            case "otro", "other" -> Relationship.other;
            default -> Relationship.valueOf(normalized); // fallback al valor exacto
        };
    }
}