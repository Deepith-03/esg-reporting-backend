package com.esg.esg_reporting_backend.service.carbon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter client designed for integrating an external Carbon Calculator API
 * (e.g., Climatiq, Carbon Interface, etc.) in the future without changing the UI
 * or service contracts.
 *
 * Reads credentials securely from environment variables:
 * - CARBON_CALCULATOR_API_KEY
 * - CARBON_CALCULATOR_API_URL
 * - CARBON_CALCULATOR_PROVIDER
 *
 * In accordance with project instructions, this does NOT fabricate fake API credentials
 * or a fake integration until actual vendor API documentation and credentials are supplied.
 */
@Component
public class ExternalCarbonCalculatorClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalCarbonCalculatorClient.class);

    private final String apiKey;
    private final String apiUrl;
    private final String providerMode;

    public ExternalCarbonCalculatorClient(
            @Value("${CARBON_CALCULATOR_API_KEY:#{systemEnvironment['CARBON_CALCULATOR_API_KEY']}}") String apiKey,
            @Value("${CARBON_CALCULATOR_API_URL:#{systemEnvironment['CARBON_CALCULATOR_API_URL']}}") String apiUrl,
            @Value("${CARBON_CALCULATOR_PROVIDER:#{systemEnvironment['CARBON_CALCULATOR_PROVIDER'] ?: 'SAMPLE_LOCAL'}}") String providerMode) {
        this.apiKey = (apiKey != null && !apiKey.isBlank()) ? apiKey : "";
        this.apiUrl = (apiUrl != null && !apiUrl.isBlank()) ? apiUrl : "";
        this.providerMode = (providerMode != null && !providerMode.isBlank()) ? providerMode : "SAMPLE_LOCAL";

        if (isConfigured()) {
            log.info("External Carbon Calculator API configured. Provider mode: {}, Target URL: {}", providerMode, apiUrl);
        } else {
            log.info("External Carbon Calculator API credentials not set. Active mode: SAMPLE_LOCAL (Sample reference factors).");
        }
    }

    public boolean isConfigured() {
        return !apiKey.isBlank() && !apiUrl.isBlank() && "EXTERNAL".equalsIgnoreCase(providerMode);
    }

    public String getProviderMode() {
        return providerMode;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public boolean hasApiKey() {
        return !apiKey.isBlank();
    }
}
