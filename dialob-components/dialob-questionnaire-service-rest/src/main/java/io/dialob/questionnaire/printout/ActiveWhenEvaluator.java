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
package io.dialob.questionnaire.printout;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal evaluator for Dialob {@code activeWhen} expressions, used to decide whether an item was
 * visible to the respondent. A completed Dialob session is frozen — the engine does not retain the
 * computed visibility — so the printout re-derives it from the static form expression and the stored
 * answers. Supports the subset used by forms: {@code and}/{@code or}, parentheses, comparisons,
 * {@code in}/{@code not in}, {@code is (not) answered} and bare truthiness.
 */
public class ActiveWhenEvaluator {

  public interface NameResolver {
    Object value(String name);
    boolean isAnswered(String name);
  }

  public boolean isActive(String activeWhen, NameResolver resolver) {
    if (activeWhen == null || activeWhen.isBlank()) {
      return true;
    }
    String normalized = activeWhen.replaceAll("\\s+", " ").trim();
    return evalOr(normalized, resolver);
  }

  private boolean evalOr(String expr, NameResolver r) {
    for (String part : splitTopLevel(expr, " or ")) {
      if (evalAnd(part.trim(), r)) {
        return true;
      }
    }
    return false;
  }

  private boolean evalAnd(String expr, NameResolver r) {
    for (String part : splitTopLevel(expr, " and ")) {
      if (!evalAtom(part.trim(), r)) {
        return false;
      }
    }
    return true;
  }

  private boolean evalAtom(String atom, NameResolver r) {
    if (atom.isEmpty()) {
      return true;
    }

    if (atom.startsWith("(") && atom.endsWith(")")) {
      return evalOr(atom.substring(1, atom.length() - 1).trim(), r);
    }
    if (atom.equalsIgnoreCase("true")) return true;
    if (atom.equalsIgnoreCase("false")) return false;

    if (atom.endsWith(" is not answered")) {
      return !r.isAnswered(atom.substring(0, atom.length() - " is not answered".length()).trim());
    }
    if (atom.endsWith(" is answered")) {
      return r.isAnswered(atom.substring(0, atom.length() - " is answered".length()).trim());
    }

    int notInIdx = indexOfOp(atom, " not in ");
    if (notInIdx >= 0) {
      String lhs = atom.substring(0, notInIdx).trim();
      List<String> list = parseList(atom.substring(notInIdx + " not in ".length()).trim());
      return !list.contains(asString(r.value(lhs)));
    }
    int inIdx = indexOfOp(atom, " in ");
    if (inIdx >= 0) {
      String lhs = atom.substring(0, inIdx).trim();
      List<String> list = parseList(atom.substring(inIdx + " in ".length()).trim());
      return list.contains(asString(r.value(lhs)));
    }

    for (String op : new String[] {"!=", "<=", ">=", "=", "<", ">"}) {
      int idx = atom.indexOf(op);
      if (idx > 0) {
        String lhs = atom.substring(0, idx).trim();
        String rhs = atom.substring(idx + op.length()).trim();
        return compare(r.value(lhs), op, rhs);
      }
    }

    Object v = r.value(atom);
    return truthy(v);
  }

  private boolean compare(Object lhsVal, String op, String rhsRaw) {
    String rhs = unquote(rhsRaw);
    Double ln = asDouble(lhsVal);
    Double rn = asDouble(rhs);
    if (ln != null && rn != null) {
      int c = Double.compare(ln, rn);
      return switch (op) {
        case "=" -> c == 0;
        case "!=" -> c != 0;
        case "<" -> c < 0;
        case ">" -> c > 0;
        case "<=" -> c <= 0;
        case ">=" -> c >= 0;
        default -> false;
      };
    }
    String ls = asString(lhsVal);
    return switch (op) {
      case "=" -> equalsLoose(ls, rhs);
      case "!=" -> !equalsLoose(ls, rhs);
      default -> false;
    };
  }

  private boolean equalsLoose(String a, String b) {
    if (a == null) return b == null || b.isEmpty();
    return a.equalsIgnoreCase(b);
  }

  private boolean truthy(Object v) {
    if (v == null) return false;
    if (v instanceof Boolean b) return b;
    String s = String.valueOf(v);
    return "true".equalsIgnoreCase(s);
  }

  private List<String> splitTopLevel(String expr, String sep) {
    List<String> out = new ArrayList<>();
    int depth = 0;
    boolean inStr = false;
    int start = 0;
    for (int i = 0; i + sep.length() <= expr.length(); i++) {
      char c = expr.charAt(i);
      if (c == '\'') inStr = !inStr;
      else if (!inStr && c == '(') depth++;
      else if (!inStr && c == ')') depth--;
      if (depth == 0 && !inStr && expr.regionMatches(true, i, sep, 0, sep.length())) {
        out.add(expr.substring(start, i));
        i += sep.length() - 1;
        start = i + 1;
      }
    }
    out.add(expr.substring(start));
    return out;
  }

  private int indexOfOp(String expr, String op) {
    int depth = 0;
    boolean inStr = false;
    for (int i = 0; i + op.length() <= expr.length(); i++) {
      char c = expr.charAt(i);
      if (c == '\'') inStr = !inStr;
      else if (!inStr && c == '(') depth++;
      else if (!inStr && c == ')') depth--;
      if (depth == 0 && !inStr && expr.regionMatches(true, i, op, 0, op.length())) {
        return i;
      }
    }
    return -1;
  }

  private List<String> parseList(String raw) {
    String s = raw.trim();
    if (s.startsWith("(")) s = s.substring(1);
    if (s.endsWith(")")) s = s.substring(0, s.length() - 1);
    List<String> out = new ArrayList<>();
    for (String tok : s.split(",")) {
      String t = unquote(tok.trim());
      if (!t.isEmpty()) out.add(t);
    }
    return out;
  }

  private String unquote(String s) {
    String t = s.trim();
    if (t.length() >= 2 && ((t.startsWith("'") && t.endsWith("'"))
        || (t.startsWith("\"") && t.endsWith("\"")))) {
      return t.substring(1, t.length() - 1);
    }
    return t;
  }

  private String asString(Object v) {
    return v == null ? null : String.valueOf(v);
  }

  private Double asDouble(Object v) {
    if (v == null) return null;
    try {
      return Double.parseDouble(String.valueOf(v).trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
