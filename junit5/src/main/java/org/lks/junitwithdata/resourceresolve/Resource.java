package org.lks.junitwithdata.resourceresolve;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

public class Resource {

  private final AbstractResourceLoader loader;
  private final String resourcePath;

  Resource(AbstractResourceLoader loader, String resourcePath) {
    this.loader = loader;
    this.resourcePath = resourcePath;
  }

  /**
   * Load test data as string.
   */
  public String asString() {
    return loader.loadAsString(resourcePath);
  }

  /**
   * Load test data as byte array.
   */
  public byte[] asBytes() {
    return loader.load(resourcePath);
  }

  /**
   * Load test data as typed java object.
   */
  public <T> T as(Class<T> type) {
    return loader.loadAs(resourcePath, type);
  }

  /**
   * Load test data as typed java object.
   */
  public <T> T as(TypeReference<T> type) {
    return loader.loadAs(resourcePath, type);
  }

  public <T> T as(JavaType type) {
    return loader.loadAs(resourcePath, type);
  }

  /**
   * Load test data as json node.
   */
  public JsonNode asNode() {
    return loader.loadAsJsonNode(resourcePath);
  }

  /**
   * Extract test data to typed java object.
   */
  public <T> T extractAs(String path, Class<T> type) {
    return loader.extractAs(resourcePath, path, type);
  }

  /**
   * Extract test data to typed java object.
   */
  public <T> T extractAs(String path, TypeReference<T> type) {
    return loader.extractAs(resourcePath, path, type);
  }

  public <T> T extractAs(String path, JavaType type) {
    return loader.extractAs(resourcePath, path, type);
  }

  /**
   * Extract test data to json string.
   */
  public String extractAsString(String path) {
    return loader.extractAsString(resourcePath, path);
  }

  /**
   * Extract test data to json node.
   */
  public JsonNode extractAsNode(String path) {
    return loader.extractAsJsonNode(resourcePath, path);
  }
}
