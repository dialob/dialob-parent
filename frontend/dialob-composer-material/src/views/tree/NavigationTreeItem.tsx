import React from 'react';
import { ListItem, ListItemText, Typography, styled } from '@mui/material';
import { TreeItem, ItemId } from '@atlaskit/tree';
import { ArrowDropDown, ArrowRight, Warning } from '@mui/icons-material';
import { TreeDraggableProvided } from '@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types';
import { DialobItem } from '../../dialob';
import { DEFAULT_ITEM_CONFIG, PAGE_CONFIG } from '../../defaults';
import { ErrorSeverity, getErrorColor, getErrorIcon, useEditor } from '../../editor';


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
  const itemConfig = DEFAULT_ITEM_CONFIG.items.find(c => c.matcher(item));
  const Icon = itemConfig?.props.icon || DEFAULT_ITEM_CONFIG.defaultIcon;
  return <PreTextIcon><Icon fontSize='small' /></PreTextIcon>;
}

const NavigationTreeItem: React.FC<TreeItemProps> = ({ item, onExpand, onCollapse, provided }) => {
  const { editor } = useEditor();
  const errorColor = getErrorColor(editor.errors, item.data.item);
  return (
    <ListItem
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
    >
      {getIcon(item, onExpand, onCollapse)}
      {errorColor ? getErrorIcon(editor.errors, item.data.item) : getTypeIcon(item.data.item, item.data.isPage)}
      <ListItemText sx={{ cursor: 'pointer', ':hover': { color: 'text.secondary' } }}>
        <Typography sx={{ color: errorColor, ':hover': { color: 'text.secondary' } }}>{item.data ? item.data.title : ''}</Typography>
      </ListItemText>
    </ListItem>
  );
};

export default NavigationTreeItem;
