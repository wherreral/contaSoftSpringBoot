package com.hp.contaSoft.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class FrontendConfigAdvice {

    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    @ModelAttribute("apiBaseUrl")
    public String apiBaseUrl() {
        return apiBaseUrl;
    }
}
