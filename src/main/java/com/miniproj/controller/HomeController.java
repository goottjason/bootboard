package com.miniproj.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

  @GetMapping("/")
  public String home() {

    log.info("index.html로 이동...");

    // GetMapping 방식으로 templates의 index.html로 이동
    return "index";
  }
}
