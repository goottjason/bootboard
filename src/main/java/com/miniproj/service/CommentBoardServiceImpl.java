package com.miniproj.service;

import com.miniproj.domain.*;
import com.miniproj.mapper.BoardMapper;
import com.miniproj.mapper.CommentBoardMapper;
import com.miniproj.mapper.MemberMapper;
import com.miniproj.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙은 것을 생성해주기 위해서...
@Slf4j
public class CommentBoardServiceImpl implements CommentBoardService {

  private final CommentBoardMapper boardMapper;
  private final MemberMapper memberMapper;
  private final FileUploadUtil fileUploadUtil;

  @Override
  public List<HBoardVO> getAllBoards() {
    return boardMapper.selectAllBoards();
  }

  @Override
  @Transactional(rollbackFor = Exception.class) // 스프링에서 도중에 뭔가 에러나면 자동으로 롤백시켜줌
  public void saveBoardWithFiles(CommBoardDTO board) {

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
    // 3. 포인트 몇 점 주어야 하는지?
    PointWhy pointWhy = PointWhy.WRITE;
    int pointScore = memberMapper.selectPointScore(pointWhy);
    String pointWho = board.getWriter();

    // 4. 누구한테 언제 몇점 주었는지 포인트 로그 기록
    memberMapper.insertPointLog(pointWho, pointWhy, pointScore);

    // 5. 포인트 업데이트
    memberMapper.updateMemberPoint(pointWho, pointScore);

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
    } // 테스트

    return boardInfo;
  }

  @Override
  public HBoardDTO getBoardDetail(int boardNo) {
    return boardMapper.selectBoardDetail(boardNo);
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
  public List<BoardUpFilesVODTO> viewFilesByBoardNo(int boardNo) {
    return boardMapper.selectFilesByBoardNo(boardNo);
  }

  @Override
  public boolean modifyBoard(HBoardDTO modifyBoard) throws IOException {


    log.info("############# update : {}", modifyBoard);
    // 1. 게시글 제목, 내용 수정
    if (boardMapper.updateBoard(modifyBoard) == 1) {
      // 2. modifyFileList를 순회하면서 파일 처리 (DB)
      for(BoardUpFilesVODTO file : modifyBoard.getUpfiles()) {
        if(file.getFileStatus() == BoardUpFileStatus.INSERT) {
          // 새로 추가할 파일 insert
          log.info("***************file : {} ", file);
          boardMapper.insertUploadFile(file);
        } else if(file.getFileStatus() == BoardUpFileStatus.DELETE) {
          boardMapper.deleteFileByNo(file.getFileNo());

          // 물리적 파일 삭제
          fileUploadUtil.deleteFile(file.getFilePath());
          if (file.getIsImage()) {
            fileUploadUtil.deleteFile(file.getThumbFileName());
          }
        }
      }
    }

    return false;

  }

  @Override
  public PagingResponseDTO<HBoardPageDTO> getList(PagingRequestDTO pagingRequestDTO) {
    List<HBoardVO> voList = boardMapper.selectList(pagingRequestDTO);


    List<HBoardPageDTO> dtoList = new ArrayList<>();
    for (HBoardVO vo : voList) {
      log.info("vo : {}", vo);
      HBoardPageDTO dto = HBoardPageDTO.builder()
        .boardNo(vo.getBoardNo())
        .title(vo.getTitle())
        .content(vo.getContent())
        .writer(vo.getWriter())
        .postDate(vo.getPostDate().toLocalDateTime())
        .readCount(vo.getReadCount())
        .ref(vo.getRef())
        .step(vo.getStep())
        .refOrder(vo.getRefOrder())
        .isDelete(vo.getIsDelete())
        .build();
      log.info("dto : {}", dto);
      dtoList.add(dto);
    }

    int total = boardMapper.selectTotalCount();
    return PagingResponseDTO.<HBoardPageDTO>allInfo()
      .pagingRequestDTO(pagingRequestDTO)
      .dtoList(dtoList)
      .total(total)
      .build();
  }

  @Override
  @Transactional(rollbackFor = Exception.class) // 에러가 나면 자동롤백을 해줘라
  public List<BoardUpFilesVODTO> removeBoard(int boardNo) {
    // 1) 실제 파일을 하드에서도 삭제해야하므로, 삭제 하기 전 첨부파일 정보 조회
    List<BoardUpFilesVODTO> fileList = boardMapper.selectFilesByBoardNo(boardNo);

    log.info("fileList : {}", fileList == null); // 비어있으면 빈배열로 옴
    log.info("fileList : {}", fileList.isEmpty()); // 비어있으면 빈배열로 옴

    // 2) boardNo번 글의 첨부파일 정보를 DB에서 삭제
    boardMapper.deleteAllBoardUpFiles(boardNo);

    // 3) boardNo번 글을 삭제 (soft delete 방식 : isDelete = "Y"로 업데이트)
    boardMapper.deleteBoardByBoardNo(boardNo);

    if (!fileList.isEmpty()) {
      return fileList;
    } else {
      return null;
    }
  }

  @Override
  public PagingResponseDTO<HBoardPageDTO> getListWithSearch(PagingRequestDTO pagingRequestDTO) {
    List<HBoardVO> voList = boardMapper.selectListWithSearch(pagingRequestDTO);

    List<HBoardPageDTO> dtoList = new ArrayList<>();
    for (HBoardVO vo : voList) {
      log.info("vo : {}", vo);
      HBoardPageDTO dto = HBoardPageDTO.builder()
        .boardNo(vo.getBoardNo())
        .title(vo.getTitle())
        .content(vo.getContent())
        .writer(vo.getWriter())
        .postDate(vo.getPostDate().toLocalDateTime())
        .readCount(vo.getReadCount())
        .ref(vo.getRef())
        .step(vo.getStep())
        .refOrder(vo.getRefOrder())
        .isDelete(vo.getIsDelete())
        .build();
      log.info("dto : {}", dto);
      dtoList.add(dto);
    }

    int total = boardMapper.selectTotalCountWithSearch(pagingRequestDTO);
    return PagingResponseDTO.<HBoardPageDTO>allInfo()
      .pagingRequestDTO(pagingRequestDTO)
      .dtoList(dtoList)
      .total(total)
      .build();
  }

  @Override
  public int likeBoard(int boardNo, String who) {
    return boardMapper.insertLike(boardNo, who);
  }

  @Override
  public int countLikes(int boardNo) {
    return boardMapper.selectCountLikes(boardNo);
  }

  @Override
  public boolean countHasLikedById(int boardNo, String memberId) {
    if(boardMapper.selectCountHasLikedById(boardNo, memberId) == 1) {
      return true;
    }
    return false;
  }

  @Override
  public List<String> selectTopLikeMembers(int boardNo, int limit) {
    return boardMapper.selectTopLikeMembers(boardNo, limit);
  }

  @Override
  public int dislikeBoard(int boardNo, String who) {
    return boardMapper.deleteLike(boardNo, who);
  }

  @Override
  public String findBoardWriterByNo(int boardNo) {
    return boardMapper.selectBoardWriterByNo(boardNo);
  }

  @Override
  public BoardUpFilesVODTO getUploadFileInfo(int fileNo) {
    return boardMapper.selectUploadFileById(fileNo);
  }

}
