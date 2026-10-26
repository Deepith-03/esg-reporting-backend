package com.esg.esg_reporting_backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CorsSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private static final String DEPLOYED_VERCEL_ORIGIN =
            "https://esg-reporting-frontend-u2je1k3a4-future-transformation.vercel.app";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Preflight OPTIONS /api/organizations from deployed Vercel frontend should succeed with CORS headers")
    void preflightFromDeployedFrontendShouldSucceed() throws Exception {
        mockMvc.perform(options("/api/organizations")
                        .header("Origin", DEPLOYED_VERCEL_ORIGIN)
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", DEPLOYED_VERCEL_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    @DisplayName("Preflight OPTIONS /api/organizations from localhost:5173 should succeed with CORS headers")
    void preflightFromLocalhostShouldSucceed() throws Exception {
        mockMvc.perform(options("/api/organizations")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("Preflight OPTIONS /api/organizations from preview Vercel domain should succeed via pattern matching")
    void preflightFromPreviewVercelDomainShouldSucceed() throws Exception {
        String previewOrigin = "https://esg-reporting-frontend-git-feat-test.vercel.app";
        mockMvc.perform(options("/api/organizations")
                        .header("Origin", previewOrigin)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", previewOrigin))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("Preflight OPTIONS from unauthorized origin should not receive Access-Control-Allow-Origin")
    void preflightFromUnauthorizedOriginShouldNotReturnAllowOrigin() throws Exception {
        mockMvc.perform(options("/api/organizations")
                        .header("Origin", "https://unauthorized-malicious-site.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Protected endpoint GET /api/organizations still enforces authentication for unauthenticated callers")
    void protectedEndpointStillEnforcesAuthentication() throws Exception {
        mockMvc.perform(get("/api/organizations")
                        .header("Origin", DEPLOYED_VERCEL_ORIGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", DEPLOYED_VERCEL_ORIGIN));
    }

    @Test
    @DisplayName("Admin can authenticate with LocalDevOnly123!")
    void adminCanAuthenticateWithLocalDevPassword() throws Exception {
        mockMvc.perform(get("/api/organizations")
                        .with(httpBasic("admin", "LocalDevOnly123!")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin can authenticate with Admin123!")
    void adminCanAuthenticateWithAdmin123() throws Exception {
        mockMvc.perform(get("/api/organizations")
                        .with(httpBasic("admin", "Admin123!")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DataEntry can authenticate with DataEntry123!")
    void dataEntryCanAuthenticate() throws Exception {
        mockMvc.perform(get("/api/organizations")
                        .with(httpBasic("dataentry", "DataEntry123!")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Invalid credentials return 401 Unauthorized")
    void invalidCredentialsReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/organizations")
                        .with(httpBasic("admin", "IncorrectPassword999!")))
                .andExpect(status().isUnauthorized());
    }
}
