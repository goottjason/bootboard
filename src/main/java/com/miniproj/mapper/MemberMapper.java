package com.miniproj.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.miniproj.domain.MemberDTO;
import com.miniproj.domain.MemberUpdateDTO;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper {

  @Insert("insert into member(memberId, memberPwd, memberName, email) values(#{memberId}, sha1(md5(#{memberPwd})), #{memberName}, #{email})")
  int insertMember(MemberDTO member);

  @Select("select exists(select 1 from member where memberId = #{memberId})")
  int selectIdIsDuplicate(String memberId);

  @Select("select * from member where memberId = #{memberId} and memberPwd = sha1(md5(#{memberPwd}))")
  MemberDTO selectAuthUser(@Param("memberId") String memberId, @Param("memberPwd") String memberPwd);


  String selectEmail(@Param("memberId") String memberId);
  int selectExistingPwdCheck(@Param("memberId") String memberId, @Param("memberPwd") String memberPwd);
  int updateInfo(MemberUpdateDTO member);

  @Select("select count(*) from member where email = #{email}")
  int uniqueEmailCheck(String email);


  MemberDTO selectMemberById(String memberId);
  
}
