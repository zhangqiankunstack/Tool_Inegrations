package com.rengu.toolintegrations.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springboot全局跨域配置
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-09-03 15:15
 **/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     允许任何域名使用
     允许任何头
     允许任何方法（post、get等）
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);// allowCredential 需设置为true
        corsConfiguration.addAllowedOrigin("*");// 允许域名
        corsConfiguration.addAllowedHeader("*");// 允许头
        corsConfiguration.addAllowedMethod("*");// 允许方法
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
