/*
 * Copyright (c) 2015 Siemens GS IT EB CONF. All rights reserved.
 * This software is the confidential and proprietary information of Siemens GS IT EB CONF.
 * This file is part of SPICE.
 */
package org.waffel.gimemo;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.waffel.gimemo.neuro.train.CalculateResult;
import org.waffel.gimemo.neuro.train.TrainResult;
/**
 * Created by Thomas Wabner on 16.06.2016.
 */
@RestController
public class TrainRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainRestController.class);

  @RequestMapping(value="/train", method = RequestMethod.POST)
  public @ResponseBody TrainResult train(@RequestParam("trainfile") MultipartFile trainFile,
      @RequestParam(value = "libraryPath", required = false) String libraryPath) {

    int maxIterations = 10000;
    NeuralNetwork neuralNet = new MultiLayerPerceptron(4, 9, 1);
    ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
    ((LMS) neuralNet.getLearningRule()).setLearningRate(0.7);//0-1
    ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1

    // create the learning data set
    final DataSet trainingSet = new DataSet(4, 1);
    trainingSet.addRow(new DataSetRow(new double[]{0, 1, 2, 3}, new double[]{0}));

    // learn with training set
    neuralNet.learn(trainingSet);

    // we need to save the network
    final Path savePath = calculateSavePath(libraryPath);
    LOGGER.debug("save neuronal network to {}", savePath);
    neuralNet.save(savePath.toString());
    final TrainResult trainResult = new TrainResult();
    trainResult.setSavePath(savePath);
    return trainResult;
  }
  @RequestMapping(value = "/calculate", method = RequestMethod.POST) public @ResponseBody CalculateResult calculate(
      @RequestParam("prediction") List<Double> predictionList) {
    predictionList.forEach(it -> LOGGER.debug(it.toString()));

    return new CalculateResult();
  }
  private Path calculateSavePath(final String libraryPath) {
    String saveFileName = Long.toString(Instant.now().getEpochSecond());
    Path savePath;
    if (null != libraryPath && Paths.get(libraryPath).toFile().exists()) {
      savePath = Paths.get(libraryPath, saveFileName);
    } else {
      // save to system temp
      savePath = Paths.get(System.getProperty("java.io.tmpdir"), saveFileName);
    }
    return savePath;
  }

}
