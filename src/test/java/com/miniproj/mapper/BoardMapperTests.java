package com.miniproj.mapper;



import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import com.miniproj.domain.PagingRequestDTO;
import com.miniproj.domain.PagingResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
@Slf4j
@Rollback(value = false) // 테스트 메서드 실행 후 자동으로 롤백
public class BoardMapperTests {

  @Autowired // Injection
  private BoardMapper boardMapper;

  @Autowired
  private DataSource dataSource;

  @Test
  public void testSelectNow() {
    log.info("now: {}", boardMapper.selectNow());
  }

  @Test
  public void testDataSource() throws SQLException {
    Connection conn = dataSource.getConnection();
    log.info("conn : {}", conn);
  }

  @Test
  @Rollback(value = false) // Transactional 붙여도 롤백되지 않게끔 설정
  public void testInsertNewBoard() {
    //HBoardDTO dto = new HBoardDTO("T title", "T content", "hong1234");
    HBoardDTO dto = HBoardDTO.builder()
      .title("테스트 제목11")
      .content("테스트 내용11")
      .writer("hong1234")
      .build();

    log.info("HBoardDTO: {}", dto);
    // 1. 게시글 insert(boardNo는 자동생성)
    int result = boardMapper.insertNewBoard(dto);
    int boardNo = dto.getBoardNo();
    String title = dto.getTitle();
    log.info("{}", boardNo);
    log.info("{}", dto);
    log.info("insert result ={}", result);

    // 2. ref 값 업데이트
    int resultUpdate = boardMapper.updateRefToBoardNo(boardNo);
    log.info("resultUpdate : {}", resultUpdate);
  }

  @Test
  public void testSelectAllBoard() {
    List<HBoardVO> list = boardMapper.selectAllBoards();
    for (HBoardVO vo : list) {
      log.info("{}", vo);
    }
  }

  @Test
  public void selectBoardDetailInfoByBoardNoTest() {

    log.info("resultMap 결과: {}", boardMapper.selectBoardDetailInfoByBoardNo(1).get(0));

  }

  @Test
  public void insertDummyData() {
    for (int i = 0; i < 1000; i++) {
      HBoardDTO dto = HBoardDTO.builder()
        .title("dummy 제목" + i)
        .content("dummy 내용" + i)
        .writer("hong1234")
        .build();
      boardMapper.insertNewBoard(dto);
      boardMapper.updateRefToBoardNo(dto.getBoardNo());
    }
  }

  @Test
  public void testPaging() {
    PagingRequestDTO dto = PagingRequestDTO.builder()
      .pageNo(2)
      .pagingSize(10)
      .build();
    log.info("pagingRequestDTO: {}", dto);
    log.info("skip = {}", dto.getSkip());

    List<HBoardVO> list = boardMapper.selectList(dto);
    for (HBoardVO vo : list) {
      log.info("{}", vo);
    }

    log.info("TOTAL ::: {}", boardMapper.selectTotalCount());

    int pageNo = 30;
    int pagingSize = 10;
    log.info("{}", (((pageNo - 1) / pagingSize) + 1) * pagingSize );
    int total = boardMapper.selectTotalCount();
    log.info("{}", (int)(Math.ceil(1031/10.0)));

  }

  @Test
  public void testSearch() {

    PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
      .pageNo(1)
      .pagingSize(10)
      // .type("tc")
      .keyword("hong1234")
      .build();

    log.info("pagingRequestDTO: {}", pagingRequestDTO);
    log.info("getTypes = {}", pagingRequestDTO.getTypes());

    List<HBoardVO> HBoardVOS = boardMapper.selectListWithSearch(pagingRequestDTO);
    for (HBoardVO hBoardVO : HBoardVOS) {
      log.info("{}", hBoardVO);
    }

  }
}

