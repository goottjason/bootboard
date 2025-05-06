package com.miniproj.controller;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDetailInfo;
import com.miniproj.domain.HBoardVO;
import com.miniproj.service.BoardService;
import com.miniproj.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 웹요청을 처리하는 컨트롤러임을 명시, 없으면 웹 요청이 이 클래스의 메서드로 오지 않음
@Controller
// private static final Logger log = LoggerFactory.getLogger(클래스명.class);를 자동으로 만들어 줌
// 없으면 로그를 직접 사용하려면 Logger 객체를 직접 선언하고 초기화해야 함
@Slf4j
// 롬복에서 제공, 주입받은 BoardService, FileUploadUtil에 대해 생성자를 자동으로 생성해 줌
@RequiredArgsConstructor
// 클래스에 붙이면, 해당 클래스의 모든 메서드에 기본 경로(/board)가 붙음
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;
  private final FileUploadUtil fileUploadUtil;

  /* @RequiredArgsConstructor 롬복 애노테이션 덕분에 생성자 불필요해짐
  @Autowired
  public BoardController(BoardService boardService, FileUploadUtil fileUploadUtil) {
    this.boardService = boardService;
    this.fileUploadUtil = fileUploadUtil;
  }*/



  @GetMapping("/list")
  public String list(Model model) {
    // select * ... 로 조회하여 HBoardVO 객체를 담은 리스트를 반환받음
    List<HBoardVO> boards = boardService.getAllBoards();

    // 반환받은 리스트를 컨트롤러에서 뷰(화면)로 데이터를 전달할 때 사용
    model.addAttribute("boards", boards);

    return "/board/list";
  }

  @GetMapping("/register")
  public String registerGET(Model model) {

    // 빈 객체라도 보내줘야 함 (#fields.hasErrors('title')를 검토하기 때문에)
    HBoardDTO board = new HBoardDTO();

    // 빈 객체라도 컨트롤러에서 뷰(화면)로 데이터를 전달
    model.addAttribute("board", board);

    return "/board/register";
  }

  @PostMapping("/register")
  @ResponseBody // REST API 방식 (반환값이 json 등으로 직접 전송됨)
  // @ModelAttribute("board") --> th:object="${board}로 보낸 폼 데이터를 DTO 객체로 자동 바인딩
  // @Valid --> 각 필드를 유효성 검사하여 실패한 필드와 메시지가 BindingResult 객체에 저장됨
  // bindingResult는 바로 뒤에 붙여야하고, 에러가 있으면 반환해줌
  public ResponseEntity writeBoard(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult) throws IOException {

    log.info("글 저장 요청 - HBoardDTO : {}", board);

    // hasErrors() : 전체 오류가 있는지 여부 반환하는 메서드
    if(bindingResult.hasErrors()) {

      Map<String, String> errors = new HashMap<>();

      // getFieldErrors() : 오류가 있는 각 필드의 FieldError 객체를 반환하는 메서드
      for (FieldError error : bindingResult.getFieldErrors()) {

        // getField() : 필드명을 반환하는 메서드
        // getDefaultMessage() : 기본 오류 메시지를 반환하는 메서드
        errors.put(error.getField(), error.getDefaultMessage());

      }
      log.info("{}", errors);

      // 컨트롤러에서 문자열, 객체 등을 반환하면 Spring이 자동으로 HTTP 응답을 만들어줌
      // ResponseEntity를 사용하면 개발자가 응답의 상태코드(200, 400, 404), 헤더, 본문을 직접 지정 가능
      // badRequest() : 400 응답을 보냄
      // 응답 본문에 errors 객체를 보냄 (JSON 등으로 클라이언트(브라우저)에 전달)
      return ResponseEntity.badRequest().body(errors);
    }


    log.info("HBoardDTO : {}", board);

    // 롬복이 getter 메서드로 첨부파일 리스트를 saveFiles() 메서드에 전달
    // saveFile() 메서드에서 하는 일
    // --> 현재 날짜로 디렉토리를 생성, 파일 저장,
    // --> 이미지이면 썸네일 이미지 및 Base64 저장, DTO를 담은 리스트 반환
    List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(board.getMultipartFiles());

    // 실제 저장된 BoardUpFilesVODTO를 담은 리스트를 HBoardDTO에 담음
    board.setUpfiles(upFilesVODTOS);

    // DTO를 저장
    boardService.saveBoardWithFiles(board);

    // .build()만 호출하면, 본문이 없는 200 OK 응답이 만들어짐
    return ResponseEntity.ok().build();
  }

  @GetMapping("/detail")
  public String boardDetail(@RequestParam(value="boardNo") int boardNo, Model model) {
    log.info("게시판 상태보기 호출... boardNo : {}", boardNo);

    // 글 상세 조회
    List<HBoardDetailInfo> detailInfos = boardService.viewBoardDetailInfoByNo(boardNo);

    if (detailInfos.get(0).getUpfiles() == null) {
      detailInfos.get(0).setUpfiles(Collections.emptyList());
    }
    log.info("{}", detailInfos.get(0));
    model.addAttribute("detail", detailInfos.get(0));
    return "/board/detail";
  }
}
