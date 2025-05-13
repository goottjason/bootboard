package com.miniproj.exampletx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceB {

  private final TransactionExMapper transactionExMapper;

  private final ServiceA serviceA;

  @Transactional(rollbackFor = Exception.class) // 기본값 : required
  public void parentServiceB() {
    log.info("p 시작");
    transactionExMapper.insertTablaA(1, "a");
    serviceA.saveBServiceA();
    log.info("p 끝");
  }


}
