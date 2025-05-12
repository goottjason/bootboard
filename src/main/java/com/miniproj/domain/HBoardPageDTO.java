package com.miniproj.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class HBoardPageDTO {
  private int boardNo;
  private String title;
  private String content;
  private String writer;
  private LocalDateTime postDate;
  private int readCount;
  private int ref;
  private int step;
  private int refOrder;
  private String isDelete;
}
