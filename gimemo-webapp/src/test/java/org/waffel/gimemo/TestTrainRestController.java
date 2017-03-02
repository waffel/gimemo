/*
 * Copyright (c) 2015 Siemens GS IT EB CONF. All rights reserved.
 * This software is the confidential and proprietary information of Siemens GS IT EB CONF.
 * This file is part of SPICE.
 */

package org.waffel.gimemo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.neuroph.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Created by z0017vtb on 27.02.2017.
 */
@RunWith(SpringRunner.class) @WebMvcTest(TrainRestController.class) public class TestTrainRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestTrainRestController.class);

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  @Autowired ObjectMapper objectMapper;
  @Autowired private MockMvc mvc;
  @Test public void testTrainCall() throws Exception {
    final File testFile = temporaryFolder.newFile();
    FileUtils.writeStringToFile(testFile, "12345");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("trainfile", new FileInputStream(testFile));

    // @formatter:off
    mvc.perform(fileUpload("/train")
          .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
    // @formatter:on
  }

  @Test public void testCalculateCall() throws Exception {
    // first we need to train to later calculate
    final File testFile = temporaryFolder.newFile();
    FileUtils.writeStringToFile(testFile, "12345");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("trainfile", new FileInputStream(testFile));

    // @formatter:off
    mvc.perform(post("/calculate")
          .param("prediction", Arrays.stream(new double[]{0,1,2,3}).boxed().map(i -> i.toString()).collect(Collectors.joining(",")))
          .contentType(APPLICATION_JSON)
          .accept(APPLICATION_JSON))
        .andExpect(status().isOk());
    // @formatter:on
  }
}
