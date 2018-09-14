/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import static org.mockito.Mockito.*;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

public class TestHelper {

  public static String readResourceAsString(String resourceName){
    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    return new BufferedReader(reader).lines().collect(Collectors.joining());
  }

  public static Component setupComponentAndIntMeasure(MeasureComputer.MeasureComputerContext context,
    Component.Type type, int measure, String metricKey){

    Component component = mock(Component.class);
    when(component.getType()).thenReturn(type);

    Measure measureObj = mock(Measure.class);
    when(measureObj.getIntValue()).thenReturn(measure);

    when(context.getComponent()).thenReturn(component);
    when(context.getMeasure(metricKey)).thenReturn(measureObj);

    return component;
  }

  public static Component setupComponentAndDoubleMeasure(MeasureComputer.MeasureComputerContext context,
    Component.Type type, double measure, String metricKey){

    Component component = mock(Component.class);
    when(component.getType()).thenReturn(type);

    Measure measureObj = mock(Measure.class);
    when(measureObj.getDoubleValue()).thenReturn(measure);

    when(context.getComponent()).thenReturn(component);
    when(context.getMeasure(metricKey)).thenReturn(measureObj);

    return component;
  }

  public static Component setupComponentAndStringMeasure(MeasureComputer.MeasureComputerContext context,
    Component.Type type, String measure, String metricKey){

    Component component = mock(Component.class);
    when(component.getType()).thenReturn(type);

    Measure measureObj = mock(Measure.class);
    when(measureObj.getStringValue()).thenReturn(measure);

    when(context.getComponent()).thenReturn(component);
    when(context.getMeasure(metricKey)).thenReturn(measureObj);

    return component;
  }

  public static void addIntMeasure(MeasureComputer.MeasureComputerContext context,
          Component component, int measure, String metricKey){
    Measure measureObj = mock(Measure.class);
    when(measureObj.getIntValue()).thenReturn(measure);

    when(context.getMeasure(metricKey)).thenReturn(measureObj);
  }

  public static void setupProject(MeasureComputer.MeasureComputerContext context){
    Component component = mock(Component.class);
    when(component.getType()).thenReturn(Component.Type.PROJECT);
    when(context.getComponent()).thenReturn(component);
  }

  public static void setupView(MeasureComputer.MeasureComputerContext context){
    Component component = mock(Component.class);
    when(component.getType()).thenReturn(Component.Type.VIEW);
    when(context.getComponent()).thenReturn(component);
  }

}
