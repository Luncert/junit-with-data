package org.lks.junitwithdata.resourceresolve.strategy;

import org.apache.commons.lang3.StringUtils;
import org.lks.junitwithdata.resourceresolve.IResourceResolveStrategy;

public class PlainStrategy implements IResourceResolveStrategy {

  @Override
  public String resolveResourceDir(String resourceDir, Class<?> testClass, String resourceId) {
    if (StringUtils.isBlank(resourceId)) {
      resourceId = testClass.getName();
    }
    return resourceDir + FILE_SEPARATOR + resourceId;
  }
}
