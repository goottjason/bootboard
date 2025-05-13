package com.miniproj.config;

import com.miniproj.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  @Value("${file.upload-base-dir}")
  private String uploadDir;

  private final LoginInterceptor loginInterceptor;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/upload/**")
      .addResourceLocations("file:" + uploadDir);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // WebMvcConfigurer.super.addInterceptors(registry);
    // 로그인 인터셉터
    registry.addInterceptor(loginInterceptor)
      .addPathPatterns("/member/login");
  }
}
