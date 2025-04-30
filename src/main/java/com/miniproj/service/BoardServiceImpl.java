package com.miniproj.service;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import com.miniproj.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙은 것을 생성해주기 위해서...
public class BoardServiceImpl implements BoardService {

  private final BoardMapper boardMapper;

  @Override
  public List<HBoardVO> getAllBoards() {
    return boardMapper.selectAllBoards();
  }

  @Override
  @Transactional // 스프링에서 도중에 뭔가 에러나면 자동으로 롤백시켜줌
  public void saveBoardWithFiles(HBoardDTO board) {
    // 1. 게시글 저장
    boardMapper.insertNewBoard(board);
    boardMapper.updateRefToBoardNo(board.getBoardNo());

    // 2. 첨부파일 저장
    if(board.getUpfiles() != null && !board.getUpfiles().isEmpty()) {
      for(BoardUpFilesVODTO file : board.getUpfiles()) {
        file.setBoardNo(board.getBoardNo()); // FK 값
        boardMapper.insertUploadFile(file);
      }
    }
  }

  @Override
  public void viewBoardByNo(int boardNo) {

  }
}
