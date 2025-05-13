package com.miniproj.controller.member;

import ch.qos.logback.core.model.Model;
import com.miniproj.domain.LoginDTO;
import com.miniproj.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@Slf4j
public class MemberController {

  @GetMapping("/login")
  public String login() {
    return "/member/login";
  }

  @PostMapping("/login")
  public void login(LoginDTO loginDTO) {
    log.info("loginDTO : {}", loginDTO);

  }

}
