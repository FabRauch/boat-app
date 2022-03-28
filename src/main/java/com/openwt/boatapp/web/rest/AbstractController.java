package com.openwt.boatapp.web.rest;

import org.springframework.http.HttpHeaders;

public abstract class AbstractController {

    protected HttpHeaders createAlert(String applicationName, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", message);
        return headers;
    }
}
