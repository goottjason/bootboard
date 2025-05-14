package com.miniproj.controller;

import com.miniproj.domain.*;
import com.miniproj.service.BoardService;
import com.miniproj.util.FileUploadUtil;
import com.miniproj.util.GetClientIPAddr;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;


/*컨트롤러단에서 하는 일들
  1) URI 매핑
  2) view단에서 보내준 매개변수 수정
  3) 데이터베이스에 대한 CRUD를 수행하기 위해 service단의 메서드 호출
  4) 부가적으로 HttpServletRequest request, HttpServletResponse, HttpSession 등의 Servlet 객체를 이용 가능
     (이 객체를 이용해 구현할 기능이 있으면 컨트롤러에서 구현함)*/



@Controller // 웹요청을 처리하는 컨트롤러임을 명시, 없으면 웹요청이 이 클래스의 메서드로 안 옴
@Slf4j // private static final Logger log = LoggerFactory.getLogger(클래스명.class) 자동생성
@RequiredArgsConstructor // 롬복에서 제공, 주입받은 BoardService, FileUploadUtil에 대해 생성자 자동 생성
@RequestMapping("/board") // 클래스에 붙이면, 해당 클래스의 모든 메서드에 기본 경로가 붙음
public class BoardController {

  /*롬복 애노테이션(@RequiredArgsConstructor) 덕분에 아래의 생성자 생성이 불필요해짐
    @Autowired
    public BoardController(BoardService boardService, FileUploadUtil fileUploadUtil) {
      this.boardService = boardService;
      this.fileUploadUtil = fileUploadUtil;
    }*/

  private final BoardService boardService;
  private final FileUploadUtil fileUploadUtil;
  private List<BoardUpFilesVODTO> modifyFileList;

  // ============================== GetMapping ==============================

  // 목록페이지
  /*@GetMapping("/list")
  public String list(Model model) {

    // select * ... 로 조회하여 HBoardVO 객체를 담은 리스트를 반환받음
    List<HBoardVO> boards = boardService.getAllBoards();

    // 반환받은 리스트를 컨트롤러에서 뷰(화면)로 데이터를 전달할 때 사용
    model.addAttribute("boards", boards);

    return "/board/list";
  }*/

  /*@GetMapping("/list")
  public String list(PagingRequestDTO pagingRequestDTO, Model model) {

    log.info("pagingRequestDTO:{}", pagingRequestDTO);

    // select * ... 로 조회하여 HBoardVO 객체를 담은 리스트를 반환받음
    PagingResponseDTO<HBoardPageDTO> responseDTO  = boardService.getList(pagingRequestDTO);

    log.info("responseDTO:{}", responseDTO.getDtoList());
    // 반환받은 리스트를 컨트롤러에서 뷰(화면)로 데이터를 전달할 때 사용
    model.addAttribute("responseDTO", responseDTO);
    model.addAttribute("pagingRequestDTO", pagingRequestDTO);
    return "/board/list";
  }*/

  @GetMapping("/list")
  public String list(PagingRequestDTO pagingRequestDTO, Model model) {

    log.info("pagingRequestDTO:{}", pagingRequestDTO);

    // select * ... 로 조회하여 HBoardVO 객체를 담은 리스트를 반환받음
    PagingResponseDTO<HBoardPageDTO> responseDTO  = boardService.getListWithSearch(pagingRequestDTO);

    log.info("responseDTO:{}", responseDTO.getDtoList());
    // 반환받은 리스트를 컨트롤러에서 뷰(화면)로 데이터를 전달할 때 사용
    model.addAttribute("responseDTO", responseDTO);
    // model.addAttribute("pagingRequestDTO", pagingRequestDTO);
    return "/board/list";
  }

  // 글등록페이지
  @GetMapping("/register")
  public String registerGET(Model model) {

    // 빈 객체라도 보내줘야 함 (#fields.hasErrors('title')를 검토하기 때문에)
    HBoardDTO board = new HBoardDTO();

    // 빈 객체라도 컨트롤러에서 뷰(화면)로 데이터를 전달
    model.addAttribute("board", board);

    return "/board/register";
  }

  // 답글등록페이지
  @GetMapping("/showReplyForm")
  public String showReplyForm(@RequestParam(value="ref") int ref,
                              @RequestParam(value="step") int step,
                              @RequestParam(value="refOrder") int refOrder, Model model) {

    HBoardDTO reply = new HBoardDTO();
    reply.setRef(ref);
    reply.setStep(step);
    reply.setRefOrder(refOrder);
    // 컨트롤러에서 뷰(화면)로 반환받은 데이터를 전달
    model.addAttribute("reply", reply);

    return "/board/replyForm";
  }

