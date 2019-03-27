/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */
package org.sonar.generic.metrics;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestFileReaderImpl {

  @Test
  public void whenReadingFileAndFileDoesNotExistShouldReturnEmptyString(){
    FileReaderImpl fileReader = new FileReaderImpl();
    assertTrue(fileReader.readFile("").isEmpty());
  }

  @Test
  public void whenReadingFileAndFileExistsShouldReturnContent(){
    FileReaderImpl fileReader = new FileReaderImpl();

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("measures1.json").getFile());
    assertFalse(fileReader.readFile(file.getAbsolutePath()).equals(""));
  }
}
