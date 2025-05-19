package com.miniproj.exception;

import com.miniproj.domain.MyResponseWithData;
import com.miniproj.domain.MyResponseWithoutData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<MyResponseWithoutData> handleCommentNotFoundException(CommentNotFoundException e) {
    return ResponseEntity.badRequest().body(new MyResponseWithoutData(400, null, e.getMessage()));
  }
}
