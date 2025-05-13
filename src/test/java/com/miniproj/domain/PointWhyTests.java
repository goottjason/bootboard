package com.miniproj.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class PointWhyTests {

  @Test
  public void test() {
    System.out.println(PointWhy.values());
    for (PointWhy pointWhy : PointWhy.values()) {
      System.out.println(pointWhy);
      System.out.println(pointWhy.toString());
      System.out.println(pointWhy.name());
      System.out.println(pointWhy.getScore());
    }
    PointWhy login = PointWhy.valueOf("LOGIN");
    System.out.println("==========================");
    System.out.println("ordinal : " + login.ordinal());

    if (login == PointWhy.LOGIN) {
      System.out.println(login);
      System.out.println(PointWhy.LOGIN);
    }
    PointWhy reason = PointWhy.WRITE;
    if(reason.equals(PointWhy.WRITE)) {
      System.out.println(reason);
      System.out.println(PointWhy.WRITE);
    }

    if (reason.name().equals(PointWhy.WRITE)) {
      System.out.println(reason);
      System.out.println(PointWhy.WRITE);
    }
  }
}
