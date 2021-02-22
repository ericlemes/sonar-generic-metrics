/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.junit.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.fs.*;
import org.sonar.api.batch.sensor.*;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;

public class TestGenericMetricsSensor {

  private GenericMetricsSensor sensor;

  @Mock
  private SensorDescriptor descriptor;

  @Mock
  private Configuration config;

  @Mock
  private SensorContext sensorContext;

  @Mock
  private FileReader fileReader;

  @Mock
  private FileSystem fileSystem;

  @Mock
  private FilePredicates filePredicates;
  
  @Mock
  private InputModule sensorContextModule;

  @Before
  public void SetUp(){
    MockitoAnnotations.initMocks(this);
    sensor = new GenericMetricsSensor(this.config);
    sensor.setFileReader(fileReader);

    when(sensorContext.fileSystem()).thenReturn(fileSystem);
    when(fileSystem.predicates()).thenReturn(filePredicates);

    when(config.get(GenericMetrics.JSON_DATA_PROPERTY)).thenReturn(Optional.of("filename"));

    NewMeasure measure = setupMockNewMeasure();
    when(sensorContext.newMeasure()).thenReturn(measure);
    
    when(sensorContext.module()).thenReturn(sensorContextModule);
  }

  private NewMeasure setupMockNewMeasure(){
    NewMeasure newMeasure = mock(NewMeasure.class);
    when(newMeasure.on(any(InputComponent.class))).thenReturn(newMeasure);
    when(newMeasure.withValue(any(Integer.class))).thenReturn(newMeasure);
    when(newMeasure.withValue(any(Float.class))).thenReturn(newMeasure);
    when(newMeasure.withValue(any(Double.class))).thenReturn(newMeasure);
    when(newMeasure.withValue(any(String.class))).thenReturn(newMeasure);
    when(newMeasure.forMetric(any(Metric.class))).thenReturn(newMeasure);
    return newMeasure;
  }

  @Test
  public void whenDefiningShouldAppendNameAndFileTypeOnDescriptor(){
    when(descriptor.name("Generic Metrics Sensor")).thenReturn(descriptor);

    sensor.describe(descriptor);

    verify(descriptor, times(1)).name("Generic Metrics Sensor");
    verify(descriptor, times(1)).onlyOnFileType(InputFile.Type.MAIN);
  }

  @Test(expected = JSONException.class)
  public void whenExecutingAndHasCorruptFileShouldThrow() throws FileNotFoundException{
    String json = "this a badly formatted json";
    when(fileReader.readFile("filename")).thenReturn(json);

    sensor.execute(this.sensorContext);
    verify(fileReader, times(1)).readFile("filename");
  }

  @Test
  public void whenExecutingAndNoFileNameShouldNotThrow(){
    when(config.get(GenericMetrics.JSON_DATA_PROPERTY)).thenReturn(Optional.empty());
    sensor.execute(sensorContext);
    verify(fileReader, times(0)).readFile("filename");
  }

  private String readResourceAsString(String resourceName){
    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    return new BufferedReader(reader).lines().collect(Collectors.joining());
  }

  private void readMetrics(){
    //Reuse existing logic to populate global metrics cache.
    GenericMetrics g = new GenericMetrics(this.config);
    g.setFileReader(this.fileReader);
    g.getMetrics();
  }

  @Test
  public void whenExecutingAndCanReadFileShouldPublishMetrics() throws IOException{
    FilePredicate filePredicate1 = mock(FilePredicate.class);
    FilePredicate filePredicate2 = mock(FilePredicate.class);

    when(filePredicates.is(any(File.class))).thenReturn(filePredicate1, filePredicate2);

    InputFile inputFile1 = mock(InputFile.class);
    InputFile inputFile2 = mock(InputFile.class);

    when(fileSystem.inputFile(filePredicate1)).thenReturn(inputFile1);
    when(fileSystem.inputFile(filePredicate2)).thenReturn(inputFile2);

    String json = readResourceAsString("measures1.json");
    when(fileReader.readFile("filename")).thenReturn(json);

    GenericMetrics.metrics = null;

    readMetrics();

    sensor.execute(this.sensorContext);

    verify(sensorContext, times(10)).newMeasure();
  }

  @Test
  public void whenExecutingAndCanReadFileAndHasUnsupportedMetricShouldNotPublishMetrics() throws IOException{
    FilePredicate filePredicate1 = mock(FilePredicate.class);
    FilePredicate filePredicate2 = mock(FilePredicate.class);

    when(filePredicates.is(any(File.class))).thenReturn(filePredicate1, filePredicate2);

    InputFile inputFile1 = mock(InputFile.class);
    InputFile inputFile2 = mock(InputFile.class);

    when(fileSystem.inputFile(filePredicate1)).thenReturn(inputFile1);
    when(fileSystem.inputFile(filePredicate2)).thenReturn(inputFile2);

    String json = readResourceAsString("measures2.json");
    when(fileReader.readFile("filename")).thenReturn(json);

    GenericMetrics.metrics = null;
    readMetrics();

    sensor.execute(this.sensorContext);

    verify(sensorContext, times(0)).newMeasure();
  }

  @Test
  public void whenExecutingAndCanReadFileAndNoMetricShouldNotPublishMetrics() throws IOException{
    GenericMetrics.metrics = null;

    FilePredicate filePredicate1 = mock(FilePredicate.class);
    when(filePredicates.is(any(File.class))).thenReturn(filePredicate1);
    InputFile inputFile1 = mock(InputFile.class);
    when(fileSystem.inputFile(filePredicate1)).thenReturn(inputFile1);

    String json = readResourceAsString("measures1.json");
    when(fileReader.readFile("filename")).thenReturn(json);

    sensor.execute(this.sensorContext);

    verify(sensorContext, times(0)).newMeasure();
  }

  @Test
  public void whenExecutingAndCanReadFileAndFileInJsonNotFoundShouldNotPublishMetrics() {
    FilePredicate filePredicate1 = mock(FilePredicate.class);
    when(filePredicates.is(any(File.class))).thenReturn(filePredicate1);

    InputFile inputFile1 = mock(InputFile.class);

    when(fileSystem.inputFile(filePredicate1)).thenReturn(null);

    String json = readResourceAsString("measures1.json");
    when(fileReader.readFile("filename")).thenReturn(json);

    readMetrics();

    sensor.execute(this.sensorContext);
    verify(sensorContext, times(1)).newMeasure();
  }
}
