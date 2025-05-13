package com.miniproj.exampletx;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SampleService {
  private final TransactionExMapper transactionExMapper;

  public void insertWithoutTX() {
    transactionExMapper.insertTablaA(1, "a");
    transactionExMapper.insertTablaA(1, "aa");
  }

  @Transactional
  public void insertWithTX() {
    transactionExMapper.insertTablaA(1, "a");
    transactionExMapper.insertTablaA(1, "aa");
  }

  @Transactional
  public void insertWithTXSyntax() {
    transactionExMapper.insertTablaA(1, "a");
    transactionExMapper.insertTablaB(2, "bb");
  }

  // 예외 발생
  @Transactional
  public void insertWithTxRuntimeEx() {
    transactionExMapper.insertTablaA(1, "a");
    transactionExMapper.insertTablaA(2, "aa");
    throw new RuntimeException("런타임예외");
  }

  // 예외 발생
  @Transactional(rollbackFor = Exception.class)
  public void insertWithTxException() throws Exception {
    transactionExMapper.insertTablaA(1, "a");
    transactionExMapper.insertTablaA(2, "aa");
    throw new Exception("예외");
  }
  @Transactional
  public void parent() {
    transactionExMapper.insertTablaA(1, "a");
    child();
  }

  @Transactional
  public void child() {
    transactionExMapper.insertTablaB(1, "b");
    throw new RuntimeException("런타임 예외");
  }
}
