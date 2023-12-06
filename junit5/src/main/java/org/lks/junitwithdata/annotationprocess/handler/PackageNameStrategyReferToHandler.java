package org.lks.junitwithdata.annotationprocess.handler;

import org.lks.junitwithdata.annotation.PackageNameStrategyReferTo;
import org.lks.junitwithdata.annotation.internal.ResolveBefore;
import org.lks.junitwithdata.annotationprocess.IAnnotationHandler;
import org.lks.junitwithdata.session.TestSession;

@ResolveBefore(ResourceResolveHandler.class)
public class PackageNameStrategyReferToHandler
    implements IAnnotationHandler<PackageNameStrategyReferTo> {

  @Override
  public void process(TestSession session,
                      PackageNameStrategyReferTo annotation,
                      Class<?> targetClass) {
    Class<?> rootClass = annotation.value();
    session.setResourceResolveStrategyParams(new Object[]{rootClass.getPackageName()});
  }
}
