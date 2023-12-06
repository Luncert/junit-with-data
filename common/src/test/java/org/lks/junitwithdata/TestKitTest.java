package org.lks.junitwithdata;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.Test;
import org.lks.junitwithdata.common.TestKit;

public class TestKitTest {

  @Test
  public void test() {
    List<String> data = TestKit.load(TestKitTest.class, "testCase1")
        .extractAs("data", new TypeReference<>() {
        });
    TestKit.assertEquals(data, List.of("Lukas", "Lukas", "Lukas"));
  }
}
