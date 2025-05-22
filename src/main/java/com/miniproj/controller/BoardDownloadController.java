package com.miniproj.controller;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.service.CommentBoardService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardDownloadController {

  private final CommentBoardService commentBoardService;

  @Value("${file.upload-base-dir}")
  private String baseDir;

  @Value("${file.upload-url-path}")
  private String uploadUrlPath;

  @GetMapping("/download/{fileId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") int fileId) {

    BoardUpFilesVODTO file = commentBoardService.getUploadFileInfo(fileId);

    if (file == null || file.getFilePath() == null) {
      return ResponseEntity.notFound().build();
    }
    String fullPath = baseDir + file.getFilePath().replace(uploadUrlPath, "").replace("/", File.separator);
    log.info("fullPath = {}", fullPath);
    File resourceFile = new File(fullPath);
    Resource resource = new FileSystemResource(resourceFile);
    String originalFilename = file.getOriginalFileName();
    String encodedName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
      .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resourceFile.length()))
      .body(resource);
  }
}
