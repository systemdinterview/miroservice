package com.miro.service;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.miro.service.storage.InMemoryWidgetRepository;
import com.miro.service.storage.WidgetRepository;

@Configuration
@EnableCaching
public class AppConfiguration implements WebMvcConfigurer {
		
	@Bean
	public WidgetRepository getWidgetRepositry() {
		return new InMemoryWidgetRepository();
	}
}
