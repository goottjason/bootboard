package com.miniproj.mapper;

import com.miniproj.domain.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentBoardMapper {

  @Select("select now()")
  String selectNow();

  @Insert("insert into hboard(title, content, writer, boardType) values(#{title}, #{content}, #{writer}, 'cboard')")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertNewBoard(HBoardDTO hBoardDTO);

  @Update("update hboard set ref = #{boardNo} where boardNo = #{boardNo} and boardType = 'cboard'")
  int updateRefToBoardNo(@Param("boardNo") int boardNo);


  // MyBatis는 SQL 결과를 자바의 List로 반환할 때 기본적으로 java.util.ArrayList를 사용
  @Select("select * from hboard where boardType = 'cboard' order by ref desc, refOrder asc")
  List<HBoardVO> selectAllBoards();

  List<HBoardVO> selectListWithSearch(PagingRequestDTO pagingRequestDTO);


  // 파일 저장
  @Update(value = """
    insert into boardUpfiles (boardNo, originalFileName, newFileName, thumbFileName, isImage, ext, size, base64, filePath)
    values (#{boardNo}, #{originalFileName}, #{newFileName}, #{thumbFileName}, #{isImage}, #{ext}, #{size}, #{base64}, #{filePath})
    """)
  int insertUploadFile(BoardUpFilesVODTO file);

  // resultMap 이용한 조인문 실행
  List<HBoardDetailInfo> selectBoardDetailInfoByBoardNo(int boardNo);

  // 게시글 상세 조회 + 조회수 처리 관련 쿼리문
  @Select("""
          select ifnull((select timestampdiff(hour, readWhen, now()) from boardreadlog 
          where readWho = #{readWho} and boardNo = #{boardNo}), -1)
          """)
  int selectDateDiffOrMinusOne(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Insert("insert into boardreadlog (readWho, boardNo) values(#{readWho}, #{boardNo})")
  int insertViewLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Update("update boardreadlog set readWhen = now() where readWho = #{readWho} and boardNo = #{boardNo}")
  void updateViewLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Update("update hboard set readCount = readCount + 1 where boardNo = #{boardNo}")
  int incrementReadCount(@Param("boardNo") int boardNo);


  @Update("update hboard set refOrder = refOrder + 1 where ref = #{ref} and refOrder > #{refOrder} and boardType = 'hboard'")
  void updateRefOrder(@Param("ref") int ref, @Param("refOrder") int refOrder);

  @Insert("insert into hboard(title, content, writer, ref, step, refOrder, boardType) values(#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder}, 'cboard')")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertReplyBoard(HBoardDTO replyBoard);

  @Update("update hboard set title = #{title}, content=#{content} where boardNo = #{boardNo}")
  int updateBoard(HBoardDTO modifyBoard);

  @Delete("delete from boardupfiles where fileNo = #{fileNo}")
  int deleteFileByNo(int fileNo);

  @Select("select boardNo, title, content, writer, postDate, readCount, ref, step, refOrder from hboard where boardNo = #{boardNo}")
  HBoardDTO selectBoardDetail(int boardNo);

  @Select("select * from boardupfiles where boardNo = #{boardNo}")
  List<BoardUpFilesVODTO> selectFilesByBoardNo(int boardNo);

  @Select("select * from hboard where boardType = 'cboard' order by ref desc, refOrder asc limit #{skip}, #{pagingSize}")
  List<HBoardVO> selectList(PagingRequestDTO pagingRequestDTO);

  @Select("select count(*) from hboard where boardType = 'cboard'")
  int selectTotalCount();

  @Delete("DELETE FROM boardupfiles WHERE boardNo = #{boardNo}")
  void deleteAllBoardUpFiles(int boardNo);

  @Update("UPDATE hboard SET isDelete = 'Y', title = '', content='' WHERE boardNo = #{boardNo}")
  void deleteBoardByBoardNo(int boardNo);


  // 검색된 총 글의 개수
  int selectTotalCountWithSearch(PagingRequestDTO pagingRequestDTO);

}
