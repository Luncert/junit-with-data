package org.lks.junitwithdata.resourceresolve;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractResourceLoader {

  private final ObjectMapper parser;
  private final Charset charset;

  public AbstractResourceLoader(ObjectMapper parser, Charset charset) {
    this.parser = parser;
    this.charset = charset;
  }

  protected abstract byte[] load(String location);

  /**
   * Load test data as string.
   */
  public String loadAsString(String location) {
    return new String(load(location), charset);
  }

  /**
   * Load test data as byte array.
   */
  public byte[] loadAsBytes(String location) {
    return load(location);
  }

  /**
   * Load test data as typed java object.
   */
  public <T> T loadAs(String location, Class<T> type) {
    try {
      return parser.readValue(load(location), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load test data as typed java object.
   */
  public <T> T loadAs(String location, TypeReference<T> type) {
    try {
      return parser.readValue(load(location), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load test data as typed java object.
   */
  public <T> T loadAs(String location, JavaType type) {
    try {
      return parser.readValue(load(location), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load test data as json node.
   */
  public JsonNode loadAsJsonNode(String location) {
    try {
      return parser.readTree(load(location));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Extract test data to typed java object.
   */
  public <T> T extractAs(String location, String path, Class<T> type) {
    try {
      JsonNode jsonNode = extractAsJsonNode(location, path);
      return parser.readValue(jsonNode.traverse(), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Extract test data to typed java object.
   */
  public <T> T extractAs(String location, String path, TypeReference<T> type) {
    try {
      JsonNode jsonNode = extractAsJsonNode(location, path);
      return parser.readValue(jsonNode.traverse(), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Extract test data to typed java object.
   */
  public <T> T extractAs(String location, String path, JavaType type) {
    try {
      JsonNode jsonNode = extractAsJsonNode(location, path);
      return parser.readValue(jsonNode.traverse(), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Extract test data to json string.
   */
  public String extractAsString(String location, String path) {
    return extractAsJsonNode(location, path).toString();
  }

  /**
   * Extract test data to json node.
   */
  public JsonNode extractAsJsonNode(String location, String path) {
    JsonNode jsonNode = loadAsJsonNode(location);

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
