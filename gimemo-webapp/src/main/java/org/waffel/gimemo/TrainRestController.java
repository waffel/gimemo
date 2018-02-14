/*
 * Copyright (c) 2017.
 */
package org.waffel.gimemo;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
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

  /**
   * Returns the correct value from the CVS. The value is in row 5 (starting with 0)
   */
  private final Function<String, Double> mapToDataset = line -> {
    String[] cells = line.split(",");
    if ((cells.length == 7) && (cells[5] != null) && (cells[5].trim().length() > 0)) {
      return new Double(cells[5]);
    }
    return 0D;
  };

  @RequestMapping(value="/train", method = RequestMethod.POST)
  public @ResponseBody TrainResult train(@RequestParam("trainfile") MultipartFile trainFile,
      @RequestParam(value = "libraryPath", required = false) String libraryPath) throws IOException {

    int maxIterations = 10000;
    MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(6, 13, 1);
    neuralNet.getLearningRule().setMaxError(0.001);//0-1
    neuralNet.getLearningRule().setLearningRate(0.7);//0-1
    neuralNet.getLearningRule().setMaxIterations(maxIterations);//0-1
    neuralNet.randomizeWeights();

    final DataSet trainingSet = generateTrainSet(trainFile, neuralNet.getInputsCount(), neuralNet.getOutputsCount());

    // learn with training set
    LOGGER.debug("start to train MultilayerPerceptron with {} input, {} hidden and {} output count. Learning rate is '{}' and max error"
        + " is '{}'", neuralNet.getInputsCount(), neuralNet.getLayersCount(), neuralNet.getOutputsCount(), neuralNet.getLearningRule()
        .getLearningRate(), neuralNet.getLearningRule().getMaxError());
    neuralNet.learn(trainingSet);
    LOGGER.debug("learning done after {} iterations", neuralNet.getLearningRule().getMaxIterations());

    // we need to save the network
    final Path savePath = calculateSavePath(libraryPath);
    LOGGER.debug("save neuronal network to {}", savePath);
    neuralNet.setLabel(java.util.UUID.randomUUID().toString());
    neuralNet.save(savePath.toString());
    final TrainResult trainResult = new TrainResult();
    trainResult.setSavePath(savePath);
    return trainResult;
  }

  @RequestMapping(value = "/calculate", method = RequestMethod.POST) public @ResponseBody CalculateResult calculate(
      @RequestParam("prediction") List<Double> predictionList, @RequestParam("savePathOnServer") String savePathOnServer)
      throws FileNotFoundException {
    final NeuralNetwork neuralNetwork = NeuralNetwork.load(new FileInputStream(savePathOnServer));
    neuralNetwork.setInput(DataSetCreator.normalizeDouble(predictionList).stream().mapToDouble(d -> d).toArray());
    LOGGER.debug("calculate the output from {}", DataSetCreator.normalizeDouble(predictionList));
    neuralNetwork.calculate();
    LOGGER.debug("output '{}'", neuralNetwork.getOutput());
    final CalculateResult result = new CalculateResult();
    result.setName(neuralNetwork.getLabel());
    return result;
  }
  private DataSet generateTrainSet(final MultipartFile trainFile, final int inputsCount, final int outputsCount) throws IOException {
    final DataSetCreator dataSetCreator;
    final InputStream inputStream = trainFile.getInputStream();
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
      List<Double> inputList;
      inputList = bufferedReader.lines().skip(1).map(mapToDataset).collect(Collectors.toList());
      dataSetCreator = new DataSetCreator(inputList, inputsCount, outputsCount);
    }
    return dataSetCreator.getDataSet();
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
