package org.lks.junitwithdata.annotationprocess;

import java.lang.annotation.Annotation;
import lombok.Getter;

@Getter
public class HandlerPriority {

  private Class<? extends Annotation> prev;
  private Class<? extends Annotation> next;

  public HandlerPriority(Class<? extends Annotation> prev, Class<? extends Annotation> next) {
    this.prev = prev;
    this.next = next;
  }
}
