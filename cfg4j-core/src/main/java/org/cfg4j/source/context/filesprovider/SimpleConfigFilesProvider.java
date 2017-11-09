package org.cfg4j.source.context.filesprovider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple provider implementation that
 */
public class SimpleConfigFilesProvider implements ConfigFilesProvider {

  private final List<Path> paths;

  SimpleConfigFilesProvider(String... files) {
    paths = new ArrayList<>(files.length);
    for (String file : files) {
      paths.add(Paths.get(file));
    }
  }

  @Override
  public Iterable<Path> getConfigFiles() {
    return paths;
  }

  @Override
  public String toString() {
    return "SimpleConfigFilesProvider{" +
      "paths=" + paths +
      '}';
  }

}