  // 글상세페이지
  @GetMapping("/detail")
  public String boardDetail(@RequestParam(value="boardNo", required = false, defaultValue = "-1") int boardNo,
                            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                            PagingRequestDTO pagingRequestDTO, Model model,
                            HttpServletRequest request, RedirectAttributes redirectAttributes) {

    // 잘못된 요청(Bod Request)
    if (boardNo == -1) {
      redirectAttributes.addFlashAttribute("error", "정상적인 주소로 접근해주세요.");
      return "redirect:/board/list";
    }

    // 클라이언트의 IP주소를 얻어와서 서비스단에 전달
    String ipAddr = GetClientIPAddr.getClientIP(request);

    List<HBoardDetailInfo> detailInfos = boardService.viewBoardByNo(boardNo, ipAddr);

    log.info("detailInfos : {}", detailInfos.get(0));
    if("Y".equals(detailInfos.get(0).getIsDelete())) {
      redirectAttributes.addFlashAttribute("error", "삭제되거나 존재되지 않는 글입니다.");
      return "redirect:/board/list";
    }

    if (detailInfos.get(0).getUpfiles() == null) {
      detailInfos.get(0).setUpfiles(Collections.emptyList());
    }
    // 컨트롤러에서 뷰(화면)로 반환받은 데이터를 전달
    model.addAttribute("detail", detailInfos.get(0));
    model.addAttribute("pagingRequestDTO", pagingRequestDTO);

    return "/board/detail";
  }

  // 게시글 수정하기
  @GetMapping("/modify")
  public String showModifyForm(@RequestParam(value="boardNo") int boardNo,
                               PagingRequestDTO pagingRequestDTO, Model model) {
    log.info("{}", boardNo);

    /*List<HBoardDetailInfo> detailInfos = boardService.viewBoardDetailInfoByNo(boardNo);
    this.modifyFileList = detailInfos.get(0).getUpfiles();
    model.addAttribute("board", detailInfos.get(0));*/

    HBoardDTO board = boardService.getBoardDetail(boardNo);
    List<BoardUpFilesVODTO> upfiles = boardService.viewFilesByBoardNo(boardNo);

    log.info("%%%%%%%%%%%%%%%%%%%%%%%%{}", upfiles);
    board.setUpfiles(upfiles);
    this.modifyFileList = upfiles;
    log.info("################ {}", board);

    model.addAttribute("board", board);
    model.addAttribute("pagingRequestDTO", pagingRequestDTO);

    return "/board/modify";
  }

  // ============================== PostMapping ==============================

  // 글저장(ajax에서 요청)
  @PostMapping("/register")
  @ResponseBody // REST API 방식 (반환값이 json 등으로 직접 전송됨)
  public ResponseEntity writeBoard(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult) throws IOException {

    /*@ModelAttribute("board") --> th:object="${board}로 보낸 폼 데이터를 DTO 객체로 자동 바인딩
      @Valid --> 각 필드를 유효성 검사하여 실패한 필드와 메시지가 BindingResult 객체에 저장됨
      BindingResult --> @ModelAttribute 바로 뒤에 붙여야하고, 에러가 있으면 반환해줌*/

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

      /*컨트롤러에서 문자열, 객체 등을 반환하면 Spring이 자동으로 HTTP 응답을 만들어줌
        ResponseEntity를 사용하면 개발자가 응답의 상태코드(200, 400, 404), 헤더, 본문을 직접 지정 가능
        badRequest() : 400 응답을 보냄
        응답 본문에 errors 객체를 보냄 (JSON 등으로 클라이언트(브라우저)에 전달)*/

      return ResponseEntity.badRequest().body(errors);
    }

    List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(board.getMultipartFiles());

    // 실제 저장된 BoardUpFilesVODTO를 담은 리스트를 HBoardDTO에 담음
    board.setUpfiles(upFilesVODTOS);

    // DTO를 각 DB에 저장 (게시글 insert, boardNo로 ref를 update, boardUpfiles테이블에 파일관련 insert)
    boardService.saveBoardWithFiles(board);

