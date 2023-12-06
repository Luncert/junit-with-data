package org.lks.junitwithdata.annotationprocess.handler;

import org.lks.junitwithdata.annotation.ResourceResolve;
import org.lks.junitwithdata.annotationprocess.IAnnotationHandler;
import org.lks.junitwithdata.resourceresolve.IResourceResolveStrategy;
import org.lks.junitwithdata.resourceresolve.ResourceLoader;
import org.lks.junitwithdata.resourceresolve.ResourceType;
import org.lks.junitwithdata.resourceresolve.strategy.PlainStrategy;
import org.lks.junitwithdata.session.TestSession;

public class ResourceResolveHandler implements IAnnotationHandler<ResourceResolve> {

  @Override
  public void process(TestSession session, ResourceResolve annotation, Class<?> targetClass) {
    String resourceRootDir;
    Class<? extends IResourceResolveStrategy> strategyType;
    ResourceType[] supportedType;

    if (targetClass.isAnnotationPresent(ResourceResolve.class)) {
      ResourceResolve a = targetClass.getAnnotation(ResourceResolve.class);
      resourceRootDir = a.value();
      strategyType = a.strategy();
      supportedType = a.supportedType();
    } else {
      resourceRootDir = IResourceResolveStrategy.RESOURCE_DIR;
      strategyType = PlainStrategy.class;
      supportedType = new ResourceType[]{ResourceType.JSON, ResourceType.YAML};
    }

    session.setLoader(new ResourceLoader(session, resourceRootDir, strategyType, supportedType));
  }
}
