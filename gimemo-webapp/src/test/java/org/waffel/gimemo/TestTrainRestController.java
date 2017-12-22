
/*
 * Copyright (c) 2017.
 */

package org.waffel.gimemo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.waffel.gimemo.neuro.train.TrainResult;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Created by z0017vtb on 27.02.2017.
 */
@RunWith(SpringRunner.class) @WebMvcTest(TrainRestController.class) public class TestTrainRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestTrainRestController.class);
  @Autowired ResourceLoader resourceLoader;

  @Autowired ObjectMapper objectMapper;
  @Autowired private MockMvc mvc;

  @Test public void testTrainCall() throws Exception {
    final Resource resource = resourceLoader.getResource("classpath:org/waffel/gimemo/AMD.csv");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("trainfile", resource.getInputStream());

    // @formatter:off
    mvc.perform(fileUpload("/train")
          .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
    // @formatter:on
  }

  @Test public void testCalculateCall() throws Exception {
    // first we need to train to later calculate
    final Resource resource = resourceLoader.getResource("classpath:org/waffel/gimemo/AMD.csv");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("trainfile", resource.getInputStream());
    // @formatter:off
    final MvcResult result = mvc.perform(fileUpload("/train")
          .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk()).andReturn();
    // @formatter:on

    final String contentAsString = result.getResponse().getContentAsString();
    TrainResult trainResult = new ObjectMapper().readValue(contentAsString, TrainResult.class);
    // @formatter:off
    mvc.perform(post("/calculate")
          .param("prediction", Arrays.stream(new double[]{9.94,10.16,9.9,10.11,10.13,10.29}).boxed().map(Object::toString).collect
              (Collectors.joining(",")))
          .param("savePathOnServer", trainResult.getSavePath().toString())
          .contentType(APPLICATION_JSON)
          .accept(APPLICATION_JSON))
        .andExpect(status().isOk());
    // @formatter:on
  }
}
