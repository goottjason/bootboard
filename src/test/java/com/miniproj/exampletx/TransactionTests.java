package com.miniproj.exampletx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class TransactionTests {

  @Autowired
  private SampleService sampleService;

  @Autowired
  private ServiceA serviceA;

  @Autowired
  private ServiceB serviceB;


  @Test
  void testInsertWithoutTX() {
    sampleService.insertWithoutTX();
  }

  @Test
  void testInsertWithTX() {
    sampleService.insertWithTX();
  }

  @Test
  void testInsertWithTXSyntax() {
    sampleService.insertWithTXSyntax();
  }

  @Test
  void textInsertWithTxRuntimeEx() {
    sampleService.insertWithTxRuntimeEx();
  }

  @Test
  void textInsertWithTxException() {
    try {
      sampleService.insertWithTxException();
    } catch (Exception e) {
      log.info("예외발생...");
    }
  }

  @Test
  void textParent() {
    sampleService.parent();
  }

  @Test
  void testInsertA() {
    serviceA.parentServiceA();
  }

  @Test
  void testInsertB() {
    serviceB.parentServiceB();
  }
}
