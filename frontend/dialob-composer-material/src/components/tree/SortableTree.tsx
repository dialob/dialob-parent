import React from 'react';
import { createPortal } from 'react-dom';
import {
  DndContext, closestCenter, PointerSensor, useSensor, useSensors, 
  DragStartEvent, DragOverlay, DragMoveEvent, DragEndEvent, DragOverEvent, MeasuringStrategy,
  DropAnimation, defaultDropAnimation, UniqueIdentifier,
} from '@dnd-kit/core';
import { SortableContext, arrayMove, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { buildTree, flattenTree, getProjection, getChildCount, removeChildrenOf, setProperty, buildTreeFromForm } from './utilities';
import { SensorContext, TreeItems } from './types';
import { SortableTreeItem } from './SortableTreeItem';
import { CSS } from '@dnd-kit/utilities';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { canContain } from '../../defaults';
import { useBackend } from '../../backend/useBackend';

const indentationWidth = 15;

const measuring = {
  droppable: {
    strategy: MeasuringStrategy.Always,
  },
};

const dropAnimationConfig: DropAnimation = {
  keyframes({transform}) {
    return [
      {opacity: 1, transform: CSS.Transform.toString(transform.initial)},
      {
        opacity: 0,
        transform: CSS.Transform.toString({
          ...transform.final,
          x: transform.final.x + 5,
          y: transform.final.y + 5,
        }),
      },
    ];
  },
  easing: 'ease-out',
  sideEffects({active}) {
    active.node.animate([{opacity: 0}, {opacity: 1}], {
      duration: defaultDropAnimation.duration,
      easing: defaultDropAnimation.easing,
    });
  },
};


export const SortableTree: React.FC = () => {
  const { form, syncTree } = useComposer();
  const { editor } = useEditor();
  const { config } = useBackend();
  const [items, setItems] = React.useState<TreeItems>([{ id: 'root', children: [], title: 'Root' }]);
  const [activeId, setActiveId] = React.useState<UniqueIdentifier | null>(null);
  const [overId, setOverId] = React.useState<UniqueIdentifier | null>(null);
  const [offsetLeft, setOffsetLeft] = React.useState(0);

  React.useEffect(() => {
    setItems(buildTreeFromForm(form.data, editor.activeFormLanguage, config.itemEditors));
  }, [form.data, editor.activeFormLanguage]);
  
  const flattenedItems = React.useMemo(() => {
    const flattenedTree = flattenTree(items);
    const collapsedItems = flattenedTree.reduce<UniqueIdentifier[]>(
      (acc, {children, collapsed, id}) =>
        collapsed && children.length ? [...acc, id] : acc,
      []
    );
    return removeChildrenOf(
      flattenedTree,
      activeId != null ? [activeId, ...collapsedItems] : collapsedItems
    );
  }, [activeId, items]);

  const sensorContext: SensorContext = React.useRef({ items: flattenedItems, offset: offsetLeft });
  const sensors = useSensors(useSensor(PointerSensor),);

  const projected = activeId && overId ? getProjection(flattenedItems, activeId, overId, offsetLeft, indentationWidth) : null;
  const sortedIds = React.useMemo(() => flattenedItems.map(({id}) => id), [flattenedItems]);
  const activeItem = activeId ? flattenedItems.find(({id}) => id === activeId) : null;

  React.useEffect(() => {
    sensorContext.current = {
      items: flattenedItems,
      offset: offsetLeft,
    };
  }, [flattenedItems, offsetLeft]);

  const handleDragStart = ({active: {id: activeId}}: DragStartEvent) => {
    setActiveId(activeId);
    setOverId(activeId);
    document.body.style.setProperty('cursor', 'grabbing');
  }

  const handleDragMove = ({delta}: DragMoveEvent) => {
    setOffsetLeft(delta.x);
  }

  const handleDragOver = ({over}: DragOverEvent) => {
    setOverId(over?.id ?? null);
  }

  const handleDragEnd = ({ active, over }: DragEndEvent) => {
    resetState();

    if (projected && over) {
      const { depth, parentId } = projected;

      if (!parentId) {
        return;
      }

      const parentItemType = form.data[parentId as string].type;
      const activeItemType = form.data[active.id as string].type;
      if (!canContain(parentItemType, activeItemType)) {
        return;
      }

      const previousFlattened = flattenTree(items);

      const activeIndex = previousFlattened.findIndex(item => item.id === active.id);
      const overIndex = previousFlattened.findIndex(item => item.id === over.id);

      const updatedFlattened = [...previousFlattened];
      updatedFlattened[activeIndex] = {
        ...updatedFlattened[activeIndex],
        depth,
        parentId,
      };

      const sortedFlattened = arrayMove(updatedFlattened, activeIndex, overIndex);

      const newTree = buildTree(sortedFlattened);
      setItems(newTree);

      syncTree(sortedFlattened);
    }
  };

  const handleDragCancel = () => {
    resetState();
  }

  const resetState = () => {
    setOverId(null);
    setActiveId(null);
    setOffsetLeft(0);
    document.body.style.setProperty('cursor', '');
  }

  const handleCollapse = (id: UniqueIdentifier) => {
    setItems((items) =>
      setProperty(items, id, 'collapsed', (value) => {
        return !value;
      })
    );
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      measuring={measuring}
      onDragStart={handleDragStart}
      onDragMove={handleDragMove}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
      onDragCancel={handleDragCancel}
    >
      <SortableContext items={sortedIds} strategy={verticalListSortingStrategy}>
        {flattenedItems.map(({id, children, collapsed, collapsible, title, depth}) => (
          <SortableTreeItem
            key={id}
            id={id as string}
            title={title}
            depth={id === activeId && projected ? projected.depth : depth}
            indentationWidth={indentationWidth}
            collapsible={collapsible}
            collapsed={Boolean(collapsed && children.length)}
            onCollapse={children.length ? () => handleCollapse(id) : undefined}
          />
        ))}
        {createPortal(
          <DragOverlay dropAnimation={dropAnimationConfig}>
            {activeId && activeItem ? (
              <SortableTreeItem
                id={activeId as string}
                depth={activeItem.depth}
                clone
                childCount={getChildCount(items, activeId) + 1}
                title={activeItem.title}
                indentationWidth={indentationWidth}
              />
            ) : null}
          </DragOverlay>,
          document.body
        )}
      </SortableContext>
    </DndContext>
  );
}
