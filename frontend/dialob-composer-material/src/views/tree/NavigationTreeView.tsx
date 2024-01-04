import React, { useState } from 'react';
import { Box, useTheme } from '@mui/material';
import Tree, {
  mutateTree,
  moveItemOnTree,
  TreeData,
  ItemId,
  TreeSourcePosition,
  TreeDestinationPosition,
  RenderItemParams,
} from '@atlaskit/tree';
import { DialobItem, useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { buildTreeFromForm } from './TreeBuilder';
import NavigationTreeItem from './NavigationTreeItem';


const INIT_TREE: TreeData = {
  rootId: 'root',
  items: {},
};

const isParentNode = (tree: TreeData, destination?: TreeDestinationPosition): boolean => {
  if (!destination) {
    return false;
  }
  if (destination.parentId.toString().includes('root')) {
    return true;
  }
  const destinationItem: DialobItem = tree.items[destination.parentId].data.item;
  if (destinationItem.type === 'group' || destinationItem.type === 'surveygroup' || destinationItem.type === 'rowgroup') {
    return true;
  }
  return false;
}

const isEmptyParentNode = (tree: TreeData, destination?: TreeDestinationPosition): boolean => {
  const isParent = isParentNode(tree, destination);
  if (!isParent) {
    return false;
  }
  const destinationItem = tree.items[destination!.parentId];
  return destinationItem.children.length === 0;
}

const renderItem = ({ item, onExpand, onCollapse, provided }: RenderItemParams) => {
  return (
    <NavigationTreeItem item={item} onExpand={onExpand} onCollapse={onCollapse} provided={provided} />
  );
}

const NavigationTreeView: React.FC = () => {
  const theme = useTheme();
  const { form, moveItem } = useComposer();
  const { editor } = useEditor();
  const [tree, setTree] = useState<TreeData>(INIT_TREE);

  React.useEffect(() => {
    setTree(buildTreeFromForm(form.data, editor.activeFormLanguage));
  }, [form, editor.activeFormLanguage]);

  const onExpand = (itemId: ItemId) => {
    setTree((prevTree) => mutateTree(prevTree, itemId, { isExpanded: true }));
  };

  const onCollapse = (itemId: ItemId) => {
    setTree((prevTree) => mutateTree(prevTree, itemId, { isExpanded: false }));
  };

  const onDragEnd = (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination) {
      return;
    }
    if (!isParentNode(tree, destination)) {
      return;
    }
    if (isEmptyParentNode(tree, destination)) {
      destination.index = 0;
    }
    const newTree = moveItemOnTree(tree, source, destination);
    const item = newTree.items[destination.parentId].children[destination.index!];
    moveItem(item.toString(), source.index, destination.index!, source.parentId.toString(), destination.parentId.toString());
  };

  return (
    <Box sx={{ mt: 2 }}>
      <Tree
        tree={tree}
        renderItem={renderItem}
        onExpand={onExpand}
        onCollapse={onCollapse}
        onDragEnd={onDragEnd}
        offsetPerLevel={Number.parseInt(theme.spacing(2))}
        isDragEnabled
        isNestingEnabled
      />
    </Box>
  );
};

export default NavigationTreeView;
