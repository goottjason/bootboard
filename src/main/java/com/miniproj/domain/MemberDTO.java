package com.miniproj.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class MemberDTO {

  //@NotBlank(message = "아이디는 필수입니다.")
  @Size(min = 4, max = 8, message = "아이디는 4~8자로 작성해주세요.")
  private String memberId;

  //@NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 4, max = 8, message = "비밀번호는 4~8자로 작성해주세요.")
  private String memberPwd;

  @Size(max = 8, message = "이름은 최대 8자까지 가능합니다.")
  private String memberName;

  @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "모바일 형식으로 입력해주세요.(010-XXXX-XXXX)")
  private String mobile;

  @Email(message="이메일 형식으로 작성해주세요.")
  private String email;

  private String registerDate;

  @NotBlank(message = "사진은 필수입니다.")
  private String memberImg;

  private int memberPoint;

  @NotBlank(message = "성별은 필수입니다.")
  private String gender;
}
