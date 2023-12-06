package org.lks.junitwithdata.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.lks.junitwithdata.common.exception.ResourceResolveException;

@Slf4j
public final class TestKit {

  private static final String FOLDER_UNIT_TEST = "unit-test";

  private static final String RESOURCE_PATH;

  private static String PACKAGE_NAME = "";

  private static final String FILE_SEPARATOR;

  // use this parser to serialize objects generates output in yaml format
  private static final ObjectMapper parser;

  private static final Cache<String, String> RESOURCE_CACHE =
      CacheBuilder.newBuilder().build();

  static {
    URL url = Objects
        .requireNonNull(resolveUrl("/"), "failed to resolve resource path");
    try {
      RESOURCE_PATH = Paths.get(url.toURI()).toFile().getPath();
    } catch (URISyntaxException e) {
      throw new ResourceResolveException(e);
    }

    if (File.separator.startsWith("/")) {
      FILE_SEPARATOR = File.separator;
    } else {
      FILE_SEPARATOR = File.separator + File.separator;
    }

    loadConfig();

    parser = new ObjectMapper(new YAMLFactory());
    parser.registerModule(new JavaTimeModule());
    parser.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    parser.setDateFormat(new SimpleDateFormat("yyyyMMdd"));
  }

  private static URL resolveUrl(String path) {
    try {
      URL url = TestKit.class.getResource(path);
      if (url == null) {
        url = TestKit.class.getClassLoader().getResource(path);
      }
      if (url == null) {
        url = ClassLoader.getSystemResource(path);
      }
      return url;
    } catch (IllegalArgumentException ex) {
      // Should not happen according to the JDK's contract:
      // see https://github.com/openjdk/jdk/pull/2662
      return null;
    }
  }

  private static void loadConfig() {
    URL url = resolveUrl("testkit.properties");
    if (url == null) {
      log.info("testkit.properties not found in classpath");
      return;
    }

    Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(url.getFile())) {
      cfg.load(inputStream);
    } catch (IOException e) {
      log.error(e.getMessage());
      return;
    }

