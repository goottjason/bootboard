package com.miniproj.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@Getter
public class DestinationPath {

  private static String uri;
  private static String queryString;
  private static String destPath;

  public static void setDestPath(HttpServletRequest req) {
    uri = req.getRequestURI();
    queryString = req.getQueryString();
    /*if (queryString != null) {
      destPath = uri + "?" + queryString;
    } else {
      destPath = uri;
    }*/
    destPath = queryString == null ? uri : uri + "?" + queryString;

    log.info("destPath = {}", destPath);
    log.info("queryString = {}", queryString);

    req.getSession().setAttribute("destPath", destPath);
  }

}
