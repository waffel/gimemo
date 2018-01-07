/*
 * Copyright (c) 2017.
 */

package org.waffel.gimemo;

import java.util.List;
import java.util.stream.Collectors;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DataSetCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataSetCreator.class);
  private final List<Double> datalist;
  private final int inputCount;
  private final int outputCount;

  DataSetCreator(List<Double> datalist, int inputCount, int outputCount) {
    this.datalist = datalist;
    this.inputCount = inputCount;
    this.outputCount = outputCount;
  }

  public Double getMax() {
    return datalist.stream().max(Double::compare).get();
  }
  public static List<Double> normalizeDouble(List<Double> deNormalizedList)
  {
    final Double max = deNormalizedList.stream().max(Double::compare).get();
    return deNormalizedList.parallelStream().map(d -> (d / max) * 0.8 + 0.1).collect(Collectors.toList());
  }
  public DataSet getDataSet() {
    LOGGER.debug("start to create the dataset");
    final List<Double> normalizedList = normalizeDouble(this.datalist);
    final DataSet dataSet = new DataSet(this.inputCount, this.outputCount);
    final int size = datalist.size();
    for (int i = 0; i < size; i += this.inputCount) {
      int nextIndex = Math.min(size, i + this.inputCount);
      final List<Double> v = normalizedList.subList(i, nextIndex);
      // only if the values have enough input elements and also one output element is there
      if (v.size() == inputCount && nextIndex < size) {
        final DataSetRow dataSetRow = new DataSetRow();
        LOGGER.debug("{} -> {}",v.toString(),normalizedList.get(nextIndex));
        dataSetRow.setInput(v.stream().mapToDouble(d -> d).toArray());
        dataSetRow.setDesiredOutput(new double[]{normalizedList.get(nextIndex)});
        dataSet.add(dataSetRow);
      }
    }
    return dataSet;
  }
}
