/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import org.junit.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.sonar.api.Plugin.Context;

public class TestGenericMetricsPlugin {

  private GenericMetricsPlugin plugin;

  @Mock
  private Context context;

  @Mock
  private Environment environment;

  @Mock
  private FileReader fileReader;

  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);
    GenericMetrics.metrics = null;
    this.plugin = new GenericMetricsPlugin();
    this.plugin.setEnvironment(environment);
    this.plugin.setFileReader(fileReader);
  }

  @Test
  public void whenDefiningAndNoEnvironmentVariableShouldNotRegisterMeasureComputers(){
    this.plugin.define(context);

    verify(context).addExtension(GenericMetrics.class);
    verify(context).addExtension(GenericMetricsSensor.class);
    verify(context, times(2)).addExtension(any(Object.class));
  }

  @Test
  public void whenDefiningAndHasEnvironmentVariableShouldRegisterMeasureComputers(){
    GenericMetrics.metrics = null;
    when(environment.getEnvironmentVariable(GenericMetrics.CONFIG_ENV_VARIABLE)).thenReturn("filename");

    String json = TestHelper.readResourceAsString("measures1.json");
    when(fileReader.readFile("filename")).thenReturn(json);

    this.plugin.define(context);

    verify(context).addExtension(GenericMetrics.class);
    verify(context).addExtension(GenericMetricsSensor.class);
    verify(context, times(6)).addExtension(any(Object.class));
  }
}
