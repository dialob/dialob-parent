package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.ValueType;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true, of = {"nodeOperator", "arguments"})
public class CallExprNode extends NodeBase {

  @Serial
  private static final long serialVersionUID = 9169421768436062924L;

  private List<NodeBase> arguments = new ArrayList<>();

  @NonNull
  private final NodeOperator nodeOperator;

  public CallExprNode(NodeBase parent, @NonNull NodeOperator nodeOperator, Span span) {
    this(parent, nodeOperator, null, span);
  }

  public CallExprNode(NodeBase parent, @NonNull NodeOperator nodeOperator, ValueType type, Span span) {
    super(parent, span, type);
    this.nodeOperator = Objects.requireNonNull(nodeOperator);
  }

  @Override
  public CallExprNode addSubnode(@NonNull NodeBase node) {
    node.setParent(this);
    arguments.add(node);
    return this;
  }

  @NonNull
  @Override
  public NodeOperator getNodeOperator() {
    return nodeOperator;
  }

  @Override
  public String toString() {
    return "(" + nodeOperator + (arguments.isEmpty() ? "" : " ") + StringUtils.join(arguments, " ") + ")";
  }

  @Nullable
  public <T extends NodeBase> T getLhs() {
    return (T) arguments.stream().findFirst().orElse(null);
  }

  @Nullable
  public <T extends NodeBase> T getRhs() {
    return (T) arguments.stream().skip(1).findFirst().orElse(null);
  }

  @Override
  public String toTypedString() {
    return "(" + nodeOperator + "[" + getValueType() + "]" + (arguments.isEmpty() ? "" : " ") + arguments.stream().map(NodeBase::toTypedString).collect(Collectors.joining(" ")) + ")";
  }

  @Override
  public String toString(String indent) {
    if (indent == null) {
      return toString();
    }
    if (arguments.isEmpty()) {
      return indent + "(" + nodeOperator + ")\n";
    }
    if (arguments.stream().noneMatch(node -> node instanceof CallExprNode)) {
      return indent + "(" + nodeOperator + " " + arguments.stream().map(Object::toString).collect(Collectors.joining(" ")) + ")";
    }
    return indent + "(" + nodeOperator + "\n" + arguments.stream().map(
      node -> node.toString(indent + "  ")).collect(Collectors.joining("\n")) + ")";
  }

  @Override
  @NonNull
  public List<NodeBase> getSubnodes() {
    return arguments;
  }

  public NodeBase accept(@NonNull ASTVisitor visitor) {
    ASTVisitor subvisitor = visitor.visitCallExpr(this);
    if (subvisitor != null) {
      int i = 0;
      int j = 0;
      NodeBase[] nodes = arguments.toArray(new NodeBase[0]);
      while (i < nodes.length) {
        NodeBase replaceNode = nodes[i++].accept(subvisitor);
        if (replaceNode != null) {
          replaceNode.setParent(this);
        }
        nodes[j++] = replaceNode;
      }
      if (j < nodes.length) {
        nodes = Arrays.copyOf(nodes, j);
      }
      arguments = Arrays.asList(nodes);
    }
    return visitor.endCallExpr(this);
  }

}
