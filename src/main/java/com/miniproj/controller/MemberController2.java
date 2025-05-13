/*
package com.miniproj.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miniproj.util.SendEmailService;
import com.miniproj.domain.MemberDTO;
import com.miniproj.domain.MemberUpdateDTO;
import com.miniproj.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController2 {
  private final MemberService memberService;
  private final SendEmailService sendEmailService; // 메일전송담당객체 주입


  @ControllerAdvice
  public class GlobalModelAttributeAdvice {
    @ModelAttribute
    public void addAuthMember(Model model, HttpSession session) {
      model.addAttribute("authMember", session.getAttribute("authMember"));
    }
  }

  @GetMapping("/login")
  public void login() {
  }
  @GetMapping("/signup")
  public void signup() {
  }
  @GetMapping("/mypage")
  public String mypage(HttpSession session) {
//
    if(session.getAttribute("authMember") != null) {
      MemberDTO authMember = (MemberDTO) session.getAttribute("authMember");
      authMember = memberService.login(authMember.getMemberId(), authMember.getMemberPwd());
      return "/member/mypage";
    } else {
      return "redirect:/member/login"; // 로그인 하지 않은 경우 로그인 페이지로 리다이렉트

    }

  }


  @GetMapping("/logout")
  public String logout(HttpSession session) {
    if(session.getAttribute("authMember") != null) {
      session.removeAttribute("authMember");
    }
    return "redirect:/";
  }

  @PostMapping("/existingpwdcheck")
  @ResponseBody
  public String existingPwdCheck(@RequestParam("memberId") String memberId, @RequestParam("memberPwd") String memberPwd) {
    String result = "fail";
    log.info("■ 유저의 아이디 : {}", memberId);
    log.info("■ 기존 비밀번호 : {}", memberPwd);

    if (memberService.existingPwdCheck(memberId, memberPwd) == 1) {
      result = "success";
    }

    return result;
  }


  @PostMapping("/signup")
  public String signupPost(@Valid MemberDTO member, BindingResult bindingResult, RedirectAttributes rttr) throws IOException {
    log.info("회원가입하러가자! registerMember: {}", member);
    String result ="";

    if(bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }
      rttr.addFlashAttribute("signupStatus", errors);
      result = "redirect:/member/signup";
    }

    if(memberService.insertMember(member) == 1) {
      // 1이면 가입완료 >>> index.jsp로 가자.
      // 리다이렉트 할 때 status, success 붙이고 가고싶을때
      // 쿼리스트링 붙어서 감
      rttr.addFlashAttribute("signupStatus", "회원가입을 축하드립니다! 로그인 후 서비스를 이용해주세요.");
      result = "redirect:/member/login";
    } else {
      // 가입 실패하면 다시 회원가입페이지로 이동하게끔!
      rttr.addFlashAttribute("signupStatus", "일시적 장애로 회원가입에 실패하였습니다. 다시 회원가입을 부탁드립니다.");
      result = "redirect:/member/signup";
    }
    return result;
  }

  @PostMapping("/updateinfo")
  public String updateInfo(MemberUpdateDTO updateMember, RedirectAttributes rttr, Model model) {
    MemberDTO authMember = (MemberDTO) model.getAttribute("authMember");

    if(authMember == null) {
      return "redirect:/member/login"; // 로그인 하지 않은 경우 로그인 페이지로 리다이렉트
    } else {
      updateMember.setMemberId(authMember.getMemberId());

      if (memberService.updateInfo(updateMember) == 1) {
        authMember = memberService.selectMemberById(authMember.getMemberId());
        model.addAttribute("authMember", authMember);
        rttr.addFlashAttribute("resultMsg", "회원정보가 변경되었습니다.");
        return "redirect:/member/mypage";

      } else {
        model.addAttribute("authMember", null);
        rttr.addFlashAttribute("resultMsg", "알 수 없는 오류로 업데이트에 실패하였습니다. 다시 시도해주시기 바랍니다.");
        return "redirect:/member/mypage";
      }

    }

  }


  @PostMapping("/login")
  public String loginPost(@RequestParam("memberId") String memberId, @RequestParam("memberPwd") String memberPwd, HttpSession session, RedirectAttributes rttr) {
    String result = "";
    MemberDTO authMember = memberService.login(memberId, memberPwd);

    if(authMember != null) {
      // authMember가 null이 아니면, 로그인 성공
      // 세션에 추가하고, index로 이동
      session.setAttribute("authMember", authMember);
      result = "redirect:/";
    } else {
      // authMember가 null이면, 로그인 실패
      // 아이디 또는 비밀번호가 일치하지 않습니다. 다시 입력해주세요.
      rttr.addFlashAttribute("authFailMsg", "아이디 또는 비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
      result = "redirect:/member/login";
    }

    return result;
  }



  @PostMapping("/idIsDuplicate")
  @ResponseBody
  public ResponseEntity idIsDuplicate(@Valid @RequestBody MemberDTO member, BindingResult bindingResult) throws IOException {

    if (bindingResult.hasFieldErrors("memberId")) {
      String errorMsg = bindingResult.getFieldError("memberId").getDefaultMessage();
      return ResponseEntity.badRequest().body(errorMsg);
    }

    // 중복 검사
    boolean isDuplicate = (memberService.idIsDuplicate(member.getMemberId()) == 1);
//    boolean isDuplicate = memberService.idIsDuplicate(member.getMemberId());
    if (isDuplicate) {
      return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
    } else {
      return ResponseEntity.ok().body("사용 가능한 아이디입니다.");
    }
  }

  @PostMapping("/pwdCheck")
  @ResponseBody
  public ResponseEntity pwdCheck(@Valid @RequestBody MemberDTO member, BindingResult bindingResult) throws IOException {
    if (bindingResult.hasFieldErrors("memberPwd")) {
      String errorMsg = bindingResult.getFieldError("memberPwd").getDefaultMessage();
      return ResponseEntity.badRequest().body(errorMsg);
    }
    return ResponseEntity.ok().body("사용 가능한 비밀번호입니다.");
  }


  @PostMapping("/uniqueEmailCheck")
  @ResponseBody
  public int uniqueEmailCheck(@RequestParam("email") String email) {
    log.info("email : {}", email);
    if(memberService.uniqueEmailCheck(email) == 1) {
      // 1이면 중복
      return 1;
    } else {
      return 0;
    }
  }

  @PostMapping("/callSendEmail")
  public ResponseEntity<String> sendEmailAuthCode(@RequestParam String email, HttpSession session) {
    log.info("tmpEmail: {}", email);
    String authCode = UUID.randomUUID().toString();
    log.info("authCode: {}", authCode);
    String result = "";
    try {
      sendEmailService.sendEmail(email, authCode);
      session.setAttribute("authCode", authCode);
      result = "success";
    } catch (IOException | MessagingException e) {
      e.printStackTrace();
      result = "fail";
    }
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @PostMapping("/checkAuthCode")
  public ResponseEntity<String> checkAuthCode(@RequestParam String memberAuthCode, HttpSession session) {
    // 유저가 보낸 AuthCode와 우리가 보낸 AuthCode가 일치하는지 확인
    log.info("memberAuthCode: {}", memberAuthCode);
    log.info("session에 저장된 코드: {}", session.getAttribute("authCode"));
    String result = "fail";
    if (session.getAttribute("authCode") != null ) {
      String sesAuthCode = (String) session.getAttribute("authCode");
      if (memberAuthCode.equals(sesAuthCode)) {
        result = "success";
      }
    }
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @PostMapping("/clearAuthCode")
  public ResponseEntity<String> clearAuthCode(HttpSession session) {
    // 세션에 저장된 인증코드를 삭제
    if(session.getAttribute("authCode") != null) {
      session.removeAttribute("authCode");
    }
    return new ResponseEntity<String>("success", HttpStatus.OK);
  }
}
*/
