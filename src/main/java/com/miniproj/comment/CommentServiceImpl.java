package com.miniproj.comment;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import com.miniproj.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentMapper commentMapper;

  @Override
  public List<CommentVO> selectAllComments(int BoardNo) {
    return commentMapper.selectAllComments(BoardNo);
  }

  @Override
  public int registerComment(CommentDTO commentDTO) {
    return commentMapper.insertComment(commentDTO);
  }
}
