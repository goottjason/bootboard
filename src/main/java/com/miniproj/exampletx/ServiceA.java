package com.miniproj.exampletx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceA {

  private final TransactionExMapper transactionExMapper;

  @Transactional(rollbackFor = Exception.class) // 기본값 : required
  public void parentServiceA() {
    log.info("p 시작");
    transactionExMapper.insertTablaA(1, "a");
    saveBServiceA();
    log.info("p 끝");
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveBServiceA() {
    log.info("s 시작");
    transactionExMapper.insertTablaB(1, "b");
    transactionExMapper.insertTablaB(2, "bb");
    log.info("s 끝");
  }
}
