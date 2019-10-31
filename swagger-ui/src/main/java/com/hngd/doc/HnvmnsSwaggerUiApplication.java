package com.hngd.doc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication(scanBasePackages = {"com.hngd"})
public class HnvmnsSwaggerUiApplication {

	private static final Logger logger=LoggerFactory.getLogger(HnvmnsSwaggerUiApplication.class);
	public static void main(String[] args) {
		File file=new File(".");
		logger.info("before workdir:{}",file.getAbsoluteFile().getAbsolutePath());
		SpringApplication.run(HnvmnsSwaggerUiApplication.class, args);
		file=new File(".");
		logger.info("after workdir:{}",file.getAbsoluteFile().getAbsolutePath());
	}
	@Bean
	 public FilterRegistrationBean<CorsFilter> corsFilter() {
	     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	     CorsConfiguration config = new CorsConfiguration();
	     config.setAllowCredentials(true);
	     config.addAllowedOrigin("*");
	     config.addAllowedHeader("*");
	     config.addAllowedMethod("*");
	     source.registerCorsConfiguration("/**", config);
	     FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
	     bean.setOrder(0);
	     return bean;
	 }

}
