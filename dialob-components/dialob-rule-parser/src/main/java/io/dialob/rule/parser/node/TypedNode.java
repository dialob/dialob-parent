package io.dialob.rule.parser.node;

import io.dialob.rule.parser.api.ValueType;
import edu.umd.cs.findbugs.annotations.Nullable;


public interface TypedNode {
  @Nullable
  ValueType getValueType();
}
