import ChoiceItem, { ChoiceItemProps } from "./ChoiceItem";
import { useSortableRow } from "./useSortableRow";

export const SortableChoiceItem: React.FC<ChoiceItemProps> = (props) => {
  const { sortableId, ...itemProps } = props;
  const { setNodeRef, style, handleProps } = useSortableRow(sortableId);

  return (
    <ChoiceItem
      {...itemProps}
      sortableId={sortableId}
      setNodeRef={setNodeRef}
      style={style}
      handleProps={handleProps}
    />
  );
};