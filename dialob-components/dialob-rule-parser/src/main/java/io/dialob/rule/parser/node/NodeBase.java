package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class NodeBase implements TypedNode, Serializable {

  @Serial
  private static final long serialVersionUID = 1948018522089217375L;

  @Getter
  @Setter
  private NodeBase parent;

  @Getter
  private final Span span;

  private ValueType type;

  public NodeBase(NodeBase parent) {
    this(parent, null, null);
  }

  public NodeBase(NodeBase parent, Span span) {
    this(parent, span, null);
  }

  public NodeBase(NodeBase parent, Span span, ValueType type) {
    this.parent = parent;
    this.span = span;
    this.type = type;
  }

  @Override
  public ValueType getValueType() {
    return type;
  }

  public void setValueType(@NonNull ValueType type) {
    this.type = Objects.requireNonNull(type);
  }

  @NonNull
  public Map<String, ValueType> getDependencies() {
    return Collections.emptyMap();
  }

  public Map<String, ValueType> getAllDependencies() {
    return getDependencies();
  }

  public boolean isConstant() {
    return false;
  }

  public boolean isIdentifier() {
    return false;
  }

  public NodeBase addSubnode(@NonNull NodeBase topNode) {
    throw new IllegalStateException("Cannot add subnodes on leaf node");
  }

  @NonNull
  public List<NodeBase> getSubnodes() {
    return Collections.emptyList();
  }

  @NonNull
  public abstract NodeOperator getNodeOperator();

  public abstract NodeBase accept(@NonNull ASTVisitor visitor);

  public String toTypedString() {
    return this + "[" + type + "]";
  }

  public String toString(String indent) {
    return indent + this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof NodeBase other)) {
      return false;
    }
    return other.type == type;
  }

  @Override
  public int hashCode() {
    return type != null ? type.hashCode() : 0;
  }

}
