package com.miniproj.interceptor;

import com.miniproj.domain.AutoLoginInfo;
import com.miniproj.domain.Member;
import com.miniproj.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
  private final MemberService memberService;

  // mapping되는 컨트롤러단의 메서드가 호출되기 이전에 request, response를 가로채서 동작
  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
    HttpSession ses = request.getSession();
    boolean result = false;

    log.info("====================================== preHandle() 호출");

    if (handler instanceof HandlerMethod) {
      HandlerMethod handlerMethod = (HandlerMethod) handler;

      // 메서드 이름
      Method method = handlerMethod.getMethod();
      log.info("===== method : {}", method);

      // 클래스 이름
      String controllerName = handlerMethod.getBeanType().getName(); // method.getDeclaringClass().getName();
      log.info("===== controllerName : {}", controllerName);
    }

    String uri = request.getRequestURI();
    String method = request.getMethod();
    log.info("===== uri : {}", uri);
    log.info("===== method : {}", method);
    if (uri.equals("/member/login") && method.equalsIgnoreCase("GET")) {
      // GET 방식 : /member/login 요청은 통과시켜야 한다.
      result = true;
    }
    if (uri.equals("/member/login") && method.equalsIgnoreCase("POST")) {
      // POST 방식 :
      result = true;
    }
    // 자동 로그인 한 유저
    // 쿠키검사하여 자동로그인 쿠키 존재여부
    Cookie autoLoginCookie = WebUtils.getCookie(request, "al");
    log.info("===== autoLoginCookie : {}", autoLoginCookie);
    if (autoLoginCookie != null) { // 쿠키가 있다
      String savedCookieSesid = autoLoginCookie.getValue();
      // DB에서 자동로그인 체크한 유저를 확인하고, 자동로그인 시켜야 함
      Member autoLoginUser = memberService.checkAutoLogin(savedCookieSesid);
      log.info("autoLoginUser : {}", autoLoginUser);
      if (autoLoginUser != null) {
        ses.setAttribute("loginMember", autoLoginUser);
        String destPath = (String) ses.getAttribute("destPath");
        response.sendRedirect((destPath != null) ? destPath : "/");
        result = false;
      }
    } else { // 쿠키가 없고, 로그인하지 않은 경우, 로그인페이지를 보여준다.
      if(ses.getAttribute("loginMember") == null) {
        log.info("쿠키가 없고, 로그인하지 않은 경우 로그인페이지를 보여줌");
        result = true;
      }

    }


    return result; // false: 해당 컨트롤러단의 메서드로 제어가 돌아가지 않는다.

  }

  // mapping되는 컨트롤러단의 메서드가 호출되어 실행된 후에, request와 response를 가로채서 동작
  @Override
  public void postHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView) throws Exception {

    log.info("====================================== postHandle() 호출");

    if (request.getMethod().equalsIgnoreCase("POST")) {
      Map<String, Object> model = modelAndView.getModel();
      Member loginMember = (Member) model.get("loginMember");
      log.info("loginMember : {}", loginMember);

      if (loginMember != null) {
        // 로그인 성공 -> 로그인 멤버정보를 세션에 저장 -> "/"으로
        log.info("postHandle() ... 로그인 성공 ... loginMember : {}", loginMember);
        HttpSession ses = request.getSession();
        ses.setAttribute("loginMember", loginMember);

        // 자동로그인을 체크한 유저라면...
        if (request.getParameter("remember") != null) {
          log.info("자동로그인 체크한 유저입니다");
          saveAutoLoginInfo(request, response);
        }

        String destPath = (String) ses.getAttribute("destPath");
        if (destPath != null) {
          response.sendRedirect(destPath);
        } else {
          response.sendRedirect("/");
        }

      } else {
        // 로그인 실패
        log.info("postHandle() ... 로그인 실패 ...");

        response.sendRedirect("/member/login?status=fail");
      }
    }

  }

  private void saveAutoLoginInfo(HttpServletRequest request, HttpServletResponse response) {
    // 자동로그인을 체크한 유저의 컬럼에 세션값과 만료일을 DB에 저장
    // 자동로그인 쿠키 생성
    String sesId = request.getSession().getId();
    String loginMemberId = ((Member) request.getSession().getAttribute("loginMember")).getMemberId();
    // 만료일 : 일주일
    Timestamp allimit1 = new Timestamp(System.currentTimeMillis() + (7*24*60*60*1000));
    Instant instant = allimit1.toInstant();
    ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("GMT"));
    Instant now = Instant.now();
    ZonedDateTime gmtDateTime = now.atZone(ZoneId.of("GMT"));
    log.info("gmtDateTime : {}", gmtDateTime);
    Timestamp utcAllimit = Timestamp.from(utcDateTime.toInstant());
    log.info("utcAllimit : {}", utcAllimit.toString());

    log.info("localdatetime + 7일 = {}", LocalDateTime.now().plusDays(7));
    log.info("localdatetime + 60초 = {}", LocalDateTime.now().plusSeconds(60));

    if (memberService.saveAutoLoginInfo(new AutoLoginInfo(loginMemberId, sesId, LocalDateTime.now().plusDays(7)))) {

      Cookie autoLoginCookie = new Cookie("al", sesId);
      autoLoginCookie.setMaxAge(7 * 24 * 60 * 60);
      autoLoginCookie.setPath("/");
      response.addCookie(autoLoginCookie);
    }
  }

  // 해당 인터셉터의 prehandle, posthandle의 전 과정이 끝난 후 view가 렌더링 된 후에 request와 response를 가로채서 동작함
  @Override
  public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {
    log.info("====================================== afterCompletion() 호출");
    // HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}
