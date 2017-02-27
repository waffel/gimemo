/*
 * Copyright (c) 2015 Siemens GS IT EB CONF. All rights reserved.
 * This software is the confidential and proprietary information of Siemens GS IT EB CONF.
 * This file is part of SPICE.
 */

package org.waffel.gimemo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
/**
 * Created by z0017vtb on 27.02.2017.
 */
@RunWith(SpringRunner.class) @WebMvcTest(TrainRestController.class) public class TestTrainRestController {

  @Autowired private MockMvc mvc;

  @Test public void testTrainCall() throws Exception {
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("trainfile", "12345".getBytes());

    // @formatter:off
    mvc.perform(fileUpload("/train")
          .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
    // @formatter:on
  }
}
