/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;

public class TestGenericMetrics {
  private GenericMetrics genericMetrics;

  @Mock
  private Configuration configurationMock;

  @Mock
  private FileReader fileReader;

  @Mock
  private Environment environment;

  @Before
  public void SetUp(){
    MockitoAnnotations.initMocks(this);

    GenericMetrics.metrics = null;

    genericMetrics = new GenericMetrics(this.configurationMock);
    genericMetrics.setFileReader(fileReader);
    genericMetrics.setEnvironment(environment);
  }

  @Test
  public void whenGetMetricsAndNoConfigurationPropertiesShouldReturnEmptyArray(){
    List<Metric> metrics = this.genericMetrics.getMetrics();
    Assert.assertEquals(0, metrics.size());
  }

  @Test
  public void whenGetMetricsAndHasCachedMetricsShouldReturnCachedMetrics(){
    ArrayList<Metric> metrics = new ArrayList<>();
    GenericMetrics.metrics = metrics;

    assertEquals(metrics, genericMetrics.getMetrics());
  }

  @Test
  public void whenGetMetricsAndHasConfigurationPropertyShouldReturnMetrics(){
    String json = TestHelper.readResourceAsString("measures1.json");
    when(fileReader.readFile("filename")).thenReturn(json);

    when(configurationMock.get(GenericMetrics.JSON_DATA_PROPERTY)).thenReturn(Optional.of("filename"));

    List<Metric> metrics = genericMetrics.getMetrics();
    assertEquals(4, metrics.size());
    assertEquals("metric1", metrics.get(0).getKey());
    assertEquals("Metric 1", metrics.get(0).getName());
    assertEquals(Metric.ValueType.INT, metrics.get(0).getType());
    assertEquals("Description Metric 1", metrics.get(0).getDescription());
    assertTrue(Metric.DIRECTION_WORST == metrics.get(0).getDirection());
    assertFalse(metrics.get(0).getQualitative());
    assertEquals("Generic Metric", metrics.get(0).getDomain());
  }

  @Test
  public void whenGetMetricsAndNoDataPropertyShouldReturnEmptyArray(){
    when(configurationMock.get(GenericMetrics.JSON_DATA_PROPERTY)).thenReturn(Optional.empty());

    List<Metric> metrics = genericMetrics.getMetrics();
    assertEquals(0, metrics.size());
  }

  @Test
  public void whenGetAllMetricsAndHasCachedMetricsShouldReturnCachedMetrics(){
    ArrayList<Metric> metrics = new ArrayList<>();
    GenericMetrics.metrics = metrics;

    assertEquals(metrics, GenericMetrics.getAllMetrics(environment, fileReader));
  }

  @Test
  public void whenGetAllMetricsAndNoCachedMetricsAndNoEnvironmentVariableShouldReturnNull(){
    GenericMetrics.metrics = null;
    when(environment.getEnvironmentVariable(GenericMetrics.CONFIG_ENV_VARIABLE)).thenReturn(null);
    List<Metric> metrics = GenericMetrics.getAllMetrics(environment, fileReader);

    assertNull(metrics);
  }

  @Test
  public void whenGetAllMetricsAndNoCachedMetricsAndEmptyEnvironmentVariableShouldReturnNull(){
    GenericMetrics.metrics = null;
    when(environment.getEnvironmentVariable(GenericMetrics.CONFIG_ENV_VARIABLE)).thenReturn("");
    List<Metric> metrics = GenericMetrics.getAllMetrics(environment, fileReader);

    assertNull(metrics);
  }

  @Test
  public void whenGetAllMetricsAndNoCachedMetricsAndHasEnvironmentVariableShouldReturnMetrics(){
    GenericMetrics.metrics = null;
    when(environment.getEnvironmentVariable(GenericMetrics.CONFIG_ENV_VARIABLE)).thenReturn("filename");

    String json = TestHelper.readResourceAsString("measures1.json");
    when(fileReader.readFile("filename")).thenReturn(json);
    when(configurationMock.get(GenericMetrics.JSON_DATA_PROPERTY)).thenReturn(Optional.of("filename"));

    List<Metric> metrics = GenericMetrics.getAllMetrics(environment, fileReader);

    assertEquals(4, metrics.size());
  }

  @Test
  public void whenGetMetricAndDoesNotExistShouldReturnNull(){
    GenericMetrics.metrics = new ArrayList<>();
    Metric m = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    GenericMetrics.metrics.add(m);
    assertNull(GenericMetrics.getMetric("non-existent-key"));
  }

  @Test
  public void whenGetMetricAndExistsShouldReturnMetric(){
    GenericMetrics.metrics = new ArrayList<>();
    Metric m = new org.sonar.api.measures.Metric.Builder("metrickey", "Name", Metric.ValueType.INT).create();
    GenericMetrics.metrics.add(m);
    assertEquals(m, GenericMetrics.getMetric("metrickey"));
  }
}
