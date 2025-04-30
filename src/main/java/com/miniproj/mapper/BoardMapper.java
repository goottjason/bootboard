package com.miniproj.mapper;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDetailInfo;
import com.miniproj.domain.HBoardVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

  @Select("select now()")
  String selectNow();

  @Insert("insert into hboard(title, content, writer) values(#{title}, #{content}, #{writer})")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertNewBoard(HBoardDTO hBoardDTO);

  @Update("update hboard set ref=#{boardNo} where boardNo = #{boardNo}")
  int updateRefToBoardNo(@Param("boardNo") int boardNo);

  @Select("select * from hboard order by boardNo desc")
  List<HBoardVO> selectAllBoards();

  // 파일 저장
  @Update("""
          insert into boardUpfiles (
            boardNo, originalFileName, newFileName, thumbFileName, isImage, ext, size, base64, filePath
            ) values (
            #{boardNo}, #{originalFileName}, #{newFileName}, #{thumbFileName}, #{isImage}, #{ext}, #{size}, #{base64}, #{filePath}
            )
          """)
  int insertUploadFile(BoardUpFilesVODTO file);

  // resultMap 이용한 조인문 실행
  List<HBoardDetailInfo> selectBoardDetailInfoByBoardNo(int boardNo);

}
