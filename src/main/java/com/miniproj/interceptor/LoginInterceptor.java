package com.miniproj.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

  // mapping되는 컨트롤러단의 메서드가 호출되기 이전에 request, response를 가로채서 동작
  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

    log.info("====================================== preHandle() 호출");
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Method method = handlerMethod.getMethod();
    log.info("===================================== method : {}", method);
    return true; // false: 해당 컨트롤러단의 메서드로 제어가 돌아가지 않는다.

    // HandlerInterceptor.super.preHandle(request, response, handler);
  }

  // mapping되는 컨트롤러단의 메서드가 호출되어 실행된 후에, request와 response를 가로채서 동작
  @Override
  public void postHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView) throws Exception {
    log.info("====================================== postHandle() 호출");
    // HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }

  // 해당 인터셉터의 prehandle, posthandle의 전 과정이 끝난 후 view가 렌더링 된 후에 request와 response를 가로채서 동작함
  @Override
  public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {
    log.info("====================================== afterCompletion() 호출");
    // HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}
