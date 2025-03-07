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
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests
                        (auth -> auth
                                //.requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/auth/register/**").permitAll()
                                .requestMatchers("/api/v1/auth/users/**").permitAll()
                                .requestMatchers("/api/v1/auth/register-doctor/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/auth/register-gp/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/auth/register-patient/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/auth/authenticate/**").permitAll()

                                // Admins can do everything
                                // Doctors can view all sick leaves, patients, doctors, treatments, appointments and diagnoses
                                // Patients can only view their own info about appointments

                                .requestMatchers(HttpMethod.GET, "/specializations/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/specializations/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/specializations/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/specializations/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/patients/**").hasAnyAuthority("ADMIN", "DOCTOR", "PATIENT")
                                .requestMatchers(HttpMethod.POST, "/patients/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/patients/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/patients/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/doctors/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/doctors/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/doctors/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/doctors/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/general-practitioners/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/general-practitioners/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/general-practitioners/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/general-practitioners/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/diagnoses/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/diagnoses/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/diagnoses/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/diagnoses/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/treatments/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/treatments/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/treatments/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/treatments/**").hasAuthority("ADMIN")

                                // Doctors can edit only the appointments they have been assigned to
                                .requestMatchers(HttpMethod.POST, "/treatments/{treatmentId}/appointments/{appointmentId}/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/treatments/{treatmentId}/appointments/{appointmentId}/**").hasAnyAuthority("ADMIN", "DOCTOR")

                                .requestMatchers(HttpMethod.POST, "/doctor-appointments/{appointmentId}/treatments/{treatmentId}/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/doctor-appointments/{appointmentId}/treatments/{treatmentId}/**").hasAnyAuthority("ADMIN", "DOCTOR")

                                .requestMatchers(HttpMethod.POST, "/doctor-appointments/{appointmentId}/diagnoses/{diagnoseId}/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/doctor-appointments/{appointmentId}/diagnoses/{diagnoseId}/**").hasAnyAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/doctor-appointments/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/doctor-appointments/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.PUT, "/doctor-appointments/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/doctor-appointments/**").hasAnyAuthority("ADMIN", "DOCTOR")

                                .requestMatchers(HttpMethod.GET, "/sick-leaves/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.POST, "/sick-leaves/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.PUT, "/sick-leaves/**").hasAnyAuthority("ADMIN", "DOCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/sick-leaves/**").hasAnyAuthority("ADMIN", "DOCTOR")

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
