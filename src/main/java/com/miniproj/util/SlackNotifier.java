package com.miniproj.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SlackNotifier {
  @Value("${webhook.slack.url}")
  private String slackUrl;

  public void notify(String title, String message) {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> payloadMap = new HashMap<>();
    payloadMap.put("text", title + "\n" + message);

    try {
      String payload = mapper.writeValueAsString(payloadMap);// Map을 Json으로 바꿀 때
      log.info("payload - {}", payload);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(payload, headers);
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.postForEntity(slackUrl, entity, String.class);

    } catch (JsonProcessingException e) {
      log.error("slack : json 변환 에러({})", e.getMessage());
    }


  }
}
