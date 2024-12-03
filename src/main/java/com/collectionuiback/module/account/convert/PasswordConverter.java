package com.collectionuiback.module.account.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.crypto.password.PasswordEncoder;

@Converter
public class PasswordConverter implements AttributeConverter<String, String>, ApplicationContextAware {

    private PasswordEncoder passwordEncoder;

    @Override
    public String convertToDatabaseColumn(String s) {
        return s != null ? passwordEncoder.encode(s) : null;
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return s;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
    }
}
