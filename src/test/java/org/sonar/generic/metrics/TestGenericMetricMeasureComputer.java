/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import java.util.ArrayList;

import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONException;
import org.junit.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer.*;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition.Builder;
import org.sonar.api.measures.Metric;

public class TestGenericMetricMeasureComputer {

  private GenericMetricMeasureComputer measureComputer;

  private Metric metric;

  @Mock
  private MeasureComputerDefinitionContext definitionContext;

  @Mock
  private MeasureComputerContext context;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);
  }

  @Test
  public void whenDefiningShouldCreateMetricDefinition() {
    Builder builder = mock(Builder.class);
    when(definitionContext.newDefinitionBuilder()).thenReturn(builder);
    when(builder.setOutputMetrics("metrickey")).thenReturn(builder);

    this.measureComputer.define(definitionContext);
    verify(builder, times(1)).build();
  }

  private void addDoubleMeasure(double value) {
    ArrayList<Measure> measures = (ArrayList<Measure>) this.context.getChildrenMeasures(this.metric.key());
    Measure measureObj = mock(Measure.class);
    when(measureObj.getDoubleValue()).thenReturn(value);
    measures.add(measureObj);
  }

  private void addIntMeasure(int value) {
    ArrayList<Measure> measures = (ArrayList<Measure>) this.context.getChildrenMeasures(this.metric.key());
    Measure measureObj = mock(Measure.class);
    when(measureObj.getIntValue()).thenReturn(value);
    measures.add(measureObj);
  }

  private void addStringMeasure(String value) {
    ArrayList<Measure> measures = (ArrayList<Measure>) this.context.getChildrenMeasures(this.metric.key());
    Measure measureObj = mock(Measure.class);
    when(measureObj.getStringValue()).thenReturn(value);
    measures.add(measureObj);
  }
  
  private Component addParentComponent(Component.Type componentType) {
    Component component = mock(Component.class);
    when(component.getType()).thenReturn(componentType);
    ArrayList<Measure> measures = new ArrayList<>();
    when(context.getChildrenMeasures(this.metric.key())).thenReturn(measures);
    when(context.getComponent()).thenReturn(component);
    return component;
  }
  
  @Test
  public void testWhenCallingComputeAndMetricIsFloatAndNoMeasuresShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.FLOAT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 0d);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsProjectAndMetricIsFloatShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.FLOAT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);
    addDoubleMeasure(10.0);
    addDoubleMeasure(0.5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 10.5d);
  }

  @Test
  public void testWhenCallingComputeAndComponentIsModuleAndMetricIsFloatShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.FLOAT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.MODULE);
    addDoubleMeasure(10.0);
    addDoubleMeasure(0.5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 10.5d);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsDirectoryAndMetricIsFloatShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.FLOAT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.DIRECTORY);
    addDoubleMeasure(10.0);
    addDoubleMeasure(0.5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 10.5d);
  }
  
  @Test
  public void testWhenCallingComputeAndMetricIsIntAndNoMeasuresShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 0);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsProjectAndMetricIsIntShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);
    addIntMeasure(10);
    addIntMeasure(5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 15);
  }

  @Test
  public void testWhenCallingComputeAndComponentIsModuleAndMetricIsIntShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.MODULE);
    addIntMeasure(10);
    addIntMeasure(5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 15);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsDirectoryAndMetricIsIntShouldComputeSumCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.DIRECTORY);
    addIntMeasure(10);
    addIntMeasure(5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 15);
  }

  @Test
  public void testWhenCallingComputeAndMetricIsPercentAndNoMeasuresShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.PERCENT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 0d);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsProjectAndMetricIsPercentShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.PERCENT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);
    addDoubleMeasure(80.5);
    addDoubleMeasure(40.5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 60.5);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsModuleAndMetricIsPercentShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.PERCENT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.MODULE);
    addDoubleMeasure(80.5);
    addDoubleMeasure(40.5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 60.5);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsDirectoryAndMetricIsPercentShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.PERCENT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.DIRECTORY);
    addDoubleMeasure(80.5);
    addDoubleMeasure(40.5);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 60.5);
  }
  
  @Test
  public void testWhenCallingComputeAndMetricIsRaitingAndNoMeasuresShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.RATING).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 0);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsProjectAndMetricIsRatingShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.RATING).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);
    addIntMeasure(6);
    addIntMeasure(10);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 8);
  }

  @Test
  public void testWhenCallingComputeAndComponentIsModuleAndMetricIsRatingShouldComputAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.RATING).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.MODULE);
    addIntMeasure(6);
    addIntMeasure(10);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 8);
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsDirectoryAndMetricIsRatingShouldComputeAverageCorrectly() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.RATING).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.DIRECTORY);
    addIntMeasure(6);
    addIntMeasure(10);

    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 8);
  }
  
  @Test
  public void testWhenCallingComputeForUnsupportedMetricShouldNotAddMeasure() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.STRING).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    TestHelper.setupComponentAndStringMeasure(context, Component.Type.FILE, "a", metric.key());
    this.measureComputer.compute(context);

    TestHelper.setupComponentAndStringMeasure(context, Component.Type.FILE, "b", metric.key());
    this.measureComputer.compute(context);

    TestHelper.setupProject(context);
    this.measureComputer.compute(context);

    verify(context, times(0)).addMeasure(eq(metric.key()), any(String.class));
  }
  
  @Test
  public void testWhenCallingComputeAndComponentIsValidForUnsupportedMetricShouldDoNothing() {
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.STRING).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    addParentComponent(Component.Type.PROJECT);
    addStringMeasure("A string");
    
    this.measureComputer.compute(context);
    verify(context, times(0)).addMeasure(eq(metric.key()), any(String.class));
  }

  @Test
  public void testWhenCallingComputeWithUnexpectedComponentShouldNotAddMeasure() {
    TestHelper.setupView(context);

    this.measureComputer.compute(context);
    verify(context, times(0)).addMeasure(anyString(), anyInt());
  }
  
  @Test
  public void testWhenCallingComputeWithMeasureAllReadyAddedShouldNotAddMeasure() {
    TestHelper.setupComponentAndIntMeasure(context, Component.Type.FILE, 2, metric.key());
    
    addParentComponent(Component.Type.PROJECT);
    addIntMeasure(5);
    addIntMeasure(10);
    
    this.measureComputer.compute(context);
    verify(context, times(0)).addMeasure(anyString(), anyInt());
  }
  
  @Test
  public void testWhenCallingComputeWithMeasureAllReadyAddedShouldReturnAddedProjectMeasure() {
    TestHelper.setupComponentAndIntMeasure(context, Component.Type.FILE, 2, metric.key());
    
    addParentComponent(Component.Type.PROJECT);
    addIntMeasure(5);
    addIntMeasure(10);
    
    this.measureComputer.compute(context);
    Assert.assertEquals(2, context.getMeasure(metric.getKey()).getIntValue());
  }

  @Test
  public void testWhenCallingComputeWithFileWithNoMeasureShouldNotThrow() {
    TestHelper.setupComponentAndIntMeasure(context, Component.Type.FILE, 2, metric.key());
    when(this.context.getMeasure(metric.key())).thenReturn(null);
    this.measureComputer.compute(context);

    verify(context, times(0)).addMeasure(anyString(), anyInt());
  }
}
