import React from 'react';
import { DraggableAttributes } from '@dnd-kit/core';
import { SyntheticListenerMap } from '@dnd-kit/core/dist/hooks/utilities';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

export type SortableHandleProps = DraggableAttributes & SyntheticListenerMap;

export const useSortableRow = (id: string) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id });

  const style: React.CSSProperties = {
    transform: CSS.Translate.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : undefined,
  };

  return {
    setNodeRef,
    style,
    handleProps: {
      ...attributes,
      ...listeners,
    } as SortableHandleProps,
    isDragging,
  };
};
