package com.miniproj.service;

import com.miniproj.domain.*;

public interface MemberService {
  /*int idIsDuplicate(String memberId);

  int insertMember(MemberDTO member);

  MemberDTO login(String memberId, String memberPwd);

  int existingPwdCheck(String memberId, String memberPwd);

  int updateInfo(MemberUpdateDTO member);

  int uniqueEmailCheck(String email);
  MemberDTO selectMemberById(String memberId);*/


  void register(Member member);

  Member login(LoginDTO loginDTO);

  boolean saveAutoLoginInfo(AutoLoginInfo autoLoginInfo);

  Member checkAutoLogin(String savedCookieSesid);

  void clearAuthLoginInfo(String memberId);
}
