package org.lks.junitwithdata.resourceresolve;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.lks.junitwithdata.RunnerConfiguration;
import org.lks.junitwithdata.exception.ResourceResolveException;
import org.lks.junitwithdata.resourceresolve.strategy.ResourceResolveStrategyFactory;
import org.lks.junitwithdata.session.TestSession;

public class ResourceLoader extends AbstractResourceLoader implements Closeable {

  private static final Cache<String, byte[]> CACHE = CacheBuilder.newBuilder().build();

  private final TestSession session;
  private final String resourceRootDir;
  private final IResourceResolveStrategy strategy;
  private final Predicate<String> resourceTypeFilter;

  public ResourceLoader(
      TestSession session,
      String resourceRootDir,
      Class<? extends IResourceResolveStrategy> strategyType,
      ResourceType[] supportedTypes) {
    super(RunnerConfiguration.getParser(), RunnerConfiguration.getCharset());
    this.session = session;
    this.resourceRootDir = resourceRootDir;
    this.strategy = ResourceResolveStrategyFactory.create(
        strategyType, session.getResourceResolveStrategyParams());

    Set<ResourceType> typeSet = Sets.newHashSet(supportedTypes);
    if (typeSet.contains(ResourceType.ANY)) {
      resourceTypeFilter = e -> true;
    } else {
      Set<String> supportedExtensions = typeSet.stream()
          .map(t -> t.name().toLowerCase(Locale.ROOT))
          .collect(Collectors.toSet());
      resourceTypeFilter = supportedExtensions::contains;
    }
  }

  @SneakyThrows
  public Resource load(Class<?> testClass, String resourceId) {
    String resourcePath = strategy.resolveResourceDir(resourceRootDir, testClass, resourceId);
    String parentPath = FilenameUtils.getPath(resourcePath);
    String resourceName = FilenameUtils.getName(resourcePath);

    URL parent = ResourceLoader.class.getClassLoader().getResource(parentPath);
    if (parent == null) {
      throw new ResourceResolveException("invalid resource path " + resourcePath);
    }

    List<Path> resources = Files.list(Path.of(parent.toURI()))
        .filter(path -> {
          String filename = path.getFileName().toString();
          return filename.startsWith(resourceName)
              && resourceTypeFilter.test(FilenameUtils.getExtension(filename));
        })
        .toList();

    if (resources.isEmpty()) {
      throw new ResourceResolveException("resource missing, resolved path = "
          + resourcePath + ", check the path and the configured resource type");
    } else if (resources.size() > 1) {
      throw new ResourceResolveException("multiple resource matching condition: " + resources);
    }

    return new Resource(this, resources.get(0).toString());
  }

  @Override
  @SneakyThrows
  protected byte[] load(String location) {
    return CACHE.get(location, () -> {
      try {
        return FileUtils.readFileToByteArray(new File(location));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void close() {
    CACHE.cleanUp();
  }
}
