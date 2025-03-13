package com.CactiEncyclopedia.config;

import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity //enabling PreAuthorize annotation
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/auth/login", "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalog/**").permitAll()
                        .anyRequest().authenticated()
                ).formLogin(form -> form.loginPage("/auth/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureUrl("/auth/login?error")
                        .permitAll())
                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }

    @Bean
    public AuthenticationMetadata authenticationMetadata() {
        return new AuthenticationMetadata();
    }

}
