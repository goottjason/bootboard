package com.miniproj.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;



@Aspect
@Component // 빈으로 등록하여 사용하기 위함
@Slf4j
public class BoardLoggingAspect {

  // 계층형게시판 글을 작성할 때마다 동작하도록 해보자
  @Before("execution(* com.miniproj.service.BoardServiceImpl.saveBoardWithFiles(..))")
  public void logBeforeSaveBoard(JoinPoint joinPoint) {
    log.info("■■■ AOP : Before save board - 전체 시그니처 : " + joinPoint.getSignature());
    Object[] args = joinPoint.getArgs();
    for (int i = 0; i < args.length; i++) {
      log.info("■■■ AOP : Before save board - Args[{}] = {}", i, args[i]);
    }
    log.info("■■■ AOP : Before save board - 메서드 : " + joinPoint.getSignature().getName());
    log.info("■■■ AOP : Before save board - 타겟클래스 : " + joinPoint.getTarget().getClass().getName());
    log.info("■■■ AOP : Before save board - joinPoint 종류 : " + joinPoint.getKind());
    log.info("■■■ AOP : Before save board - 프록시 객체 클래스 : " + joinPoint.getThis().getClass().getName());
  }

  @After("execution(* com.miniproj.service.BoardServiceImpl.saveBoardWithFiles(..))")
  public void logAfterSaveBoard(JoinPoint joinPoint) {
    log.info("■■■ AOP : After save board - 전체 시그니처 : " + joinPoint.getSignature());
  }
}
