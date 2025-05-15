package com.miniproj.comment;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;

import java.util.List;

public interface CommentService {
  List<CommentVO> selectAllComments(int BoardNo);

  int registerComment(CommentDTO commentDTO);
}
