package io.dialob.rule.parser.node;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.io.Serializable;

public record Span(
  int startIndex,
  int stopIndex
) implements Serializable {

  public static final Span UNDEFINED_SPAN = new Span(-1, -1);

  public static Span of(Token token) {
    return new Span(token.getStartIndex(), token.getStopIndex());
  }

  public static Span of(int startIndex, int stopIndex) {
    assert startIndex >= 0;
    assert startIndex <= stopIndex;
    return new Span(startIndex, stopIndex);
  }

  public static Span undefined() {
    return UNDEFINED_SPAN;
  }

  public static Span of(ParserRuleContext ctx) {
    return of(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
  }
}
