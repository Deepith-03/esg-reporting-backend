
package com.esg.esg_reporting_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        String adminPassword = System.getenv()
                .getOrDefault("ESG_DEMO_PASSWORD", "LocalDevOnly123!");

        var admin = User.withUsername("admin")
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        var dataEntry = User.withUsername("dataentry")
                .password(passwordEncoder.encode("DataEntry123!"))
                .roles("DATA_ENTRY")
                .build();

        var reviewer = User.withUsername("reviewer")
                .password(passwordEncoder.encode("Reviewer123!"))
                .roles("REVIEWER")
                .build();

        var viewer = User.withUsername("viewer")
                .password(passwordEncoder.encode("Viewer123!"))
                .roles("VIEWER")
                .build();

        return new InMemoryUserDetailsManager(
                admin, dataEntry, reviewer, viewer
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth

                // Preflight CORS requests must be permitted before authentication checks
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Admin-only Data Cleanup & Management endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Allow authenticated users to read metric definitions.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/metric-definitions",
                    "/api/metric-definitions/**"
                ).authenticated()

                // Only ADMIN can create, update, or delete metric definitions.
                .requestMatchers(
                    "/api/metric-definitions",
                    "/api/metric-definitions/**"
                ).hasRole("ADMIN")

                // Create reporting periods.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/organizations/*/periods"
                ).hasAnyRole("ADMIN", "DATA_ENTRY")

                // Create organizations.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/organizations"
                ).hasRole("ADMIN")

                // Create metric entries.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/periods/*/entries"
                ).hasAnyRole("ADMIN", "DATA_ENTRY")

                // Upload evidence.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/metric-entries/*/evidence"
                ).hasAnyRole("ADMIN", "DATA_ENTRY")

                // Submit or resubmit reports.
                .requestMatchers(
                    "/api/reporting-periods/*/submit",
                    "/api/reporting-periods/*/resubmit"
                ).hasAnyRole("ADMIN", "DATA_ENTRY")

                // Request changes or approve reports.
                .requestMatchers(
                    "/api/reporting-periods/*/request-changes",
                    "/api/reporting-periods/*/approve"
                ).hasAnyRole("ADMIN", "REVIEWER")

                // Carbon Calculator permissions
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/carbon-calculator/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/carbon-calculator/save"
                ).hasAnyRole("ADMIN", "DATA_ENTRY")

                .requestMatchers(
                    "/api/carbon-calculator/**"
                ).authenticated()

                // All other API endpoints require authentication.
                .requestMatchers("/api/**").authenticated()

                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
