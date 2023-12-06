package org.lks.junitwithdata.session;

import lombok.Getter;
import lombok.Setter;
import org.lks.junitwithdata.resourceresolve.ResourceLoader;

public class TestSession implements AutoCloseable {

  @Getter
  private final Class<?> targetClass;

  @Getter
  private final Object instance;

  @Setter
  @Getter
  private ResourceLoader loader;

  @Setter
  @Getter
  private Object[] resourceResolveStrategyParams;

  public TestSession(Class<?> targetClass, Object instance) {
    this.targetClass = targetClass;
    this.instance = instance;
  }

  @Override
  public void close() {
    if (loader != null) {
      loader.close();
    }
  }
}
