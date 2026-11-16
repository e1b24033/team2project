package oit.is.team2.team2project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Issue #8 ではログインを扱わないため、全ページを誰でも閲覧できるようにする。
// CSRF対策は既定のまま有効にし、Thymeleafのフォームが自動で付けるトークンで検証する。
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authz -> authz
        .anyRequest().permitAll());
    return http.build();
  }
}
