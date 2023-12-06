package org.lks.junitwithdata.annotation.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.lks.junitwithdata.annotationprocess.IAnnotationHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ResolveBefore {

  Class<? extends IAnnotationHandler<?>> value();
}
