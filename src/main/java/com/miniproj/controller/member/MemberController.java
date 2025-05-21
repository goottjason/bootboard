package com.miniproj.controller.member;

import com.miniproj.domain.LoginDTO;
import com.miniproj.domain.Member;
import com.miniproj.service.MemberService;
import com.miniproj.util.GetClientIPAddr;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/login")
  public String login() {

    log.info("GetMapping");
    return "/member/login";
  }

  @PostMapping("/login")
  public void login(LoginDTO loginDTO, Model model, HttpServletRequest req) {
    log.info("Controller | loginDTO : {}", loginDTO);

    loginDTO.setIpAddress(GetClientIPAddr.getClientIP(req));

    Member loginMember = memberService.login(loginDTO);

    if(loginMember != null) {
      log.info("Controller | 로그인 성공 | loginMember : {}", loginMember);
      model.addAttribute("loginMember", loginMember); // 로그인멤버 정보가 인터셉터로


      /*session.setAttribute("loginMember", loginMember);
      redirectAttributes.addFlashAttribute("loginMember", loginMember);
      return "redirect:/";*/


    } else {
      log.info("Controller | 로그인 실패");


      /*redirectAttributes.addFlashAttribute("msg", "아이디 또는 비밀번호를 다시 확인해주세요.");
      return "redirect:/member/login";*/

    }

  }


  @GetMapping("/logout")
  public String logout(HttpSession session, HttpServletResponse response) {
    Member loginMember = (Member) session.getAttribute("loginMember");

    if(loginMember != null) {
      // 자동로그인 쿠키 삭제
      Cookie cookie = new Cookie("al", "");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
      memberService.clearAuthLoginInfo(loginMember.getMemberId());

      session.removeAttribute("loginMember");
      session.removeAttribute("destPath");
      session.invalidate(); // 세션 무효화 (새로운 세션 생성되도록 함)
    };
    return "redirect:/";
  }

}
