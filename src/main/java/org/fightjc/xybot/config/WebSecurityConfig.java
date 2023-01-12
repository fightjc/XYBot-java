package org.fightjc.xybot.config;

import org.fightjc.xybot.security.JwtAccessDeniedHandler;
import org.fightjc.xybot.security.JwtAuthenticationEntryPoint;
import org.fightjc.xybot.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // cors
        http.cors().configurationSource(corsConfigurationSource());

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();
        http.headers().frameOptions().disable();

        // No session will be created or used by spring security
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
        http.authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // OPTIONS methods
                .antMatchers("/test").permitAll()
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/register").permitAll()
                .antMatchers("/api/**").authenticated()
                // Disallow everything else..
                .anyRequest().denyAll();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Allow resources to be accessed without authentication
//        web.ignoring()
//                .antMatchers("/v2/api-docs")
//                .antMatchers("/swagger-resources/**")
//                .antMatchers("/swagger-ui.html")
//                .antMatchers("/configuration/**")
//                .antMatchers("/webjars/**")
//                .antMatchers("/public");
        super.configure(web);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
//        auth.userDetailsService().passwordEncoder(passwordEncoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(false);
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Content-Type", "Access-Control-Allow-Headers", "Authorization", "X-Requested-With"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTION", "PUT", "DELETE"));
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
