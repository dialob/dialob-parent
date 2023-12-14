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
import { useComposer } from '../../dialob';
import { buildTreeFromForm } from './TreeBuilder';
import NavigationTreeItem from './NavigationTreeItem';


const isParentNode = (tree: TreeData, destination?: TreeDestinationPosition): boolean => {
  if (!destination) {
    return false;
  }
  if (destination.parentId.toString().includes('root')) {
    return true;
  }
  const destinationItem = tree.items[destination.parentId];
  if (destinationItem.data.type === 'group' || destinationItem.data.type === 'surveygroup' || destinationItem.data.type === 'rowgroup') {
    return true;
  }
  return false;
}

const renderItem = ({ item, onExpand, onCollapse, provided }: RenderItemParams) => {
  return (
    <NavigationTreeItem item={item} onExpand={onExpand} onCollapse={onCollapse} provided={provided} />
  );
}

const NavigationTreeView: React.FC = () => {
  const theme = useTheme();
  const { form, moveItem } = useComposer();
  const treeData: TreeData = buildTreeFromForm(form.data);
  const [tree, setTree] = useState<TreeData>(treeData);

  React.useEffect(() => {
    setTree(buildTreeFromForm(form.data));

  }, [form]);

  console.log('treeData', treeData)
  console.log('formData', form.data)

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
    console.log('source', source)
    console.log('destination', destination)
    console.log('tree', tree)

    if (!destination) {
      return;
    }
    if (!isParentNode(tree, destination)) {
      return;
    }
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);

    console.log('newTree', newTree)

    const item = newTree.items[destination.parentId].children[destination.index!];
    moveItem(item.toString(), source.index, destination.index!, source.parentId.toString(), destination.parentId.toString());
    console.log('new state', form.data)
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
