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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
  private final BoardService boardService;
  private final FileUploadUtil fileUploadUtil;

  @GetMapping("/list")
  public String list(Model model) {
    List<HBoardVO> boards = boardService.getAllBoards();
    model.addAttribute("boards", boards);
    return "/board/list";
  }

  @GetMapping("/register")
  public String registerGET(Model model) {
    HBoardDTO board = new HBoardDTO();
    // 빈 객체라도 보내줘야 함 (#fields.hasErrors('title')를 검토하기 때문에)
    model.addAttribute("board", board);
    return "/board/register";
  }

  @PostMapping("/register")
  @ResponseBody
  public ResponseEntity writeBoard(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult) throws IOException {
    // bindingResult는 바로 뒤에 붙여야하고, 에러가 있으면 반환해줌

    log.info("글 저장 요청 - HBoardDTO : {}", board);

    if(bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();

      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }
      log.info("{}", errors);
      return ResponseEntity.badRequest().body(errors);
    }
    log.info("HBoardDTO : {}", board);

    /*for (MultipartFile mpf : board.getMultipartFiles()) {
      log.info("업로드파일이름: {}", mpf.getOriginalFilename());
    }*/

    List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(board.getMultipartFiles());
    board.setUpfiles(upFilesVODTOS);

    boardService.saveBoardWithFiles(board);

    /*for (BoardUpFilesVODTO dto : upFilesVODTOS) {
      log.info("upfile dto = {}", dto);
    }*/

    return ResponseEntity.ok().build();
  }

  @GetMapping("/detail")
  public String boardDetail(@RequestParam(value="boardNo") int boardNo, Model model) {
    log.info("게시판 상태보기 호출... boardNo : {}", boardNo);

    // 글 상세 조회
//    boardService.viewBoardByNo(boardNo);

    List<HBoardDetailInfo> detailInfos = boardService.viewBoardDetailInfoByNo(boardNo);
    model.addAttribute("detail", detailInfos.get(0));
    return "/board/detail";
  }
}
