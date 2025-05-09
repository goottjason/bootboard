package com.miniproj.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class HBoardDTO {


  private int boardNo;

  // null, 빈 문자열(""), 공백만 있는 문자열(" ") 모두 허용하지 않음
  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 20, message = "제목은 20자 이내로 작성해 주세요")
  private String title;

  @Size(max = 2000, message = "내용은 2000자 이내로 작성해 주세요")
  private String content;
  private LocalDateTime postDate;
  private String writer;

  // 업로드된 파일 수집
  private List<MultipartFile> multipartFiles;

  // 실제 저장된 파일 정보
  private List<BoardUpFilesVODTO> upfiles;

  private int ref;
  private int step;
  private int refOrder;


}
