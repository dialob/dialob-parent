import { ContextVariable } from "../../types";
import { useSortableRow } from "../useSortableRow";
import ContextVariableRow from "./ContextVariableRow";
import { VariableProps } from "./VariableComponents";

export const SortableContextVariableRow: React.FC<VariableProps> = ({ sortableId, item, ...rowProps }) => {
  const { setNodeRef, style, handleProps } = useSortableRow(sortableId);
  return (
    <ContextVariableRow
      {...rowProps}
      item={item as ContextVariable}
      sortableId={sortableId}
      setNodeRef={setNodeRef}
      style={style}
      handleProps={handleProps}
    />
  );
};