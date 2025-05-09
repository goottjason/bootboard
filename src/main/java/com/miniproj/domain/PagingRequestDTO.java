package com.miniproj.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

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

  public int getSkip() {
    return (pageNo - 1) * pagingSize;
  }

//  private int totalCount;

  public String getLink() {
    return "pageNo=" + pageNo + "&pagingSize=" + pagingSize;
  }
}
