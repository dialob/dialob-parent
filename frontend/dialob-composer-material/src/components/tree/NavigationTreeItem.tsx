import React from 'react';
import { ListItem, ListItemText, Typography, IconButton, styled } from '@mui/material';
import { ArrowDropDown, ArrowRight } from '@mui/icons-material';
import { TreeItem, ItemId } from '@atlaskit/tree';
import { TreeDraggableProvided } from '@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types';
import { useComposer } from '../../dialob';
import { DEFAULT_ITEM_CONFIG, PAGE_CONFIG } from '../../defaults';
import { useEditor } from '../../editor';
import { getErrorIcon, useErrorColorSx } from '../../utils/ErrorUtils';
import { scrollToItem } from '../../utils/ScrollUtils';
import { useBackend } from '../../backend/useBackend';
import { ItemConfig } from '../../defaults/types';
import { PreTextIcon } from '../../utils/TreeUtils';
import { DialobItem } from '../../types';


interface TreeItemProps {
  item: TreeItem;
  onExpand: (itemId: ItemId) => void;
  onCollapse: (itemId: ItemId) => void;
  provided: TreeDraggableProvided;
}

const MAX_TREE_ITEM_TITLE_LENGTH = 40;

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
  if (item.data && item.data.treeCollapsible && item.children && item.children.length > 0) {
    return item.isExpanded ? (
      <ArrowIcon onClick={() => onCollapse(item.id)}><ArrowDropDown fontSize='small' /></ArrowIcon>
    ) : (
      <ArrowIcon onClick={() => onExpand(item.id)}><ArrowRight fontSize='small' /></ArrowIcon>
    );
  }
  return <ArrowIcon sx={{ mr: 0.5 }} />;
};

const getTypeIcon = (item: DialobItem, isPage: boolean, highlighted: boolean, itemConfig?: ItemConfig) => {
  if (isPage) {
    return <PreTextIcon disableRipple><PAGE_CONFIG.icon fontSize='small' /></PreTextIcon>;
  }
  const resolvedConfig = itemConfig ?? DEFAULT_ITEM_CONFIG;
  const matchedConfig = resolvedConfig.items.find(c => c.matcher(item));
  const Icon = matchedConfig?.props.icon || DEFAULT_ITEM_CONFIG.defaultIcon;
  return <PreTextIcon disableRipple sx={{ mr: 0.5 }}><Icon fontSize='small' color={highlighted ? 'primary' : 'inherit'} /></PreTextIcon>;
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
  const { editor, setActivePage, setHighlightedItem, setActiveItem, setItemOptionsActiveTab } = useEditor();
  const { form } = useComposer();
  const { config } = useBackend();
  const itemId = item.data.item.id;
  const errorColor = useErrorColorSx(editor.errors, itemId);
  const itemConfig = config.itemEditors;
  const [highlighted, setHighlighted] = React.useState<boolean>(false);
  const textColor = errorColor ?? highlighted ? 'primary.main' : 'text.primary';
  const fontWeight = highlighted ? 'bold' : 'inherit';

  React.useEffect(() => {
    if (editor?.highlightedItem?.id === item.id) {
      setHighlighted(true);
    } else {
      setHighlighted(false);
    }
  }, [editor.highlightedItem])

  const handleScrollTo = (e: React.MouseEvent) => {
    e.stopPropagation();
    setHighlightedItem(item.data.item);
    scrollToItem(itemId, Object.values(form.data), editor.activePage, setActivePage);
  }

  const handleOpenEditor = () => {
    setActiveItem(item.data.item);
    setItemOptionsActiveTab('label');
  }

  return (
    <ListItem
      id={`tree-item-${itemId}`}
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
    >
      {getIcon(item, onExpand, onCollapse)}
      {errorColor ? getErrorIcon(editor.errors, itemId) : getTypeIcon(item.data.item, item.data.isPage, highlighted, itemConfig)}
      <ListItemText sx={{ cursor: 'pointer', ':hover': { color: 'text.secondary' } }} onClick={handleScrollTo} onDoubleClick={handleOpenEditor}>
        <Typography sx={{ color: textColor, fontWeight, ':hover': { color: 'text.secondary' } }}>{getTitle(item)}</Typography>
      </ListItemText>
    </ListItem>
  );
};

export default NavigationTreeItem;
