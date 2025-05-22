package com.miniproj.util;

import com.miniproj.domain.BoardUpFilesVODTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploadUtil {

  // @Value --> 자동으로 할당(초기화)
  @Value("${file.upload-base-dir}")
  private String baseDir;

  @Value(("${file.upload-url-path}"))
  private String uploadUrlPath;


  /*saveFiles() 메서드에서 하는 일들
      1) 현재 날짜로 디렉토리를 생성, 파일 저장,
      2) 이미지이면 썸네일 이미지 및 Base64 저장, DTO를 담은 리스트 반환*/

  // MultipartFile : Spring Web에서 파일 업로드를 쉽게 처리하기 위해 제공하는 인터페이스
  public List<BoardUpFilesVODTO> saveFiles(List<MultipartFile> multipartFileList) throws IOException {
    List<BoardUpFilesVODTO> resultList = new ArrayList<>();

    log.info("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK>");
    // 첨부파일 리스트가 null이거나 비어있으면 빈 리스트 객체를 return
    if (multipartFileList == null || multipartFileList.isEmpty()) return resultList;

    // 현재 날짜로 디렉토리를 생성하기 위해 2025/04/29 형태의 값을 받아옴
    String datePath = getDatePath();

    // C:/upload/2025/04/29 형태의 String을 생성
    String targetDir = baseDir + datePath;// baseDir + File.separator + datePath;

    // 디렉토리 생성 (Files 내장객체로 해당 경로의 디렉토리 생성(없으면 생성, 있으면 그대로 둠))
    Files.createDirectories(Paths.get(targetDir));


    for (MultipartFile file: multipartFileList) {

      // 혹시 첨부파일이 없으면 다음 루프로 건너뜀
      if(file.isEmpty()) continue;

      String originalFileName = file.getOriginalFilename();

      String uuid = UUID.randomUUID().toString();

      // 확장자 추출
      String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

      // 'uuid_'를 앞에 붙여 새로운 파일명을 생성
      String newName = uuid + "_" + originalFileName;

      // 경로를 포함한 파일명
      String fullPath = targetDir + "/" + newName;// targetDir + File.separator + newName;

      // 그 경로에 실제 파일 저장
      file.transferTo(new File(fullPath));

      // 이미지이면 썸네일, base64 생성
      // 해당하는 확장자가 image 타입인 지 체크(boolean 반환)
      boolean isImage = ImageMimeType.isImage(ext);


      String base64 = null;
      if (isImage) {
        // 이미지파일이면, 썸네일 이미지를 생성

        // 앞에 'thumb_'를 붙인 파일명
        String thumbName ="thumb_" + newName;

        // 경로를 포팜함 파일명
        String thumbPath = targetDir + "/" + thumbName;// targetDir + File.separator + thumbName;

        // 썸네일 이미지를 생성 (원본 이미지를 200x200 크기로 변환)
        BufferedImage thumbnail = Thumbnails.of(new File(fullPath)).size(200,200).asBufferedImage();
        // 썸네일 이미지를 지정한 경로(thumbPath)에 파일로 저장 (확장자는 ext로 지정)
        ImageIO.write(thumbnail, ext, new File(thumbPath));

        // 썸네일 이미지 파일을 바이트 배열로 읽어옴
        byte[] imageBytes = Files.readAllBytes(Paths.get(thumbPath));

        // 바이트 배열을 Base64 문자열로 인코딩
        base64 = Base64.getEncoder().encodeToString(imageBytes);
      }

      // /upload/yyyy/MM/dd/새파일명
      String relativePath = uploadUrlPath + datePath + "/" + newName;
      String thumbRelativePath = isImage ? uploadUrlPath + datePath + "/" + "thumb_" + newName : null;

      BoardUpFilesVODTO dto = new BoardUpFilesVODTO();

      dto.setOriginalFileName(originalFileName);
      dto.setNewFileName(newName);
      dto.setIsImage(isImage);
      dto.setExt(ext);
      dto.setSize(file.getSize());
      dto.setBase64(base64);
      dto.setFilePath(relativePath);
      dto.setThumbFileName(thumbRelativePath);

      // resultList에 각 dto를 저장
      resultList.add(dto);

    }

    // throw new IOException("예외발생");
    return resultList;
  }

  private String getDatePath() {

    // java.time에서 제공하는 LocalDate로 현재 날짜와 시간을 받아옴
    LocalDate today = LocalDate.now();

    // 'yyyy/MM/dd' 패턴의 형태로 반환
    return today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
  }

  public void deleteFile(String relativePath) throws IOException {
    /*"C:/upload/"+ relativePath*/
    String fullPath = (baseDir + relativePath.replace(uploadUrlPath, "")).replace("/", File.separator);
    File file = new File(fullPath);
    if (file.exists()) file.delete();
  }
}
