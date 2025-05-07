package com.miniproj.service;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDetailInfo;
import com.miniproj.domain.HBoardVO;

import java.util.List;

public interface BoardService {

  List<HBoardVO> getAllBoards();

  void saveBoardWithFiles(HBoardDTO board);

  List<HBoardDetailInfo> viewBoardByNo(int boardNo, String ipAddr);

  void saveReply(HBoardDTO reply);

}
