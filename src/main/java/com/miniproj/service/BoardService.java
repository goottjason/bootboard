package com.miniproj.service;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;

import java.util.List;

public interface BoardService {
  // 글 조회
  List<HBoardVO> getAllBoards();

  void saveBoardWithFiles(HBoardDTO board);

  void viewBoardByNo(int boardNo);
}
