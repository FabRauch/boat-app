package com.openwt.boatapp.config;

import static com.openwt.boatapp.config.StaticResourcesWebConfiguration.RESOURCE_PATHS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

class StaticResourcesWebConfigurerTest {

    public StaticResourcesWebConfiguration staticResourcesWebConfiguration;
    private ResourceHandlerRegistry resourceHandlerRegistry;

    @BeforeEach
    void setUp() {
        MockServletContext servletContext = spy(new MockServletContext());
        WebApplicationContext applicationContext = mock(WebApplicationContext.class);
        resourceHandlerRegistry = spy(new ResourceHandlerRegistry(applicationContext, servletContext));
        staticResourcesWebConfiguration = spy(new StaticResourcesWebConfiguration());
    }

    @Test
    void shouldAppendResourceHandlerAndInitializeIt() {
        staticResourcesWebConfiguration.addResourceHandlers(resourceHandlerRegistry);

        verify(resourceHandlerRegistry, times(1)).addResourceHandler(RESOURCE_PATHS);
        for (String testingPath : RESOURCE_PATHS) {
            assertThat(resourceHandlerRegistry.hasMappingForPattern(testingPath)).isTrue();
        }
    }
}
