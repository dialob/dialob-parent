package io.dialob.session.engine.program.expr;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputFormatterTest {

  @Test
  void test() {
    var formatter = new OutputFormatter("fi");
    assertEquals("1.1.2018", formatter.format(LocalDate.of(2018, 1, 1), null));
    assertEquals("12.15", formatter.format(LocalTime.of(12, 15, 10), null));
    assertEquals("2018", formatter.format(LocalDate.of(2018, 1, 1), "YYYY"));
    assertEquals("15", formatter.format(LocalTime.of(12, 15, 10), "mm"));
  }

}
