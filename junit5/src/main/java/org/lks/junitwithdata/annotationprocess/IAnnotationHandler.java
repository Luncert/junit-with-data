package org.lks.junitwithdata.annotationprocess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.lks.junitwithdata.session.TestSession;

public interface IAnnotationHandler<T extends Annotation> {

  default void process(TestSession session, T annotation, Class<?> targetClass) {
  }

  default void process(TestSession session, T annotation, Field field) {
  }

  default void process(TestSession session, T annotation, Method method) {
  }
}
