package com.miniproj.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SlackNotifierTests {
  @Autowired
  private SlackNotifier notifier;

  @Test
  public void testNotify() {
    notifier.notify("test title!!", "test message!!");
  }

}
