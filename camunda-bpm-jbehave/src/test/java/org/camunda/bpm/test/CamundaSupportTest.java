package org.camunda.bpm.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class CamundaSupportTest {

  @Test
  public void shouldParseObjectArray() {
    Object[] foo = new Object[] { "Key", Integer.valueOf(1), "Key2", "Value" };

    Map<String, Object> buildMap = CamundaSupport.buildMap(foo);
    assertEquals(2, buildMap.size());
    assertEquals(Integer.valueOf(1), buildMap.get("Key"));
    assertEquals("Value", buildMap.get("Key2"));
  }
}
