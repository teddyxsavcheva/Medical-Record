package com.nbu.medicalrecordf104458.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // Applies the configuration to all requests; adjust as needed
                .csrf(AbstractHttpConfigurer::disable) // Explicitly disable CSRF
                .authorizeHttpRequests
                        (auth -> auth

                                // Admins can access everything

                                // Doctors can view all sick leaves, patients, doctors, treatments, and appointments
                                .requestMatchers(HttpMethod.GET, "/patients/**").hasAnyRole("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.GET, "/doctors/**").hasAnyRole("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.GET, "/sick-leaves/**").hasAnyRole("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.GET, "/appointments/**").hasAnyRole("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.GET, "/treatments/**").hasAnyRole("ADMIN", "DOCTOR")

                                // Doctors can edit sick leaves and appointments only for the patients they have treated
//                                .requestMatchers(HttpMethod.POST, "/appointments/**")
//                                .access("hasRole('DOCTOR') " +
//                                        "and @patientSecurityService.canEditAppointment(authentication, #appointmentId)")
//                                .requestMatchers(HttpMethod.PUT, "/appointments/**")
//                                .access("hasRole('DOCTOR') " +
//                                        "and @patientSecurityService.canEditAppointment(authentication, #appointmentId)")
//                                .requestMatchers(HttpMethod.DELETE, "/appointments/**")
//                                .access("hasRole('DOCTOR') " +
//                                        "and @patientSecurityService.canEditAppointment(authentication, #appointmentId)")
//                                .requestMatchers(HttpMethod.POST, "/sick-leaves/**")
//                                .access("hasRole('DOCTOR') " +
//                                        "and @patientSecurityService.canEditSickLeave(authentication, #sickLeaveId)")
//                                .requestMatchers(HttpMethod.PUT, "/sick-leaves/**")
//                                .access("hasRole('DOCTOR') " +
//                                        "and @patientSecurityService.canEditSickLeave(authentication, #sickLeaveId)")
//                                .requestMatchers(HttpMethod.DELETE, "/sick-leaves/**")
//                                .access("hasRole('DOCTOR') " +
//                                        "and @patientSecurityService.canEditSickLeave(authentication, #sickLeaveId)")
//

                                // Patients can view only their own information



                                // All other requests require authentication (for the user to be logged in)
                                .anyRequest().authenticated()
                        )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
                )
                .authenticationProvider(authenticationProvider) // Custom authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

}
