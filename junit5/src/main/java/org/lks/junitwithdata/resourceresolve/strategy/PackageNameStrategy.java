package org.lks.junitwithdata.resourceresolve.strategy;

import org.apache.commons.lang3.StringUtils;
import org.lks.junitwithdata.resourceresolve.IResourceResolveStrategy;

public class PackageNameStrategy implements IResourceResolveStrategy {

  private final String prefixToIgnore;

  public PackageNameStrategy() {
    prefixToIgnore = "";
  }

  public PackageNameStrategy(String prefixToIgnore) {
    this.prefixToIgnore = StringUtils.isBlank(prefixToIgnore) ? "" : prefixToIgnore;
  }

  @Override
  public String resolveResourceDir(String resourceDir, Class<?> testClass, String resourceId) {
    String dirPath = testClass.getPackageName()
        .replace(prefixToIgnore, "")
        .replace(".", FILE_SEPARATOR);

    String className = testClass.getSimpleName();
    if (StringUtils.isBlank(resourceId)) {
      resourceId = className;
    } else {
      if (StringUtils.isNotEmpty(dirPath)) {
        dirPath += FILE_SEPARATOR;
      }
      dirPath += className;
    }

    return resourceDir + FILE_SEPARATOR + dirPath + FILE_SEPARATOR + resourceId;
  }
}
