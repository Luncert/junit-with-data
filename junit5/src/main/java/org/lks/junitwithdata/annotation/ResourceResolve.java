package org.lks.junitwithdata.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.lks.junitwithdata.resourceresolve.IResourceResolveStrategy;
import org.lks.junitwithdata.resourceresolve.ResourceType;
import org.lks.junitwithdata.resourceresolve.strategy.PlainStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ResourceResolve {

  /**
   * Resource root path.
   */
  String value() default IResourceResolveStrategy.RESOURCE_DIR;

  /**
   * Resolving strategy.
   */
  Class<? extends IResourceResolveStrategy> strategy() default PlainStrategy.class;

  /**
   * Default is {@link ResourceType#JSON}, {@link ResourceType#YAML}.
   */
  ResourceType[] supportedType() default {ResourceType.JSON, ResourceType.YAML};
}
