package com.miniproj.comment;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody
@Slf4j
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/all/{boardNo}")
  public List<CommentVO> getAllCommentByBoardNo(@PathVariable("boardNo") int boardNo) {

    log.info("댓글형 : {}번 글의 모든 댓글 조회... ", boardNo);
    List<CommentVO> result = commentService.selectAllComments(boardNo);

    return result;
  }

  @PostMapping("/{boardNo}")
  public ResponseEntity<String> saveComment(@PathVariable("boardNo") int boardNo, @RequestBody CommentDTO commentDTO) {
    log.info("댓글형 : {}번 글의 댓글 작성... ", boardNo);
    log.info("댓글형 : DTO {}... ", commentDTO);
    commentDTO.setBoardNo(boardNo);
    try {
      int result = commentService.registerComment(commentDTO);
      return ResponseEntity.ok("success");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
    }
  }
}
