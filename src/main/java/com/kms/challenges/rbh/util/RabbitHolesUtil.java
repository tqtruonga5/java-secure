package com.kms.challenges.rbh.util;

import com.kms.challenges.rbh.annotation.FormField;
import com.kms.challenges.rbh.annotation.MatchWith;
import com.kms.challenges.rbh.annotation.Require;
import com.kms.challenges.rbh.error.ValidationError;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author tkhuu.
 */
public class RabbitHolesUtil {
    public static Properties properties;
    static {
        properties = new Properties();
        try {
            properties.load(RabbitHolesUtil.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Can't load application.properties",e);
        }
    }

    public static String getUploadLocation() {
        return properties.getProperty("upload.location");
    }

    public static String getFileLocation(UploadFile file) {
        return getUploadLocation() + file.getUploader().getId() + "/" + file.getFileName();
    }
    public static <T> T convertParameterMapToBean(
            Class<T> clazz, HttpServletRequest request,
            Map<String, ValidationError> errorMap) throws
            IllegalAccessException, InstantiationException {
        T bean = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation ano : field.getAnnotations()) {
                if (ano instanceof FormField) {
                    FormField formfieldAnnotation = (FormField) ano;
                    String value = request.getParameter(formfieldAnnotation.value());
                    Class fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(bean, value);
                    }
                    if (fieldType == int.class || fieldType == Integer.class) {
                        field.set(bean, Integer.parseInt(value));
                    }
                    if (fieldType == boolean.class || fieldType == Boolean.class) {
                        field.set(bean, Boolean.parseBoolean(value));
                    }
                    if (fieldType == long.class || fieldType == Long.class) {
                        field.set(bean, Long.parseLong(value));
                    }
                    if (fieldType == float.class || fieldType == Float.class) {
                        field.set(bean, Float.parseFloat(value));
                    }
                    for (Annotation ano1 : field.getAnnotations()) {
                        if (ano1 instanceof Require) {
                            if (((Require) ano1).require()) {
                                if (StringUtils.isEmpty(request.getParameter(formfieldAnnotation.value()))) {
                                    errorMap.put(formfieldAnnotation.value(),
                                            new ValidationError(formfieldAnnotation.value(),
                                                    ((Require) ano1).errorMessage()));
                                }
                            }
                        }
                        if (ano1 instanceof MatchWith) {
                            if (request.getParameter(formfieldAnnotation.value()) != null && !request
                                    .getParameter(formfieldAnnotation.value())
                                    .equals(request.getParameter(((MatchWith) ano1).fieldName()))) {
                                errorMap.put(formfieldAnnotation.value(),
                                        new ValidationError(formfieldAnnotation.value(),
                                                ((MatchWith) ano1).errorMessage()));
                            }
                        }
                    }
                }
            }
        }
        return bean;
    }

    public static boolean authenticate(User user, Set<User.ROLE> requireRole) {
        return user != null && requireRole.contains(user.getRole());
    }
}
