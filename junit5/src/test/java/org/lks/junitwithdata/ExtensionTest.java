package org.lks.junitwithdata;
//import lombok.Data;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//@ExtendWith(CommonExtension.class)
//@ResourceResolve(strategy = PackageNameStrategy.class)
//@PackageNameStrategyReferTo(Index.class)
//public class ExtensionTest {
//
//  @Resolve("example")
//  private Resource exampleData;
//
//  @Test
//  public void test() {
//    T item1 = exampleData.extractAs("item1", T.class);
//    Assertions.assertEquals("123", item1.name);
//  }
//
//  @Data
//  public static class T {
//    String name;
//  }
//}
