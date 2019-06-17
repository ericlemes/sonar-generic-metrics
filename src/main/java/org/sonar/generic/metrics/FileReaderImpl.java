/**
 * Sonar Generic Metrics
 * Copyright (C) 2018
 * http://github.com/ericlemes/sonar-generic-metrics
 */

package org.sonar.generic.metrics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.sonar.api.utils.log.Logger;

public class FileReaderImpl implements FileReader {

  private final Logger LOG = LoggerWithDebugCheck.get(FileReaderImpl.class);

  @Override
  public String readFile(String fileName) {
    try {
      InputStream stream = new FileInputStream(fileName);
      InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
      return new BufferedReader(reader).lines().collect(Collectors.joining());
    }
    catch (FileNotFoundException e){
      LOG.error("Could not read file: " + fileName);
      return "";
    }
    
  }
}
