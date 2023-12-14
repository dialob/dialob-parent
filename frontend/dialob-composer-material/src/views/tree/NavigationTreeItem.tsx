import React from 'react';
import { ListItem, ListItemText, styled } from '@mui/material';
import { TreeItem, ItemId } from '@atlaskit/tree';
import { ArrowDropDown, ArrowRight } from '@mui/icons-material';
import { DEFAULT_ICON_CONFIG } from '../../defaults/IconConfig';
import { TreeDraggableProvided } from '@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types';


interface TreeItemProps {
  item: TreeItem;
  onExpand: (itemId: ItemId) => void;
  onCollapse: (itemId: ItemId) => void;
  provided: TreeDraggableProvided;
}

const PreTextIcon = styled('span')({
  width: '24px',
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

const getTypeIcon = (type: string) => {
  const Icon = DEFAULT_ICON_CONFIG[type];
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
      {getTypeIcon(item.data.type)}
      <ListItemText sx={{ cursor: 'pointer' }} onClick={() => { console.log('navigate to item', item.id) }}>{item.data ? item.data.title : ''}</ListItemText>
    </ListItem>
  );
};

export default NavigationTreeItem;
