package com.miniproj.comment;

import com.miniproj.domain.*;
import com.miniproj.exception.CommentNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // @Controller + @ResponseBody
@Slf4j
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/all/{boardNo}/{pageNo}")
  public ResponseEntity<MyResponseWithData> getAllCommentByBoardNo(@PathVariable("boardNo") int boardNo,
                                                                   @PathVariable("pageNo") int pageNo,
                                                                   HttpSession session) {


    log.info("댓글형 : {}번 글의 {} 페이지 댓글 조회... ", boardNo, pageNo);
    // List<CommentVO> result = commentService.selectAllComments(boardNo);

    Member loginMember = (Member) session.getAttribute("loginMember");

    String loginMemberId = (loginMember != null) ? loginMember.getMemberId() : null;

    PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
      .pageNo(pageNo)
      .pagingSize(10)
      .build();

    PagingResponseDTO<CommentVO> responseDTO =
      commentService.getAllCommentsWithPaging(boardNo, pagingRequestDTO);

    Map<String, Object> result = new HashMap<>();
    result.put("responseDTO", responseDTO);
    result.put("loginMemberId", loginMemberId);

    return ResponseEntity.ok(MyResponseWithData.success(result));
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

  @PatchMapping(value = "/{commentNo}")
  public ResponseEntity<MyResponseWithoutData> modifyComment(@PathVariable("commentNo") Integer commentNo, @RequestBody CommentDTO commentDTO, HttpSession session) {

    log.info("댓글 수정 요청: commentNo={}, commentDTO={}", commentNo, commentDTO);

    // 세션의 로그인된 멤버정보를 가져옴
    Member loginMember =  (Member) session.getAttribute("loginMember");

    // 불러올 수 없다면, NOT_LOGGED_IN 반환
    if (loginMember == null) {
      return ResponseEntity.ok(new MyResponseWithoutData(401, null, "NOT_LOGGED_IN"));
    }

    // 해당 댓글의 작성자정보를 가져옴
    CommentVO commentVO = commentService.getCommentByNo(commentNo);
    // 해당 댓글을 불러올 수 없으면, NOT_EXIST 반환
    if (commentVO == null) {
      return ResponseEntity.ok(new MyResponseWithoutData(404, null, "NOT_EXIST"));
    }
    // 로그인된 자와 댓글작성자가 불일치할 경우, FORBIDDEN 반환
    if(!loginMember.getMemberId().equals(commentVO.getCommenter())) {
      return ResponseEntity.ok(new MyResponseWithoutData(403, null, "FORBIDDEN"));
    }

    try {
      commentDTO.setCommentNo(commentNo);
      commentService.updateComment(commentDTO);
      return ResponseEntity.ok(new MyResponseWithoutData(200, null, "SUCCESS"));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(new MyResponseWithoutData(400, null, "FAIL"));
    }
  }

  @DeleteMapping("/{commentNo}")
  public ResponseEntity<MyResponseWithData> deleteComment(@PathVariable("commentNo") Integer commentNo) throws CommentNotFoundException {
    log.info("댓글 삭제 요청: commentNo={}", commentNo);
    int result = commentService.deleteComment(commentNo);

    if(result == 1) {
      return ResponseEntity.ok(MyResponseWithData.success());
    } else {
      throw new CommentNotFoundException("삭제할 댓글이 존재하지 않습니다.");
    }
  }
}
