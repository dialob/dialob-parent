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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.IBANValidator;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DefaultFunctions {

  public DefaultFunctions(FunctionRegistry functionRegistry) {
    functionRegistry.configureFunction("today", "now", LocalDate.class, false);
    functionRegistry.configureFunction("now", "now", LocalTime.class, false);
    functionRegistry.configureFunction("lengthOf", "lengthOf", DefaultFunctions.class, false);
    functionRegistry.configureFunction("isLyt", "isLyt", DefaultFunctions.class, false);
    functionRegistry.configureFunction("isNotLyt", "isNotLyt", DefaultFunctions.class, false);
    functionRegistry.configureFunction("isHetu", "isHetu", DefaultFunctions.class, false);
    functionRegistry.configureFunction("isNotHetu", "isNotHetu", DefaultFunctions.class, false);
    functionRegistry.configureFunction("count", "count", DefaultFunctions.class, false);
    functionRegistry.configureFunction("birthDateFromHetu", "birthDateFromHetu", DefaultFunctions.class, false);
    functionRegistry.configureFunction("isIban", "isIban", DefaultFunctions.class, false);
    functionRegistry.configureFunction("isNotIban", "isNotIban", DefaultFunctions.class, false);
    functionRegistry.configureFunction("calculateTimeDiff", "calculateTimeDiffDates", DefaultFunctions.class, false);
    functionRegistry.configureFunction("calculateTimeDiff", "calculateTimeDiffTimes", DefaultFunctions.class, false);
    functionRegistry.configureFunction("convertTimeDiff", "convertTimeDiffPeriod", DefaultFunctions.class, false);
    functionRegistry.configureFunction("convertTimeDiff", "convertTimeDiffDuration", DefaultFunctions.class, false);
  }

  public static boolean isIban(String iban) {
    return IBANValidator.getInstance().isValid(StringUtils.deleteWhitespace(iban));
  }

  public static boolean isNotIban(String iban) {
    return !isIban(iban);
  }

  public static BigInteger lengthOf(String s) {
    if (s == null) {
      return BigInteger.ZERO;
    }
    return BigInteger.valueOf(s.length());
  }

  public static boolean isNotLyt(String lyt) {
    return !isLyt(lyt);
  }

  public static int count(Object[] list) {
    if (list == null) {
      return 0;
    }
    return list.length;
  }

  public static boolean isLyt(String lyt) {
    if (StringUtils.isBlank(lyt)) {
      return false;
    }
    lyt = lyt.trim();
    if (lyt.length() > 9) {
      return false;
    }
    if (lyt.length() < 9) {
      lyt = "00000000".substring(0, 9 - lyt.length()) + lyt;
    }
    if (!lyt.matches("\\d{7}-\\d")) {
      return false;
    }

    int[] factors = {7, 9, 10, 5, 8, 4, 2};
    int sum = 0;
    for (int i = 0; i < 7; i++) {
      sum += factors[i] * (lyt.charAt(i) - '0');
    }
    int reminder = sum % 11;
    if (reminder > 0) {
      reminder = 11 - reminder;
    }
    return (lyt.charAt(8) - '0') == reminder;
  }

  public static boolean isNotHetu(String hetu) {
    return !isHetu(hetu);
  }

  public static boolean isHetu(String hetu) {
    if (StringUtils.isBlank(hetu)) {
      return false;
    }
    if (hetu.length() != 11) {
      return false;
    }
    hetu = hetu.toUpperCase();
    String date = hetu.substring(0,6);
    String seq = hetu.substring(7,10);
    if (!date.matches("\\d{6}")) {
      return false;
    }
    if (!seq.matches("\\d{3}")) {
      return false;
    }
    LocalDate birthDate =  birthDateFromHetu(hetu);
    if (birthDate == null) {
      return false;
    }
    int reminder = Integer.parseInt(date + seq) % 31;
    char checkCode = "0123456789ABCDEFHJKLMNPRSTUVWXY".charAt(reminder);
    return checkCode == hetu.charAt(10);
  }

  public static LocalDate birthDateFromHetu(String hetu) {
    if (StringUtils.isBlank(hetu)) {
      return null;
    }
    if (hetu.length() != 11) {
      return null;
    }
    try {
      hetu = hetu.toUpperCase();
      int day = Integer.parseInt(hetu.substring(0,2));
      int month = Integer.parseInt(hetu.substring(2,4));
      int year = Integer.parseInt(hetu.substring(4,6));
      char middlechar = hetu.charAt(6);
      if (middlechar == '+') {
        year += 1800;
      } else if (middlechar == '-' || (middlechar >= 'U' && middlechar <= 'Y')) {
        year += 1900;
      } else if (middlechar >= 'A' && middlechar <= 'F') {
        year += 2000;
      } else {
        return null;
      }
      return LocalDate.of(year,month,day);
    } catch(Exception dte) {
      // Exception means invalid hetu
    }
    return null;
  }

  public static BigInteger calculateTimeDiffDates(LocalDate a, LocalDate b, String unit) {
    if (a == null || b == null) {
      return null;
    }
    long amount = switch (TimeDiffUnit.from(unit)) {
      case YEARS -> ChronoUnit.YEARS.between(b, a);
      case MONTHS -> ChronoUnit.MONTHS.between(b, a);
      case WEEKS -> ChronoUnit.WEEKS.between(b, a);
      case DAYS -> ChronoUnit.DAYS.between(b, a);
      case HOURS -> ChronoUnit.HOURS.between(b.atStartOfDay(), a.atStartOfDay());
      case MINUTES -> ChronoUnit.MINUTES.between(b.atStartOfDay(), a.atStartOfDay());
      case SECONDS -> ChronoUnit.SECONDS.between(b.atStartOfDay(), a.atStartOfDay());
    };
    return BigInteger.valueOf(amount);
  }

  public static BigInteger calculateTimeDiffTimes(LocalTime a, LocalTime b, String unit) {
    if (a == null || b == null) {
      return null;
    }
    long amount = switch (TimeDiffUnit.from(unit)) {
      case HOURS -> ChronoUnit.HOURS.between(b, a);
      case MINUTES -> ChronoUnit.MINUTES.between(b, a);
      case SECONDS -> ChronoUnit.SECONDS.between(b, a);
      default -> throw unsupportedUnit(unit, "a time");
    };
    return BigInteger.valueOf(amount);
  }

  public static BigInteger convertTimeDiffPeriod(Period period, String unit) {
    if (period == null) {
      return null;
    }
    long totalMonths = period.toTotalMonths();
    long totalDays = totalMonths * 30L + period.getDays();
    long amount = switch (TimeDiffUnit.from(unit)) {
      case YEARS -> totalMonths / 12;
      case MONTHS -> totalMonths;
      case WEEKS -> totalDays / 7;
      case DAYS -> totalDays;
      case HOURS -> totalDays * 24L;
      case MINUTES -> totalDays * 24L * 60L;
      case SECONDS -> totalDays * 24L * 60L * 60L;
    };
    return BigInteger.valueOf(amount);
  }

  public static BigInteger convertTimeDiffDuration(Duration duration, String unit) {
    if (duration == null) {
      return null;
    }
    long amount = switch (TimeDiffUnit.from(unit)) {
      case SECONDS -> duration.toSeconds();
      case MINUTES -> duration.toMinutes();
      case HOURS -> duration.toHours();
      case DAYS -> duration.toDays();
      case WEEKS -> duration.toDays() / 7;
      default -> throw unsupportedUnit(unit, "a duration");
    };
    return BigInteger.valueOf(amount);
  }

  private static IllegalArgumentException unsupportedUnit(String unit, String valueKind) {
    return new IllegalArgumentException("Unit '" + unit + "' is not supported for " + valueKind);
  }

  private enum TimeDiffUnit {
    YEARS, MONTHS, WEEKS, DAYS, HOURS, MINUTES, SECONDS;

    static TimeDiffUnit from(String unit) {
      if (unit == null || unit.isBlank()) {
        throw new IllegalArgumentException("Unit is required");
      }
      return switch (unit.trim().toLowerCase(Locale.ROOT)) {
        case "year", "years" -> YEARS;
        case "month", "months" -> MONTHS;
        case "week", "weeks" -> WEEKS;
        case "day", "days" -> DAYS;
        case "hour", "hours", "hr" -> HOURS;
        case "minute", "minutes", "min" -> MINUTES;
        case "second", "seconds" -> SECONDS;
        default -> throw new IllegalArgumentException("Unknown unit: " + unit);
      };
    }
  }

}
