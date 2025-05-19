package com.miniproj.comment;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import com.miniproj.domain.PagingRequestDTO;
import com.miniproj.domain.PagingResponseDTO;

import java.util.List;

public interface CommentService {
  List<CommentVO> selectAllComments(int BoardNo);

  int registerComment(CommentDTO commentDTO);

  PagingResponseDTO<CommentVO> getAllCommentsWithPaging(int boardNo, PagingRequestDTO pagingRequestDTO);

  int updateComment(CommentDTO commentDTO);

  int deleteComment(Integer commentNo);

  CommentVO getCommentByNo(Integer commentNo);
}
