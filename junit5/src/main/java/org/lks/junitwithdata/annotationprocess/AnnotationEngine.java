package org.lks.junitwithdata.annotationprocess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lks.junitwithdata.annotationprocess.handler.PackageNameStrategyReferToHandler;
import org.lks.junitwithdata.annotationprocess.handler.ResolveHandler;
import org.lks.junitwithdata.annotationprocess.handler.ResourceResolveHandler;
import org.lks.junitwithdata.exception.AnnotationEngineConfigException;
import org.lks.junitwithdata.session.TestSession;

public class AnnotationEngine {

  private enum ConfigPhase {
    REGISTER_HANDLERS,
    FROZE
  }

  private static ConfigPhase configPhase = ConfigPhase.REGISTER_HANDLERS;
  private static final List<IAnnotationHandler<?>> handlers = new ArrayList<>();
  private static final Map<Class<? extends Annotation>, Integer> handlerIndex = new HashMap<>();

  static {
    register(new PackageNameStrategyReferToHandler());
    register(new ResourceResolveHandler());

    register(new ResolveHandler());
  }

  public static void register(IAnnotationHandler<?> handler) {
    checkPhase(ConfigPhase.REGISTER_HANDLERS);

    Class<?> handlerType = handler.getClass();
    Class<? extends Annotation> annotationType = getAnnotationType(handlerType);

    if (annotationType == null) {
      throw new IllegalArgumentException("invalid annotation handler");
    }
    if (handlerIndex.containsKey(annotationType)) {
      throw new IllegalArgumentException("duplicated annotation handler");
    }

    handlerIndex.put(annotationType, handlers.size());
    handlers.add(handler);
  }

  /**
   * Processes the test instance to configure annotated members.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void process(TestSession session) {
    if (configPhase == ConfigPhase.REGISTER_HANDLERS) {
      configPhase = ConfigPhase.FROZE;
    } else {
      checkPhase(ConfigPhase.FROZE);
    }

    Class<?> type = session.getTargetClass();
    Arrays.stream(type.getAnnotations())
        .filter(annotation -> handlerIndex.containsKey(annotation.annotationType()))
        .sorted(Comparator.comparingInt(
            annotation -> handlerIndex.get(annotation.annotationType())))
        .forEach(annotation -> {
          IAnnotationHandler handler = handlers.get(handlerIndex.get(annotation.annotationType()));
          handler.process(session, annotation, type);
        });

    for (Field field : type.getDeclaredFields()) {
      Arrays.stream(field.getAnnotations())
          .filter(annotation -> handlerIndex.containsKey(annotation.annotationType()))
          .sorted(Comparator.comparingInt(
              annotation -> handlerIndex.get(annotation.annotationType())))
          .forEach(annotation -> {
            IAnnotationHandler handler = handlers.get(
                handlerIndex.get(annotation.annotationType()));
            handler.process(session, annotation, field);
          });
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends Annotation> getAnnotationType(Class<?> handlerType) {
    for (Type genericInterface : handlerType.getGenericInterfaces()) {
      ParameterizedType p = (ParameterizedType) genericInterface;
      if (IAnnotationHandler.class.isAssignableFrom((Class<?>) p.getRawType())) {
        return (Class<? extends Annotation>) p.getActualTypeArguments()[0];
      }
    }

    return null;
  }

  private static void checkPhase(ConfigPhase expected) {
    if (configPhase != expected) {
      AnnotationEngineConfigException e;
      if (expected == ConfigPhase.REGISTER_HANDLERS) {
        e = new AnnotationEngineConfigException("cannot register annotation handler now");
      } else {
        e = new AnnotationEngineConfigException("unexpected error");
      }
      throw e;
    }
  }
}
