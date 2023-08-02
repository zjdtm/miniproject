package com.example.miniproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedMethod("*"); // 허용할 Header
		config.addAllowedHeader("*"); // 허용할 Http Method
		config.addExposedHeader(HttpHeaders.SET_COOKIE); // Set-Cookie 만 허용
		source.registerCorsConfiguration("/api/**", config);

		return new CorsFilter(source);
	}

}
