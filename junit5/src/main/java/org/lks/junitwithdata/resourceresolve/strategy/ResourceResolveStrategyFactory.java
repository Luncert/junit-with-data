package org.lks.junitwithdata.resourceresolve.strategy;

import java.util.Arrays;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.lks.junitwithdata.resourceresolve.IResourceResolveStrategy;

public class ResourceResolveStrategyFactory {

  @SneakyThrows
  public static IResourceResolveStrategy create(
      Class<? extends IResourceResolveStrategy> type, Object... params) {
    if (ArrayUtils.isNotEmpty(params)) {
      Class<?>[] types = Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
      return type.getDeclaredConstructor(types).newInstance(params);
    }

    try {
      return type.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException e) {
      throw new NoSuchMethodException("no default constructor found in " + type.getName());
    }
  }
}
