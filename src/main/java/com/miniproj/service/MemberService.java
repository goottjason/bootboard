package com.miniproj.service;

import com.miniproj.domain.MemberDTO;
import com.miniproj.domain.MemberUpdateDTO;

public interface MemberService {
  int idIsDuplicate(String memberId);

  int insertMember(MemberDTO member);

  MemberDTO login(String memberId, String memberPwd);

  int existingPwdCheck(String memberId, String memberPwd);

  int updateInfo(MemberUpdateDTO member);

  int uniqueEmailCheck(String email);
  MemberDTO selectMemberById(String memberId);
}
