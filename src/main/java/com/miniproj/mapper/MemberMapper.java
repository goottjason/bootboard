package com.miniproj.mapper;

import com.miniproj.domain.Member;
import com.miniproj.domain.PointWhy;
import org.apache.ibatis.annotations.*;

import com.miniproj.domain.MemberDTO;
import com.miniproj.domain.MemberUpdateDTO;

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


  @Select("select pointScore from pointdef where pointWhy = #{pointWhy}")
  int selectPointScore(PointWhy pointWhy);

  @Insert("insert into pointlog(pointWho, pointWhy, pointScore) values (#{pointWho}, #{pointWhy}, #{pointScore})")
  int insertPointLog(@Param("pointWho") String pointWho, @Param("pointWhy") PointWhy pointWhy, @Param("pointScore") int pointScore);

  @Update("update member set memberPoint = memberPoint + #{memberPoint} where memberId = #{memberId}")
  int updateMemberPoint(@Param("memberId") String memberId, @Param("memberPoint") int memberPoint);

  @Insert("insert into member (memberId, memberPwd, memberName, mobile, email, memberImg, gender) values (#{memberId}, #{memberPwd}, #{memberName}, #{mobile}, #{email}, #{memberImg}, #{gender})")
  int insertMemberByMember(Member member);

  @Select("select * from member where memberId = #{memberId]}")
  Member findMemberById(String memberId);
}

