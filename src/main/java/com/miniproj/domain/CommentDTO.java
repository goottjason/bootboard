package com.miniproj.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentDTO {
  private int commentNo;
  private String content;
  private String commenter;
  private LocalDateTime regDate;
  private int boardNo;
}
