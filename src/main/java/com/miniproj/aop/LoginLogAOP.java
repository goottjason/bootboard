package com.miniproj.aop;

import com.miniproj.domain.LoginDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 로그인기록 관리 : 로그인시간, 멤버아이디, IP주소, 로그인 성공여부
 * c:/upload/logs/log_yyyyMMdd.csv
 */
@Aspect
@Component
@Slf4j
public class LoginLogAOP {

  // 스태틱은 먼저 올라가기때문에 Value 어노테이션으로 값이 안 넣어짐
  @Value("${file.upload-base-dir}")
  private String baseDir;
  private String logDir; // logs/log_yyyyMMdd.csv

  @PostConstruct // Spring에서 Bean을 생성하고, 의존성 주입 직후, 자동으로 실행되는 메서드에 불티는 어노테이션
  public void makeLogDir() {
    this.logDir = baseDir + "logs";
  }

  @Around("execution(* com.miniproj.service.MemberServiceImpl.login(..))")
  public Object logLoginAttempt(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("■■■ AOP : 로그인 시도 감지");
    Object[] args = joinPoint.getArgs();
    if (args.length == 0 || !(args[0] instanceof LoginDTO)) {
      log.warn("■■■ AOP : loginDTO가 아님({})", args[0]);
    }
    LoginDTO loginDTO = (LoginDTO) args[0];
    String memberId = loginDTO.getMemberId();
    String ipAddress = loginDTO.getIpAddress();
    String loginTime = LocalDateTime.now().toString();

    StringBuilder logContent = new StringBuilder();
    logContent.append(loginTime).append(",")
      .append(ipAddress).append(",")
      .append(loginDTO.getMemberId()).append(",");

    log.info("■■■ AOP : 로그인시도 정보 : memberId({}), ipAddress({}), loginTime({})", memberId, ipAddress, loginTime);

    Object result = joinPoint.proceed();
    log.info("■■■ AOP : result({})", result);
    if (result == null) {
      log.info("■■■ AOP : 로그인 실패 = {}", memberId);
      logContent.append("login fail");
    } else {
      log.info("■■■ AOP : 로그인 성공 = {}", memberId);
      logContent.append("login success");
    }

    log.info("■■■ AOP : baseDir = {}", baseDir);
    log.info("■■■ AOP : 로그저장경로 = {}", logDir);

    String yyyyMMdd = getDatePath();
    String fileName = "logs_" + yyyyMMdd + ".csv";

    // 파일명
    writeLog(fileName, logContent.toString());

    return result; // target 메서드 수행 후, 반환되는 값을 다시 컨트롤러 단으로 돌려줌
  }

  private void writeLog(String fileName, String logContent) {
    File dir = new File(logDir);
    if(!dir.exists()) {
      dir.mkdirs();
    }
    File file = new File(dir, fileName);
    try (FileWriter fw = new FileWriter(file, true)) {
      fw.write(logContent + "\n");
      fw.flush();
    } catch (IOException e) {
      log.error("■■■ AOP : 로그인 로그 저장 실패 = {}", e.getMessage());
    }


  }

  private String getDatePath() {
    LocalDate localDate = LocalDate.now();
    return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }
}
