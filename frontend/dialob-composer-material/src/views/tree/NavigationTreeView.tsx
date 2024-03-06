import React, { useState } from 'react';
import { Box, Button, Paper, useTheme } from '@mui/material';
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
import { DEFAULT_ITEM_CONFIG, canContain } from '../../defaults';
import { KeyboardArrowDown, KeyboardArrowRight } from '@mui/icons-material';


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
  if (DEFAULT_ITEM_CONFIG.items.find(c => c.matcher(destinationItem))?.props.treeCollapsible) {
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

  const expandAll = () => {
    setTree((prevTree) => {
      const newTree = { ...prevTree };
      Object.keys(newTree.items).forEach(key => {
        newTree.items[key].isExpanded = true;
      });
      return newTree;
    });
  };

  const collapseAll = () => {
    setTree((prevTree) => {
      const newTree = { ...prevTree };
      Object.keys(newTree.items).forEach(key => {
        newTree.items[key].isExpanded = false;
      });
      return newTree;
    });
  };

  const onDragEnd = (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination) {
      return;
    }
    const sourceItemId = tree.items[source.parentId].children[source.index!];
    const sourceItem = tree.items[sourceItemId].data.item.type;
    const destinationItem = tree.items[destination.parentId].data.item.type;
    if (!canContain(destinationItem, sourceItem)) {
      return;
    }
    if (isEmptyParentNode(tree, destination)) {
      destination.index = 0;
    }
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);
    const item = newTree.items[destination.parentId].children[destination.index!];
    moveItem(item.toString(), source.index, destination.index!, source.parentId.toString(), destination.parentId.toString());
  };

  return (
    <Box>
      <Paper elevation={3} sx={{ my: 1, p: 1, display: 'flex', justifyContent: 'space-evenly' }}>
        <Button variant='text' onClick={expandAll} endIcon={<KeyboardArrowDown />}>Expand All</Button>
        <Button variant='text' onClick={collapseAll} endIcon={<KeyboardArrowRight />}>Collapse All</Button>
      </Paper>
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
