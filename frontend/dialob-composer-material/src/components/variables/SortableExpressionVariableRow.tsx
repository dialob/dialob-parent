import { Variable } from "../../types";
import { useSortableRow } from "../useSortableRow";
import ExpressionVariableRow from "./ExpressionVariableRow";
import { VariableProps } from "./VariableComponents";

export const SortableExpressionVariableRow: React.FC<VariableProps> = ({ sortableId, item, ...rowProps }) => {
  const { setNodeRef, style, handleProps } = useSortableRow(sortableId);
  return (
    <ExpressionVariableRow
      {...rowProps}
      item={item as Variable}
      sortableId={sortableId}
      setNodeRef={setNodeRef}
      style={style}
      handleProps={handleProps}
    />
  );
};