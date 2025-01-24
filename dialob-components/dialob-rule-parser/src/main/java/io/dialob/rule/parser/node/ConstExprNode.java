package io.dialob.rule.parser.node;


import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.ValueType;
import lombok.EqualsAndHashCode;
import org.apache.commons.text.StringEscapeUtils;

import java.io.Serial;
import java.util.Objects;


@EqualsAndHashCode(callSuper = true, of = {"value", "unit"})
public class ConstExprNode extends NodeBase {

  @Serial
  private static final long serialVersionUID = 7754763577641440194L;

  private final String value;

  private final String unit;

  public ConstExprNode(NodeBase parent, String value, String unit, @Nullable ValueType valueType, Span span) {
    super(parent, span);
    this.value = value;
    this.unit = unit;
    if (valueType != null) {
      setValueType(valueType);
    }
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @NonNull
  @Override
  public NodeOperator getNodeOperator() {
    return NodeOperator.CONST;
  }

  public boolean isNull() {
    return value == null;
  }

  public String getValue() {
    return value;
  }

  public String getUnit() {
    return unit;
  }

  public Object getAsValueType() {
    return getAsValueType(this.getValueType());
  }

  public Object getAsValueType(ValueType valueType) {
    Objects.requireNonNull(valueType, "valueType may not be null");
    if (value == null) {
      return null;
    }
    if (unit != null) {
      return valueType.parseFromStringWithUnit(value, unit);
    }

    return valueType.parseFromString(value);
  }

  @Override
  public String toString() {
    if (unit != null) {
      return "\"" + value + " " + unit + "\"";
    }
    if (getValueType() == ValueType.STRING) {
      return "\"" + StringEscapeUtils.escapeJava(value) + "\"";
    }
    return value;
  }

  @Override
  public String toTypedString() {
    String string = "'" + value;
    if (unit != null) {
      string = string + " " + unit;
    }
    return string + "'[" + getValueType() + "]";
  }


  public NodeBase accept(@NonNull ASTVisitor visitor) {
    return visitor.visitConstExpr(this);
  }
}
