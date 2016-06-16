/*
 * Copyright (c) 2015 Siemens GS IT EB CONF. All rights reserved.
 * This software is the confidential and proprietary information of Siemens GS IT EB CONF.
 * This file is part of SPICE.
 */
package org.waffel.gimemo;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.waffel.gimemo.neuro.train.TrainResult;
/**
 * Created by Thomas Wabner on 16.06.2016.
 */
@RestController
public class TrainRestController {

  // to test file upload: https://github.com/murygin/rest-document-archive/blob/master/src/test/java/org/murygin/archive/client/test/ArchiveClientTest.java

  @RequestMapping(value="/train", method = RequestMethod.POST)
  public @ResponseBody
  TrainResult train(@RequestParam("trainfile")MultipartFile trainFile) {

    int maxIterations = 10000;
    NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 9, 1);
    ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
    ((LMS) neuralNet.getLearningRule()).setLearningRate(0.7);//0-1
    ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1

    // create the learning data set
    final DataSet trainingSet = new DataSet(0);

    // learn with training set
    neuralNet.learnInNewThread(trainingSet);

    // we need to save the network
    //neuralNet.save(path);

    return new TrainResult();
  }

}
