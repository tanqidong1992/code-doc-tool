package com.hngd.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.hngd.doc.config.DocumentProperties;

@SpringBootApplication(exclude = {
        CacheAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@EnableConfigurationProperties(value = {DocumentProperties.class})
public class HnvmnsSwaggerUiApplication {

    private static final Logger logger=LoggerFactory.getLogger(HnvmnsSwaggerUiApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(HnvmnsSwaggerUiApplication.class, args);
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
