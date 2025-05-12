package com.miniproj.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PagingRequestDTO {

  @Builder.Default
  @Min(value = 1)
  @Positive
  private int pageNo = 1;

  @Builder.Default
  @Min(value = 10)
  @Max(value = 100)
  @Positive
  private int pagingSize = 10;

  private String link;

  private String keyword;
  private String type; // 검색타입 : c, t, w, ct, ctw





  // ------------- 메서드

  public int getSkip() {
    return (pageNo - 1) * pagingSize;
  }

  public String getLink() {
    if (link == null) {
      link = generateLink();
    }
    return link;
  }

  private String generateLink() {
    StringBuilder sb = new StringBuilder();
    sb.append("pageNo=").append(pageNo)
      .append("&pagingSize=").append(pagingSize);

    if(type != null && !type.isBlank()) { // isEmpty 이거나 whitespace인 경우, true 반환
      sb.append("&type=").append(type);
    }
    if(keyword != null && !keyword.isBlank()) {
      sb.append("&keyword=").append(keyword);
    }
    return sb.toString();
  }

  public List<String> getTypes() {
    if (type == null || type.isEmpty()) {
      return null;
    } else {
      return Arrays.asList(type.split("")); // of는 뭐지?
    }

  }
}
