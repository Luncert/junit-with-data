package org.lks.junitwithdata.annotationprocess.handler;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import org.junit.platform.commons.util.ReflectionUtils;
import org.lks.junitwithdata.annotation.Resolve;
import org.lks.junitwithdata.annotationprocess.IAnnotationHandler;
import org.lks.junitwithdata.exception.ResourceResolveException;
import org.lks.junitwithdata.resourceresolve.Resource;
import org.lks.junitwithdata.session.TestSession;

public class ResolveHandler implements IAnnotationHandler<Resolve> {

  private final Map<Class<?>, Function<Resource, Object>> typeHandlers =
      ImmutableMap.<Class<?>, Function<Resource, Object>>builder()
          .put(Resource.class, resource -> resource)
          .put(String.class, Resource::asString)
          .put(byte[].class, Resource::asBytes)
          .build();

  @Override
  public void process(TestSession session, Resolve annotation, Field field) {
    String resourceId = annotation.value();
    Resource resource = session.getLoader().load(session.getTargetClass(), resourceId);
    ReflectionUtils.makeAccessible(field);

    Class<?> fieldType = field.getType();
    try {
      Object value;

      Function<Resource, Object> typeHandler = typeHandlers.get(fieldType);
      if (typeHandler == null) {
        value = resource.as(fieldType);
      } else {
        value = typeHandler.apply(resource);
      }

      field.set(session.getInstance(), value);
    } catch (IllegalAccessException e) {
      throw new ResourceResolveException(e);
    }
  }
}
