package com.openwt.boatapp.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.h2.server.web.WebServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CorsFilter;

/**
 * Unit tests for the {@link WebConfigurer} class.
 */
class WebConfigurerTest {

    private WebConfigurer webConfigurer;

    private MockServletContext servletContext;

    private MockEnvironment env;

    @BeforeEach
    public void setup() {
        servletContext = spy(new MockServletContext());
        doReturn(mock(FilterRegistration.Dynamic.class)).when(servletContext).addFilter(anyString(), any(Filter.class));
        doReturn(mock(ServletRegistration.Dynamic.class)).when(servletContext).addServlet(anyString(), any(Servlet.class));

        env = new MockEnvironment();

        webConfigurer = new WebConfigurer(env);
    }

    @Test
    void shouldStartUpProdServletContext() throws ServletException {
        env.setActiveProfiles(Constants.SPRING_PROFILE_PRODUCTION);

        assertThatCode(() -> webConfigurer.onStartup(servletContext)).doesNotThrowAnyException();
        verify(servletContext, never()).addServlet(eq("H2Console"), any(WebServlet.class));
    }

    @Test
    void shouldStartUpDevServletContext() throws ServletException {
        env.setActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);

        assertThatCode(() -> webConfigurer.onStartup(servletContext)).doesNotThrowAnyException();
        verify(servletContext).addServlet(eq("H2Console"), any(WebServlet.class));
    }

    @Test
    void shouldCustomizeServletContainer() {
        env.setActiveProfiles(Constants.SPRING_PROFILE_PRODUCTION);
        UndertowServletWebServerFactory container = new UndertowServletWebServerFactory();
        webConfigurer.customize(container);
        assertThat(container.getMimeMappings().get("abs")).isEqualTo("audio/x-mpeg");
        assertThat(container.getMimeMappings().get("html")).isEqualTo("text/html");
        assertThat(container.getMimeMappings().get("json")).isEqualTo("application/json");
        if (container.getDocumentRoot() != null) {
            assertThat(container.getDocumentRoot()).isEqualTo(new File("target/classes/static/"));
        }
    }

    @Test
    void shouldCorsFilterOnOtherPath() throws Exception {
        CorsFilter corsFilter = webConfigurer.corsFilter("*", "GET,POST,PUT,DELETE", "*", true, "", 1800L);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController()).addFilters(corsFilter).build();

        mockMvc
            .perform(get("/test/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void shouldCorsFilterDeactivatedForEmptyAllowedOrigins() throws Exception {
        CorsFilter corsFilter = webConfigurer.corsFilter("", "GET,POST,PUT,DELETE", "*", true, "", 1800L);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController()).addFilters(corsFilter).build();

        mockMvc
            .perform(get("/api/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isForbidden())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
