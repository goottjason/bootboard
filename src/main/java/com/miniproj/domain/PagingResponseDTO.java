package com.miniproj.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
@Getter
@ToString
public class PagingResponseDTO<T> {
  private int pageNo;
  private int pagingSize;
  private int total;

  private int start; // 블록 내 시작 페이지 번호
  private int end; // 블록 내 끝 페이지 번호
  private int last; // 마지막 페이지 번호

  private boolean prev;
  private boolean next;

  private List<T> dtoList;

  @Builder(builderMethodName = "allInfo") // 빌더의 이름 지정
  public PagingResponseDTO(PagingRequestDTO pagingRequestDTO, List<T> dtoList, int total) {

    this.pageNo = pagingRequestDTO.getPageNo();
    this.pagingSize = pagingRequestDTO.getPagingSize();

    this.total = total;

    this.end = (((pageNo - 1) / pagingSize) + 1) * pagingSize;
    this.start = this.end - (pagingSize-1); // (int)(Math.ceil(end/(double)pagingSize)) - pagingSize;
    this.last = (int)(Math.ceil(total/(double)pagingSize));

    // this.end > this.last ? this.last : this.end;
    this.end = Math.min(this.end, this.last);

    this.prev = this.start > 1;
    this.next = this.end < this.last;

    this.dtoList = dtoList;
  }
}
