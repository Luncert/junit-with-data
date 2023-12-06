package org.lks.junitwithdata.util;

import com.fasterxml.jackson.databind.JavaType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.lks.junitwithdata.RunnerConfiguration;

public final class TestKit {

  public static void assertEquals(Object actual, Object expect) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .withComparatorForType(TestKit::bigDecimalComparator, BigDecimal.class)
        .withComparatorForType(TestKit::zonedDateTimeComparator, ZonedDateTime.class)
        .ignoringExpectedNullFields()
        .isEqualTo(expect);
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

  public static JavaType createListType(Class<?> elementType) {
    return RunnerConfiguration.getParser().getTypeFactory().constructCollectionType(
        List.class, elementType);
  }

  public static JavaType createSetType(Class<?> elementType) {
    return RunnerConfiguration.getParser().getTypeFactory().constructCollectionType(
        Set.class, elementType);
  }

  public static JavaType createMapType(Class<?> keyType, Class<?> valueType) {
    return RunnerConfiguration.getParser().getTypeFactory().constructMapType(
        Map.class, keyType, valueType);
  }
}
