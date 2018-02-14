
/*
 * Copyright (c) 2017.
 */

package org.waffel.gimemo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.waffel.gimemo.neuro.train.CalculateResult;
import org.waffel.gimemo.neuro.train.TrainResult;
/**
 * Created by z0017vtb on 27.02.2017.
 */
@RunWith(SpringRunner.class) @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestTrainRestController {

  @Autowired TestRestTemplate restTemplate;

  @Test public void testTrainCall() {
    final ClassPathResource resource = new ClassPathResource("AMD.csv", getClass());
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("trainfile", resource);
    final ResponseEntity<TrainResult> responseEntity = this.restTemplate.postForEntity("/train", map, TrainResult.class);
    assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(responseEntity.getBody().getSavePath()).isNotNull();
  }

  @Test public void testCalculateCall() {
    final ClassPathResource resource = new ClassPathResource("AMD.csv", getClass());
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("trainfile", resource);
    final ResponseEntity<TrainResult> responseEntity = this.restTemplate.postForEntity("/train", map, TrainResult.class);

    MultiValueMap<String, Object> calcMap = new LinkedMultiValueMap<>();
    calcMap.add("prediction",
        Arrays.stream(new double[]{9.94, 10.16, 9.9, 10.11, 10.13, 10.29}).boxed().map(Object::toString).collect(Collectors.joining(",")));
    calcMap.add("savePathOnServer", responseEntity.getBody().getSavePath().toString());
    final ResponseEntity<CalculateResult>
        calculateResultResponseEntity =
        this.restTemplate.postForEntity("/calculate", calcMap, CalculateResult.class);
    assertThat(calculateResultResponseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(calculateResultResponseEntity.getBody().getName()).isNotNull();
  }

}