    String ignoring = cfg.getProperty("test.class.path.ignoring");
    if (StringUtils.isNotBlank(ignoring)) {
      PACKAGE_NAME = Pattern.quote(ignoring);
    }
  }

  private TestKit() {
  }

  /**
   * Load test data as string.
   */
  @SneakyThrows
  public static String loadResource(String location) {
    return RESOURCE_CACHE.get(location, () -> {
      URL url = TestKit.class.getClassLoader().getResource(location);
      if (url == null) {
        throw new ResourceResolveException("invalid resource path " + location);
      }
      try {
        return IOUtils.toString(url, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Load test data as byte array.
   */
  public static byte[] loadResourceAsBytes(String location) {
    try {
      URL url = TestKit.class.getClassLoader().getResource(location);
      if (url == null) {
        throw new ResourceResolveException("invalid resource path " + location);
      }
      return IOUtils.toByteArray(url);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load test data as typed java object.
   */
  public static <T> T loadData(String location, Class<T> type) {
    String json = loadResource(location);
    try {
      return parser.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load test data as typed java object.
   */
  public static <T> T loadData(String location, TypeReference<T> type) {
    String json = loadResource(location);
    try {
      return parser.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T loadData(String location, JavaType type) {
    String json = loadResource(location);
    try {
      return parser.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load test data as json node.
   */
  public static JsonNode loadDataAsJsonNode(String location) {
    String json = loadResource(location);
    try {
      return parser.readTree(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static TestDataLoader load(Class<?> testCaseClass) {
    return new TestDataLoader(testCaseClass);
  }

  public static TestDataLoader load(Class<?> testCaseClass, String testDataFile) {
    return new TestDataLoader(testCaseClass, testDataFile);
  }

  public static TestDataLoader load(Object testCase) {
    return new TestDataLoader(testCase.getClass());
  }

  public static TestDataLoader load(Object testCase, String testDataFile) {
    return new TestDataLoader(testCase.getClass(), testDataFile);
  }

  public static class TestDataLoader {

    private final String resourcePath;

    @SneakyThrows
    public TestDataLoader(Class<?> testCaseClass, String testDataFile) {
      String relatedResDir = testCaseClass.getName()
          .replaceFirst(PACKAGE_NAME, "")
          .replaceAll("\\.", FILE_SEPARATOR);
      String resourceDir = Paths.get(FOLDER_UNIT_TEST, relatedResDir).toString();
      resourcePath = resolveResourcePath(resourceDir, testDataFile);
    }

    @SneakyThrows
    TestDataLoader(Class<?> testCaseClass) {
      String relatedResDir = testCaseClass.getPackageName()
          .replaceFirst(PACKAGE_NAME, "")
          .replaceAll("\\.", FILE_SEPARATOR);
      String resourceDir = Paths.get(FOLDER_UNIT_TEST, relatedResDir).toString();
      String resourceName = testCaseClass.getSimpleName();
      resourcePath = resolveResourcePath(resourceDir, resourceName);
    }

    private String resolveResourcePath(String resourceDir, String resourceName) throws IOException {
      List<Path> resources = Files.list(Paths.get(RESOURCE_PATH, resourceDir))
          .filter(path -> path.getFileName().toString().startsWith(resourceName))
          .filter(path -> path.toFile().isFile())
          .collect(Collectors.toList());

      if (resources.isEmpty()) {
        throw new RuntimeException("resource not found, path: "
            + resourceDir + File.separatorChar + resourceName + "*");
      }
      if (resources.size() > 1) {
        throw new RuntimeException("multiple resources found: " + resources);
      }

      return resourceDir + File.separatorChar + resources.get(0).getFileName().toString();
    }

    /**
     * Load test data as string.
     */
    public String asString() {
      return loadResource(resourcePath);
    }

    /**
     * Load test data as byte array.
     */
    public byte[] asBytes() {
      return loadResourceAsBytes(resourcePath);
    }

    /**
     * Load test data as typed java object.
     */
    public <T> T as(Class<T> type) {
      return loadData(resourcePath, type);
    }

    /**
     * Load test data as typed java object.
     */
    public <T> T as(TypeReference<T> type) {
      return loadData(resourcePath, type);
    }

    public <T> T as(JavaType type) {
      return loadData(resourcePath, type);
    }

    /**
     * Load test data as json node.
     */
    public JsonNode asNode() {
      return loadDataAsJsonNode(resourcePath);
    }

    /**
     * Extract test data to typed java object.
     */
    public <T> T extractAs(String path, Class<T> type) {
      try {
        JsonNode jsonNode = extractAsNode(path);
        return parser.readValue(jsonNode.traverse(), type);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * Extract test data to typed java object.
     */
    public <T> T extractAs(String path, TypeReference<T> type) {
      try {
        JsonNode jsonNode = extractAsNode(path);
        return parser.readValue(jsonNode.traverse(), type);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public <T> T extractAs(String path, JavaType type) {
      try {
        JsonNode jsonNode = extractAsNode(path);
        return parser.readValue(jsonNode.traverse(), type);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * Extract test data to json string.
     */
    public String extractAsString(String path) {
      return extractAsNode(path).toString();
    }

    /**
     * Extract test data to json node.
     */
    public JsonNode extractAsNode(String path) {
      JsonNode jsonNode = asNode();

      for (String name : path.split("\\.")) {
        if (StringUtils.isEmpty(name)) {
          throw new IllegalArgumentException("invalid json path \"" + path + "\"");
        }

        if (StringUtils.isNumeric(name)) {
          JsonNode tmp = jsonNode.get(Integer.parseInt(name));
          jsonNode = tmp != null ? tmp : jsonNode.get(name);
        } else {
          jsonNode = jsonNode.get(name);
        }

        if (jsonNode == null) {
          throw new IllegalArgumentException("invalid json path \"" + path + "\"");
        }
      }

      return jsonNode;
    }
  }

  public static ObjectMapper getParser() {
    return parser;
  }

  public static void assertEquals(Object actual, Object expect) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .withComparatorForType(TestKit::bigDecimalComparator, BigDecimal.class)
        .withComparatorForType(TestKit::zonedDateTimeComparator, ZonedDateTime.class)
        .ignoringExpectedNullFields()
        .isEqualTo(expect);
  }

  public static JavaType createListType(Class<?> elementType) {
    return parser.getTypeFactory().constructCollectionType(
        List.class, elementType);
  }

  public static JavaType createSetType(Class<?> elementType) {
    return parser.getTypeFactory().constructCollectionType(
        Set.class, elementType);
  }

  public static JavaType createMapType(Class<?> keyType, Class<?> valueType) {
    return parser.getTypeFactory().constructMapType(
        Map.class, keyType, valueType);
  }

  private static int zonedDateTimeComparator(ZonedDateTime a, ZonedDateTime b) {
    if (a != null && b != null) {
      // ignore time zone difference
      return a.toInstant().compareTo(b.toInstant());
    }
    return 1;
  }

  private static int bigDecimalComparator(BigDecimal a, BigDecimal b) {
    if (a != null && b != null) {
      return a.compareTo(b);
    }
    return 1;
  }
}
