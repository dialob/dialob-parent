import React from 'react';
import { ListItem, ListItemText, Typography, IconButton, styled } from '@mui/material';
import { ArrowDropDown, ArrowRight } from '@mui/icons-material';
import { TreeItem, ItemId } from '@atlaskit/tree';
import { TreeDraggableProvided } from '@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types';
import { DialobItem, useComposer } from '../../dialob';
import { DEFAULT_ITEM_CONFIG, PAGE_CONFIG } from '../../defaults';
import { useEditor } from '../../editor';
import { getErrorIcon, useErrorColor } from '../../utils/ErrorUtils';
import { scrollToItem } from '../../utils/ScrollUtils';


interface TreeItemProps {
  item: TreeItem;
  onExpand: (itemId: ItemId) => void;
  onCollapse: (itemId: ItemId) => void;
  provided: TreeDraggableProvided;
}

const MAX_TREE_ITEM_TITLE_LENGTH = 40;

const PreTextIcon = styled(IconButton)(({ theme }) => ({
  padding: theme.spacing(0.5),
  color: 'inherit',
}));

const ArrowIcon = styled(IconButton)(({ theme }) => ({
  padding: 0,
  marginLeft: theme.spacing(0.5),
  color: 'inherit',
}));

const getIcon = (
  item: TreeItem,
  onExpand: (itemId: ItemId) => void,
  onCollapse: (itemId: ItemId) => void,
) => {
  if (item.children && item.children.length > 0) {
    return item.isExpanded ? (
      <ArrowIcon onClick={() => onCollapse(item.id)}><ArrowDropDown fontSize='small' /></ArrowIcon>
    ) : (
      <ArrowIcon onClick={() => onExpand(item.id)}><ArrowRight fontSize='small' /></ArrowIcon>
    );
  }
  return <ArrowIcon sx={{ mr: 0.5 }} />;
};

const getTypeIcon = (item: DialobItem, isPage: boolean) => {
  if (isPage) {
    return <PreTextIcon disableRipple><PAGE_CONFIG.icon fontSize='small' /></PreTextIcon>;
  }
  const itemConfig = DEFAULT_ITEM_CONFIG.items.find(c => c.matcher(item));
  const Icon = itemConfig?.props.icon || DEFAULT_ITEM_CONFIG.defaultIcon;
  return <PreTextIcon disableRipple sx={{ mr: 0.5 }}><Icon fontSize='small' /></PreTextIcon>;
}

const getTitle = (item: TreeItem) => {
  if (!item.data) {
    return '';
  }
  const rawTitle = item.data.title;
  return rawTitle.length > MAX_TREE_ITEM_TITLE_LENGTH
    ? rawTitle.substring(0, MAX_TREE_ITEM_TITLE_LENGTH) + '…'
    : rawTitle;
}

const NavigationTreeItem: React.FC<TreeItemProps> = ({ item, onExpand, onCollapse, provided }) => {
  const { editor, setActivePage } = useEditor();
  const { form } = useComposer();
  const errorColor = useErrorColor(editor.errors, item.data.item);
  const itemId = item.data.item.id;

  const handleScrollTo = (e: React.MouseEvent) => {
    e.stopPropagation();
    scrollToItem(itemId, Object.values(form.data), editor.activePage, setActivePage);
  }

  return (
    <ListItem
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
    >
      {getIcon(item, onExpand, onCollapse)}
      {errorColor ? getErrorIcon(editor.errors, item.data.item) : getTypeIcon(item.data.item, item.data.isPage)}
      <ListItemText sx={{ cursor: 'pointer', ':hover': { color: 'text.secondary' } }} onClick={handleScrollTo}>
        <Typography sx={{ color: errorColor, ':hover': { color: 'text.secondary' } }}>{getTitle(item)}</Typography>
      </ListItemText>
    </ListItem>
  );
};

export default NavigationTreeItem;
