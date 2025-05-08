package com.miniproj.service;

import com.miniproj.domain.*;
import com.miniproj.mapper.BoardMapper;
import com.miniproj.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙은 것을 생성해주기 위해서...
@Slf4j
public class BoardServiceImpl implements BoardService {

  private final BoardMapper boardMapper;
  private final FileUploadUtil fileUploadUtil;

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
  @Transactional(rollbackFor = Exception.class)
  public List<HBoardDetailInfo> viewBoardByNo(int boardNo, String ipAddr) {

    List<HBoardDetailInfo> boardInfo = boardMapper.selectBoardDetailInfoByBoardNo(boardNo);

    // 조회수 처리
    // ipAddr 유저가 boardNo번 글을 조회한 적이 없다 -> 조회 내역 저장 -> 조회수 증가
    int dateDiff = boardMapper.selectDateDiffOrMinusOne(ipAddr, boardNo);

    if(dateDiff == -1) {
      // 최초조회
      if (boardMapper.insertViewLog(ipAddr, boardNo) == 1) {
        if (boardMapper.incrementReadCount(boardNo) == 1) {
          for(HBoardDetailInfo b : boardInfo) {
            b.setReadCount(b.getReadCount() + 1);
          }
        }
      }
    } else if (dateDiff >= 24) {
      boardMapper.updateViewLog(ipAddr, boardNo);
      if (boardMapper.incrementReadCount(boardNo) == 1) {
        for(HBoardDetailInfo b : boardInfo) {
          b.setReadCount(b.getReadCount() + 1);
        }
      }
    }

    return boardInfo;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveReply(HBoardDTO replyBoard) {
    // 답글 저장
    boardMapper.updateRefOrder(replyBoard.getRef(), replyBoard.getRefOrder());

    replyBoard.setStep(replyBoard.getStep() + 1);
    replyBoard.setRefOrder(replyBoard.getRefOrder() + 1);

    if(boardMapper.insertReplyBoard(replyBoard) ==1 ) {
      if (replyBoard.getUpfiles() != null && !replyBoard.getUpfiles().isEmpty()) {
        for(BoardUpFilesVODTO file : replyBoard.getUpfiles()) {
          file.setBoardNo(replyBoard.getBoardNo());
          boardMapper.insertUploadFile(file);
        }
      }
    }
  }

  @Override
  public List<HBoardDetailInfo> viewBoardDetailInfoByNo(int boardNo) {
    return boardMapper.selectBoardDetailInfoByBoardNo(boardNo);
  }

  @Override
  public boolean modifyBoard(HBoardDTO modifyBoard) throws IOException {



    // 1. 게시글 제목, 내용 수정
    if (boardMapper.updateBoard(modifyBoard) == 1) {
      // 2. modifyFileList를 순회하면서 파일 처리 (DB)
      for(BoardUpFilesVODTO file : modifyBoard.getUpfiles()) {
        if(file.getFileStatus() == BoardUpFileStatus.INSERT) {
          // 새로 추가할 파일 insert
          boardMapper.insertUploadFile(file);
        } else if(file.getFileStatus() == BoardUpFileStatus.DELETE) {
          boardMapper.deleteFileByNo(file.getFileNo());

          // 물리적 파일 삭제
          fileUploadUtil.deleteFiles(file.getFilePath());
          if (file.getIsImage()) {
            fileUploadUtil.deleteFiles(file.getThumbFileName());
          }
        }
      }
    }

    return false;

  }
}
