package com.miniproj.service;

import com.miniproj.domain.Member;
import com.miniproj.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MemberServiceTests {

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberMapper memberMapper;

  @Test
  public void testRegister() {
    Member member = Member.builder()
      .memberId("user05")
      .memberPwd("1234")
      .memberName("유저04")
      .email("us35501@abc.com")
      .mobile("010-5544-6446")
      .gender("M")
      // .memberImg("user01.png")
      .build();

    memberService.register(member);

    Member member1 = memberMapper.findMemberById(member.getMemberId());
    log.info(member1.toString());
  }

  @Test
  public void testLogin() {
    String tmpId = "user03";
    Member member = memberMapper.findMemberById(tmpId);
    log.info("{}", member);
  }

}
