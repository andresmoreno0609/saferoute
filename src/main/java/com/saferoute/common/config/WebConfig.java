package com.saferoute.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.saferoute.common.entity.StudentGuardianEntity.Relationship;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new RelationshipEnumConverter());
    }

    @Bean
    @Primary
    public Module relationshipModule() {
        SimpleModule module = new SimpleModule("RelationshipModule");
        module.addDeserializer(Relationship.class, new JsonDeserializer<Relationship>() {
            @Override
            public Relationship deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (value == null || value.isBlank()) {
                    return null;
                }
                String normalized = value.trim().toLowerCase();
                return switch (normalized) {
                    case "padre", "father" -> Relationship.father;
                    case "madre", "mother" -> Relationship.mother;
                    case "acudiente", "guardian" -> Relationship.guardian;
                    case "otro", "other" -> Relationship.other;
                    default -> Relationship.valueOf(normalized);
                };
            }
        });
        return module;
    }
}