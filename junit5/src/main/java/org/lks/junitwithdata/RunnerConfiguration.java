package org.lks.junitwithdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class RunnerConfiguration {

  private static ObjectMapper parser;
  private static Charset charset = StandardCharsets.UTF_8;

  static {
    parser = new ObjectMapper(new YAMLFactory());
    parser.registerModule(new JavaTimeModule());
    parser.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    parser.setDateFormat(new SimpleDateFormat("yyyyMMdd"));
  }

  public static ObjectMapper getParser() {
    return parser;
  }

  public static void setParser(ObjectMapper parser) {
    Objects.requireNonNull(parser);
    RunnerConfiguration.parser = parser;
  }

  public static Charset getCharset() {
    return charset;
  }

  public static void setCharset(Charset charset) {
    Objects.requireNonNull(charset);
    RunnerConfiguration.charset = charset;
  }
}
