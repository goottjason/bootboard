package com.miniproj.service;

import com.miniproj.domain.*;
import com.miniproj.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
  
  private final MemberMapper memberMapper;


  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  
  /*@Override
  public int idIsDuplicate(String memberId) {
    return memberMapper.selectIdIsDuplicate(memberId);
  }
  @Override
  public int uniqueEmailCheck(String email) {
    // TODO Auto-generated method stub
    return memberMapper.uniqueEmailCheck(email);
  }
  @Override
  public int insertMember(MemberDTO member) {
    return memberMapper.insertMember(member);
  }

  @Override
  public MemberDTO login(String memberId, String memberPwd) {
    return memberMapper.selectAuthUser(memberId, memberPwd);
  }

  @Override
  public int existingPwdCheck(String memberId, String memberPwd) {
    return memberMapper.selectExistingPwdCheck(memberId, memberPwd);
  }

  @Override
  public int updateInfo(MemberUpdateDTO member) {
    return memberMapper.updateInfo(member);
  }
  @Override
  public MemberDTO selectMemberById(String memberId) {
    return memberMapper.selectMemberById(memberId);
  }*/

  @Override
  public void register(Member member) {
    String encryptedPwd = bCryptPasswordEncoder.encode(member.getMemberPwd());
    member.setMemberPwd(encryptedPwd);

    memberMapper.insertMemberByMember(member);
  }

  @Override
  public Member login(LoginDTO loginDTO) {
    Member member = memberMapper.findMemberById(loginDTO.getMemberId());
    if (member != null && bCryptPasswordEncoder.matches(loginDTO.getMemberPwd(), member.getMemberPwd())) {
      // log.info("member: {}", member);
      return member;
    }
    return null;
  }

  @Override
  public boolean saveAutoLoginInfo(AutoLoginInfo autoLoginInfo) {
    boolean flag = false;
    if (memberMapper.updateAutoLoginInfo(autoLoginInfo) == 1) {
      flag = true;
    }
    return flag;
  }

  @Override
  public Member checkAutoLogin(String savedCookieSesid) {
    return memberMapper.checkAutoLoginMember(savedCookieSesid);
  }

  @Override
  public void clearAuthLoginInfo(String memberId) {
    memberMapper.clearAutoLoginInfo(memberId);
  }


}