    return ResponseEntity.ok().build(); // .build()만 호출하면, 본문이 없는 200 OK 응답이 만들어짐
  }

  // 답글저장(폼태그에서 요청)
  @PostMapping("/saveReply")
  public String saveReply(@Valid @ModelAttribute("reply") HBoardDTO reply, BindingResult bindingResult) throws IOException {

    if(bindingResult.hasErrors()) {
      return "/board/replyForm";
    }

    List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(reply.getMultipartFiles());
    reply.setUpfiles(upFilesVODTOS);

    boardService.saveReply(reply);

    return "redirect:/board/list";
  }


  @PostMapping(value = "/modifyRemoveFileCheck", produces = "application/json; charset=UTF-8")
  public ResponseEntity<MyResponseWithoutData> modifyRemoveFileCheck(@RequestParam("removeFileNo") int removeFilePK) {
    log.info("삭제하자 {}", removeFilePK);
    for (BoardUpFilesVODTO file : modifyFileList) {
      if(removeFilePK == file.getFileNo()) {
        file.setFileStatus(BoardUpFileStatus.DELETE); // 삭제예정 표시
      }
    }
    outputCurModifyFileList();
    return ResponseEntity.ok(new MyResponseWithoutData(200, null, "success"));
  }

  private void outputCurModifyFileList() {
    for(BoardUpFilesVODTO file : modifyFileList) {
      log.info("outputCurModifyFileList : {}", file);
    }
  }

  @PostMapping(value = "/cancelRemFiles")
  public ResponseEntity<MyResponseWithoutData> cancelRemFiles() {
    log.info("파일리스트의 모든 파일 삭제 취소처리");

    for(BoardUpFilesVODTO file : modifyFileList) {
      file.setFileStatus(null);
      log.info("{}", file);
    }

    return ResponseEntity.ok(new MyResponseWithoutData(200, null, "success"));
  }

  @PostMapping(value = "/modifyBoardSave")
  public String modifyBoardSave(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult,
                                @ModelAttribute("pagingRequestDTO") PagingRequestDTO pagingRequestDTO,
                                @RequestParam(value = "modifyNewFile", required=false) MultipartFile[] modifyNewFile, RedirectAttributes redirectAttributes) {
    log.info("수정하자 {}", board);
    String link = pagingRequestDTO.getLink();
    if(bindingResult.hasErrors()) {

      // "redirect:/board/detail?boardNo=" + board.getBoardNo();
      redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
      redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.board", bindingResult);
      redirectAttributes.addFlashAttribute("board", board);
      for (ObjectError error :bindingResult.getAllErrors()) {
        log.info("에러메시지 {}", error.getDefaultMessage());
      }
      return "redirect:/board/modify?boardNo=" + board.getBoardNo() + "&" + link;
    }

    // 새로 업로드 된 파일 저장 준비
    try {
      if (modifyNewFile != null && modifyNewFile.length > 0) {
        List<MultipartFile> fileList = new ArrayList<>();
        for (MultipartFile file : modifyNewFile) {
          if(!file.isEmpty()) {
            fileList.add(file);
          }
        }
        if(!fileList.isEmpty()) {
          List<BoardUpFilesVODTO> savedFiles = fileUploadUtil.saveFiles(fileList);
          for (BoardUpFilesVODTO fileInfo : savedFiles) {
            fileInfo.setFileStatus(BoardUpFileStatus.INSERT);
            fileInfo.setBoardNo(board.getBoardNo());
            modifyFileList.add(fileInfo);
          }
        }
      }
      outputCurModifyFileList();

      // 최종 수정 저장
      board.setUpfiles(modifyFileList);
      boardService.modifyBoard(board);
      redirectAttributes.addAttribute("status", "success");
    } catch (Exception e) {
      log.error("게시글 수정 실패", e);
      redirectAttributes.addAttribute("status", "failure");
    }

    return "redirect:/board/detail?boardNo=" + board.getBoardNo() +"&" + link;

/*    if(bindingResult.hasErrors()) {
      log.info("{}", bindingResult);
      return "/board/modifyBoardSave";
    }
    log.info("{}", board);*/
  }

  @RequestMapping("/removeBoard")
  public String removeBoard(@RequestParam("boardNo") int boardNo, PagingRequestDTO pagingRequestDTO, RedirectAttributes redirectAttributes) {
    log.info("{}번 글을 삭제하자...", boardNo);

    List<BoardUpFilesVODTO> upFilesVODTOS = boardService.removeBoard(boardNo);

    try {
      if(upFilesVODTOS != null) {
        // 하드에서 삭제해주면 됨
        for (BoardUpFilesVODTO upFilesVODTO : upFilesVODTOS) {
          log.info("{}", upFilesVODTO);
          fileUploadUtil.deleteFile(upFilesVODTO.getFilePath());
          if (upFilesVODTO.getIsImage()) {
            fileUploadUtil.deleteFile(upFilesVODTO.getThumbFileName());
          }
        }
        redirectAttributes.addAttribute("status", "success");
      } else {
        // 하드에서 삭제할건 따로 없는...?
      }
    } catch (Exception e) {
      redirectAttributes.addAttribute("status", "failure");
    }

    return "redirect:/board/list";
  }
}
