package com.kms.challenges.rbh.initializer;

import com.kms.challenges.rbh.servlet.Login;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Arrays;
import java.util.Set;

/**
 * @author tkhuu.
 */
public class RabbitHolesServletInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    }
}
