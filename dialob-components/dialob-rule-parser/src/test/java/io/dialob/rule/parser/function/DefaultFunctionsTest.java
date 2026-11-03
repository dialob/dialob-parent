/*
 * Copyright © 2015 - 2025 ReSys (info@dialob.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dialob.rule.parser.function;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

class DefaultFunctionsTest {
  @Test
  void testIsLyt() {
    assertTrue(DefaultFunctions.isLyt("1572860-0"));
    assertFalse(DefaultFunctions.isLyt("1572860-1"));
    assertFalse(DefaultFunctions.isLyt(null));
    assertFalse(DefaultFunctions.isLyt(""));
    assertFalse(DefaultFunctions.isLyt(" "));
    assertTrue(DefaultFunctions.isLyt("737546-2"));
    assertTrue(DefaultFunctions.isLyt("1961630-7"));
  }

  @Test
  void testHetu() {
    assertTrue(DefaultFunctions.isHetu("010170-555D"));
    assertFalse(DefaultFunctions.isHetu(null));
    assertFalse(DefaultFunctions.isHetu(""));
    assertFalse(DefaultFunctions.isHetu("010170-555"));
    assertFalse(DefaultFunctions.isHetu("014070-555D"));
    assertFalse(DefaultFunctions.isHetu("010170-5555D"));
    assertTrue(DefaultFunctions.isHetu("010170A555D"));
    assertFalse(DefaultFunctions.isHetu("010A70A555D"));
    assertFalse(DefaultFunctions.isHetu("010170AA55D"));
    assertTrue(DefaultFunctions.isHetu("010170+555D"));
    assertFalse(DefaultFunctions.isHetu("010170#555D"));
    assertTrue(DefaultFunctions.isHetu("010170A555D"));
    assertTrue(DefaultFunctions.isHetu("010170B555D"));
    assertTrue(DefaultFunctions.isHetu("010170C555D"));
    assertTrue(DefaultFunctions.isHetu("010170D555D"));
    assertTrue(DefaultFunctions.isHetu("010170E555D"));
    assertTrue(DefaultFunctions.isHetu("010170F555D"));
    assertFalse(DefaultFunctions.isHetu("010170G555D"));
    assertTrue(DefaultFunctions.isHetu("010170Y555D"));
    assertTrue(DefaultFunctions.isHetu("010170X555D"));
    assertTrue(DefaultFunctions.isHetu("010170W555D"));
    assertTrue(DefaultFunctions.isHetu("010170V555D"));
    assertTrue(DefaultFunctions.isHetu("010170U555D"));
    assertFalse(DefaultFunctions.isHetu("010170Z555D"));
  }

  @Test
  void testIsIban() {
    assertTrue(DefaultFunctions.isIban("DE91100000000123456789"));
    assertTrue(DefaultFunctions.isIban("BR1500000000000010932840814P2"));
    assertTrue(DefaultFunctions.isIban("SE45 5000 0000 0583 9825 7466"));
    assertTrue(DefaultFunctions.isIban("FI21 1234 5600 0007 85"));
    assertTrue(DefaultFunctions.isIban("EE38 2200 2210 2014 5685"));
    assertFalse(DefaultFunctions.isIban("SE45 5000 0000 0583 9825 7467"));
    assertFalse(DefaultFunctions.isIban(""));
    assertFalse(DefaultFunctions.isIban(null));
    assertFalse(DefaultFunctions.isIban("foo"));
  }

  @Test
  void testBirthDateFromHetu() {
    assertEquals(LocalDate.of(1870, 1, 1), DefaultFunctions.birthDateFromHetu("010170+555D"));
    assertEquals(LocalDate.of(1970, 1, 1), DefaultFunctions.birthDateFromHetu("010170-555D"));
    assertEquals(LocalDate.of(1970, 1, 1), DefaultFunctions.birthDateFromHetu("010170Y555D"));
    assertEquals(LocalDate.of(1970, 1, 1), DefaultFunctions.birthDateFromHetu("010170X555D"));
    assertEquals(LocalDate.of(1970, 1, 1), DefaultFunctions.birthDateFromHetu("010170W555D"));
    assertEquals(LocalDate.of(1970, 1, 1), DefaultFunctions.birthDateFromHetu("010170V555D"));
    assertEquals(LocalDate.of(1970, 1, 1), DefaultFunctions.birthDateFromHetu("010170U555D"));
    assertNull(DefaultFunctions.birthDateFromHetu("010170Z555D"));
    assertEquals(LocalDate.of(2070, 1, 1), DefaultFunctions.birthDateFromHetu("010170A555D"));
    assertEquals(LocalDate.of(2070, 1, 1), DefaultFunctions.birthDateFromHetu("010170B555D"));
    assertEquals(LocalDate.of(2070, 1, 1), DefaultFunctions.birthDateFromHetu("010170C555D"));
    assertEquals(LocalDate.of(2070, 1, 1), DefaultFunctions.birthDateFromHetu("010170D555D"));
    assertEquals(LocalDate.of(2070, 1, 1), DefaultFunctions.birthDateFromHetu("010170E555D"));
    assertEquals(LocalDate.of(2070, 1, 1), DefaultFunctions.birthDateFromHetu("010170F555D"));
    assertNull(DefaultFunctions.birthDateFromHetu("010170G555D"));
  }

  @Test
  void testCalculateTimeDiffDates() {
    LocalDate earlier = LocalDate.of(2020, 1, 1);
    LocalDate later = LocalDate.of(2020, 1, 3);
    assertEquals(BigInteger.valueOf(48), DefaultFunctions.calculateTimeDiffDates(later, earlier, "hours"));
    assertEquals(BigInteger.valueOf(48), DefaultFunctions.calculateTimeDiffDates(later, earlier, "Hours"));
    assertEquals(BigInteger.valueOf(48), DefaultFunctions.calculateTimeDiffDates(later, earlier, "hr"));
    assertEquals(BigInteger.valueOf(2), DefaultFunctions.calculateTimeDiffDates(later, earlier, "days"));
    assertEquals(BigInteger.valueOf(-48), DefaultFunctions.calculateTimeDiffDates(earlier, later, "hours"));
    assertEquals(BigInteger.valueOf(2), DefaultFunctions.calculateTimeDiffDates(earlier.plusDays(14), earlier, "weeks"));
    assertEquals(BigInteger.valueOf(1), DefaultFunctions.calculateTimeDiffDates(LocalDate.of(2021, 6, 1), earlier, "years"));
    assertEquals(BigInteger.valueOf(86400), DefaultFunctions.calculateTimeDiffDates(earlier.plusDays(1), earlier, "seconds"));
    assertNull(DefaultFunctions.calculateTimeDiffDates(null, earlier, "hours"));
    assertNull(DefaultFunctions.calculateTimeDiffDates(later, null, "hours"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.calculateTimeDiffDates(later, earlier, "fortnights"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.calculateTimeDiffDates(later, earlier, null));
  }

  @Test
  void testCalculateTimeDiffTimes() {
    LocalTime start = LocalTime.of(8, 0);
    LocalTime end = LocalTime.of(9, 30);
    assertEquals(BigInteger.valueOf(90), DefaultFunctions.calculateTimeDiffTimes(end, start, "minutes"));
    assertEquals(BigInteger.valueOf(90), DefaultFunctions.calculateTimeDiffTimes(end, start, "min"));
    assertEquals(BigInteger.valueOf(1), DefaultFunctions.calculateTimeDiffTimes(end, start, "hours"));
    assertEquals(BigInteger.valueOf(-90), DefaultFunctions.calculateTimeDiffTimes(start, end, "minutes"));
    assertEquals(BigInteger.valueOf(5400), DefaultFunctions.calculateTimeDiffTimes(end, start, "seconds"));
    assertNull(DefaultFunctions.calculateTimeDiffTimes(null, start, "minutes"));
    assertNull(DefaultFunctions.calculateTimeDiffTimes(end, null, "minutes"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.calculateTimeDiffTimes(end, start, "days"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.calculateTimeDiffTimes(end, start, "years"));
  }

  @Test
  void testConvertTimeDiffPeriod() {
    assertEquals(BigInteger.valueOf(336), DefaultFunctions.convertTimeDiffPeriod(Period.ofDays(14), "hours"));
    assertEquals(BigInteger.valueOf(1440), DefaultFunctions.convertTimeDiffPeriod(Period.ofMonths(2), "hours"));
    assertEquals(BigInteger.valueOf(840), DefaultFunctions.convertTimeDiffPeriod(Period.ofMonths(1).plusDays(5), "hours"));
    assertEquals(BigInteger.valueOf(18), DefaultFunctions.convertTimeDiffPeriod(Period.of(1, 6, 0), "months"));
    assertEquals(BigInteger.valueOf(1), DefaultFunctions.convertTimeDiffPeriod(Period.of(1, 6, 0), "years"));
    assertEquals(BigInteger.valueOf(1), DefaultFunctions.convertTimeDiffPeriod(Period.ofDays(10), "weeks"));
    assertEquals(BigInteger.valueOf(-336), DefaultFunctions.convertTimeDiffPeriod(Period.ofDays(-14), "hours"));
    assertEquals(BigInteger.valueOf(48), DefaultFunctions.convertTimeDiffPeriod(Period.ofDays(2), "HR"));
    assertNull(DefaultFunctions.convertTimeDiffPeriod(null, "hours"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.convertTimeDiffPeriod(Period.ofDays(1), "fortnights"));
  }

  @Test
  void testConvertTimeDiffDuration() {
    assertEquals(BigInteger.valueOf(1), DefaultFunctions.convertTimeDiffDuration(Duration.ofMinutes(90), "hours"));
    assertEquals(BigInteger.valueOf(90), DefaultFunctions.convertTimeDiffDuration(Duration.ofMinutes(90), "minutes"));
    assertEquals(BigInteger.valueOf(555), DefaultFunctions.convertTimeDiffDuration(Duration.ofHours(9).plusMinutes(15), "minutes"));
    assertEquals(BigInteger.valueOf(2), DefaultFunctions.convertTimeDiffDuration(Duration.ofHours(48), "days"));
    assertEquals(BigInteger.valueOf(1), DefaultFunctions.convertTimeDiffDuration(Duration.ofDays(10), "weeks"));
    assertEquals(BigInteger.valueOf(-1), DefaultFunctions.convertTimeDiffDuration(Duration.ofMinutes(-90), "hours"));
    assertNull(DefaultFunctions.convertTimeDiffDuration(null, "minutes"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.convertTimeDiffDuration(Duration.ofHours(1), "years"));
    assertThrows(IllegalArgumentException.class, () -> DefaultFunctions.convertTimeDiffDuration(Duration.ofHours(1), "months"));
  }


}
