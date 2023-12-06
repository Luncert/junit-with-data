package org.lks.junitwithdata.resourceresolve;

public interface IResourceResolveStrategy {

  String RESOURCE_DIR = "unit-test";

  String FILE_SEPARATOR = "/";

  String resolveResourceDir(String resourceDir, Class<?> testClass, String resourceId);
}
