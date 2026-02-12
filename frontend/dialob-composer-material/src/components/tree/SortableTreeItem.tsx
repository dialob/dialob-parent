import React from 'react';
import { AnimateLayoutChanges, useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { TreeItem, TreeItemProps } from './TreeItem';
import { iOS } from './utilities';


const animateLayoutChanges: AnimateLayoutChanges = ({ isSorting, wasDragging }) => {
  return isSorting || wasDragging ? false : true;
}

export const SortableTreeItem: React.FC<TreeItemProps> = ({ id, depth, isVariable, ...props }) => {
  const sortable = useSortable({ id, animateLayoutChanges });
  const { attributes, isDragging, isSorting, listeners, setDraggableNodeRef, setDroppableNodeRef, transform, transition } = sortable;
  
  const style: React.CSSProperties = {
    transform: CSS.Translate.toString(transform),
    transition,
  };

  // For non-draggable items, don't apply drag listeners
  const handleProps = isVariable ? undefined : {
    ...attributes,
    ...listeners,
  };

  return (
    <TreeItem
      id={id}
      ref={setDraggableNodeRef}
      wrapperRef={setDroppableNodeRef}
      style={style}
      depth={depth}
      ghost={isDragging}
      disableSelection={iOS}
      disableInteraction={isSorting}
      handleProps={handleProps}
      isVariable={isVariable}
      {...props}
    />
  );
}
