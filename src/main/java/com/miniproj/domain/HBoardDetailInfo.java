package com.miniproj.domain;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class HBoardDetailInfo {
  private int boardNo;
  private String title;
  private String content;
  private LocalDateTime postDate;
  private int readCount;
  private int ref;
  private int step;
  private int refOrder;
  private String isDelete;

  private MemberVO writer;

  private List<BoardUpFilesVODTO> upfiles;

}
