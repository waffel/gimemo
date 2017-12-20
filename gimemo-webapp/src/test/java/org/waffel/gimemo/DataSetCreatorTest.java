/*
 * Copyright (c) 2017.
 */

package org.waffel.gimemo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.junit.Test;
import org.neuroph.core.data.DataSet;

import com.google.common.collect.Lists;
public class DataSetCreatorTest {

  @Test public void maxCalculationShouldWork() {
    final List<Double> datalist = DoubleStream.of(12.45, 13.76, 2.56, 10.56, 11.67, 8.67).boxed().collect(Collectors.toList());
    final DataSetCreator dataSetCreator = new DataSetCreator(datalist, 5, 1);
    assertEquals("the maximum should be 13.76", (Double) 13.76, dataSetCreator.getMax());
  }

  @Test public void inputListSplitShouldWorkWithMoreElements() {
    final List<Double>
        datalist =
        DoubleStream.of(12.45, 13.76, 2.56, 10.56, 11.67, 8.67, 5.6, 7, 8.94, 11.7, 13.87, 12.99).boxed().collect(Collectors.toList());
    final DataSetCreator dataSetCreator = new DataSetCreator(datalist, 5, 1);
    final DataSet dataSet = dataSetCreator.getDataSet();
    assertEquals("there should be only two rows, the rest of the list should be skipped", 2, dataSet.getRows().size());
    assertThat(dataSet.getRowAt(0).getDesiredOutput(), equalTo(new double[]{8.67}));
    assertThat(dataSet.getRowAt(1).getDesiredOutput(), equalTo(new double[]{13.87}));
  }

  @Test public void inputListSplitShouldWorkWithExactElements() {
    final List<Double>
        datalist =
        DoubleStream.of(12.45, 13.76, 2.56, 10.56, 11.67, 8.67, 5.6, 7, 8.94, 11.7).boxed().collect(Collectors.toList());
    final DataSetCreator dataSetCreator = new DataSetCreator(datalist, 5, 1);
    final DataSet dataSet = dataSetCreator.getDataSet();
    assertThat(dataSet.getRows().size(), equalTo(1));
    assertThat(dataSet.getRowAt(0).getDesiredOutput(), equalTo(new double[]{8.67}));
  }

  @Test public void givenList_whenParitioningIntoSublistsUsingPartitionBy_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

    Map<Boolean, List<Integer>> groups = intList.stream().collect(Collectors.partitioningBy(s -> s > 6));
    List<List<Integer>> subSets = new ArrayList<>(groups.values());

    List<Integer> lastPartition = subSets.get(1);
    List<Integer> expectedLastPartition = Lists.newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(2));
    assertThat(lastPartition, equalTo(expectedLastPartition));
  }

}