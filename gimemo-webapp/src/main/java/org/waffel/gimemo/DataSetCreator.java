/*
 * Copyright (c) 2017.
 */

package org.waffel.gimemo;

import java.util.List;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
public class DataSetCreator {

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

  public DataSet getDataSet() {
    final DataSet dataSet = new DataSet(this.inputCount, this.outputCount);
    final int size = datalist.size();
    for (int i = 0; i < size; i += this.inputCount) {
      int nextIndex = Math.min(size, i + this.inputCount);
      final List<Double> v = datalist.subList(i, nextIndex);
      // only if the values have enough input elements and also one output element is there
      if (v.size() == inputCount && nextIndex < size) {
        final DataSetRow dataSetRow = new DataSetRow();
        dataSetRow.setInput(v.stream().mapToDouble(d -> d).toArray());
        dataSetRow.setDesiredOutput(new double[]{datalist.get(nextIndex)});
        dataSet.add(dataSetRow);
      }
    }
    return dataSet;
  }
}
