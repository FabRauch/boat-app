package com.openwt.boatapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Boat App.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {}
