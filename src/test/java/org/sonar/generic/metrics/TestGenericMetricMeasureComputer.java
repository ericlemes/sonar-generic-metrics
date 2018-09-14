/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

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
  public void setUp(){
    MockitoAnnotations.initMocks(this);

    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);
  }

  @Test
  public void whenDefiningShouldCreateMetricDefinition(){
    Builder builder = mock(Builder.class);
    when(definitionContext.newDefinitionBuilder()).thenReturn(builder);
    when(builder.setOutputMetrics("metrickey")).thenReturn(builder);

    this.measureComputer.define(definitionContext);
    verify(builder, times(1)).build();
  }

  @Test
  public void testWhenCallingComputeForIntMetricShouldComputeSumCorrectly(){
    TestHelper.setupComponentAndIntMeasure(context, Component.Type.FILE, 3, metric.key());
    this.measureComputer.compute(context);

    TestHelper.setupComponentAndIntMeasure(context, Component.Type.FILE, 2, metric.key());
    this.measureComputer.compute(context);

    TestHelper.setupProject(context);
    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 5);
  }

  @Test
  public void testWhenCallingComputeForFloatMetricShouldComputeSumCorrectly(){
    this.metric = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.FLOAT).create();
    this.measureComputer = new GenericMetricMeasureComputer(metric);

    TestHelper.setupComponentAndDoubleMeasure(context, Component.Type.FILE, 3.5, metric.key());
    this.measureComputer.compute(context);

    TestHelper.setupComponentAndDoubleMeasure(context, Component.Type.FILE, 2.5, metric.key());
    this.measureComputer.compute(context);

    TestHelper.setupProject(context);
    this.measureComputer.compute(context);

    verify(context).addMeasure(metric.key(), 6.0d);
  }

  @Test
  public void testWhenCallingComputeForUnsupportedMetricShouldNotAddMeasure(){
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
  public void testWhenCallingComputeWithUnexpectedComponentShouldNotAddMeasure(){
    TestHelper.setupView(context);

    this.measureComputer.compute(context);
    verify(context, times(0)).addMeasure(anyString(), anyInt());
  }

  @Test
  public void testWhenCallingComputeWithFileWithNoMeasureShouldNotThrow(){
    TestHelper.setupComponentAndIntMeasure(context, Component.Type.FILE, 2, metric.key());
    when(this.context.getMeasure(metric.key())).thenReturn(null);
    this.measureComputer.compute(context);

    verify(context, times(0)).addMeasure(anyString(), anyInt());
  }
}
