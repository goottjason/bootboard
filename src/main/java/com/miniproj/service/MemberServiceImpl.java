package com.miniproj.service;

import com.miniproj.mapper.MemberMapper;
import org.springframework.stereotype.Service;

import com.miniproj.domain.MemberDTO;
import com.miniproj.domain.MemberUpdateDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  
  private final MemberMapper memberMapper;
  
  @Override
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
  }



}
