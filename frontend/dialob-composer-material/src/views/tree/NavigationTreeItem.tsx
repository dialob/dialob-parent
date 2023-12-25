import React from 'react';
import { ListItem, ListItemText, styled } from '@mui/material';
import { TreeItem, ItemId } from '@atlaskit/tree';
import { ArrowDropDown, ArrowRight } from '@mui/icons-material';
import { TreeDraggableProvided } from '@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types';
import { DialobItem, DialobItemType } from '../../dialob';
import { DEFAULT_ITEM_CONFIG, PAGE_CONFIG } from '../../defaults';


interface TreeItemProps {
  item: TreeItem;
  onExpand: (itemId: ItemId) => void;
  onCollapse: (itemId: ItemId) => void;
  provided: TreeDraggableProvided;
}

const PreTextIcon = styled('span')({
  width: '1.5em',
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
});

const getIcon = (
  item: TreeItem,
  onExpand: (itemId: ItemId) => void,
  onCollapse: (itemId: ItemId) => void,
) => {
  if (item.children && item.children.length > 0) {
    return item.isExpanded ? (
      <PreTextIcon onClick={() => onCollapse(item.id)}><ArrowDropDown fontSize='small' /></PreTextIcon>
    ) : (
      <PreTextIcon onClick={() => onExpand(item.id)}><ArrowRight fontSize='small' /></PreTextIcon>
    );
  }
  return <PreTextIcon />;
};

const getTypeIcon = (item: DialobItem, isPage: boolean) => {
  if (isPage) {
    return <PreTextIcon><PAGE_CONFIG.icon fontSize='small' /></PreTextIcon>;
  }
  let itemConfig = DEFAULT_ITEM_CONFIG.items.find(c => c.matcher(item));
  const Icon = itemConfig?.props.icon!;
  return <PreTextIcon><Icon fontSize='small' /></PreTextIcon>;
}

const NavigationTreeItem: React.FC<TreeItemProps> = ({ item, onExpand, onCollapse, provided }) => {
  return (
    <ListItem
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
    >
      {getIcon(item, onExpand, onCollapse)}
      {getTypeIcon(item.data.item, item.data.isPage)}
      <ListItemText sx={{ cursor: 'pointer' }}>{item.data ? item.data.title : ''}</ListItemText>
    </ListItem>
  );
};

export default NavigationTreeItem;
