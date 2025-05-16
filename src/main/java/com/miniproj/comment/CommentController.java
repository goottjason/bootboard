package com.miniproj.comment;

import com.miniproj.domain.*;
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

  @GetMapping("/all/{boardNo}/{pageNo}")
  public ResponseEntity<MyResponseWithData> getAllCommentByBoardNo(@PathVariable("boardNo") int boardNo, @PathVariable("pageNo") int pageNo) {

    log.info("댓글형 : {}번 글의 {} 페이지 댓글 조회... ", boardNo, pageNo);
    // List<CommentVO> result = commentService.selectAllComments(boardNo);

    PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
      .pageNo(pageNo)
      .pagingSize(10)
      .build();

    PagingResponseDTO<CommentVO> responseDTO =
      commentService.getAllCommentsWithPaging(boardNo, pagingRequestDTO);


    return ResponseEntity.ok(MyResponseWithData.success(responseDTO));
  }

  @PostMapping(value = "/{boardNo}", produces = {"application/json; charset=utf-8"} /*consumes = ""*/ )
  public ResponseEntity<MyResponseWithData> saveComment(@PathVariable("boardNo") int boardNo, @RequestBody CommentDTO commentDTO) {
    log.info("댓글형 : {}번 글의 댓글 작성... ", boardNo);
    log.info("댓글형 : DTO {}... ", commentDTO);
    commentDTO.setBoardNo(boardNo);
    try {
      int result = commentService.registerComment(commentDTO);
      return ResponseEntity.ok(MyResponseWithData.success());
    } catch (Exception e) {
      return ResponseEntity.ok(MyResponseWithData.fail());
      // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
    }
  }



}
