package com.miniproj.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MemberUpdateDTO {
  private String memberId;
  private String memberPwd;
  private String email;
  private String memberName;
}
