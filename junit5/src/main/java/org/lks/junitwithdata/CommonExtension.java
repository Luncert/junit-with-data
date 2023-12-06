package org.lks.junitwithdata;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.lks.junitwithdata.annotationprocess.AnnotationEngine;
import org.lks.junitwithdata.session.TestSession;

public class CommonExtension implements TestInstancePostProcessor, AfterAllCallback {

  private TestSession session;

  @Override
  public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
    session = new TestSession(o.getClass(), o);
    AnnotationEngine.process(session);
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    if (session != null) {
      session.close();
    }
  }
}
