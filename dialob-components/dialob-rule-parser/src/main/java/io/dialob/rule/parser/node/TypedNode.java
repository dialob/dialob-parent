package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.ValueType;


public interface TypedNode {
  @Nullable
  ValueType getValueType();
}
