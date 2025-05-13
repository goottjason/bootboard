package com.miniproj.domain;

import lombok.Getter;


public enum PointWhy {

  SIGNUP(100),
  LOGIN(1),
  WRITE(10),
  REPLY(2);

  private final int score;

  PointWhy(int score) {
    this.score = score;
  }
  public int getScore() {
    return score;
  }

}
