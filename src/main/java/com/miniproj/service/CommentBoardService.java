package com.miniproj.service;

import com.miniproj.domain.*;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

public interface CommentBoardService {

  List<HBoardVO> getAllBoards();

  void saveBoardWithFiles(HBoardDTO board);

  List<HBoardDetailInfo> viewBoardByNo(int boardNo, String ipAddr);

  HBoardDTO getBoardDetail(int boardNo);
  void saveReply(HBoardDTO reply);

  List<HBoardDetailInfo> viewBoardDetailInfoByNo(int boardNo);

  List<BoardUpFilesVODTO> viewFilesByBoardNo(int boardNo);

  boolean modifyBoard(@Valid HBoardDTO board) throws IOException;

  PagingResponseDTO<HBoardPageDTO> getList(PagingRequestDTO pagingRequestDTO);

  List<BoardUpFilesVODTO> removeBoard(int boardNo);

  PagingResponseDTO<HBoardPageDTO> getListWithSearch(PagingRequestDTO pagingRequestDTO);
}
