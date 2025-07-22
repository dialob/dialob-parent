import React from 'react';
import { IconButton, Typography, Badge } from '@mui/material';
import { Handle } from './Handle';
import { KeyboardArrowDown } from '@mui/icons-material';
import { StyledListItem, StyledTreeItem } from './TreeItemComponents';
import { useEditor } from '../../editor';
import { scrollToItem } from '../../utils/ScrollUtils';
import { useComposer } from '../../dialob';
import { useErrorColorSx } from '../../utils/ErrorUtils';
import { useBackend } from '../../backend/useBackend';


export interface TreeItemProps {
  childCount?: number;
  clone?: boolean;
  collapsed?: boolean;
  depth: number;
  disableInteraction?: boolean;
  disableSelection?: boolean;
  ghost?: boolean;
  handleProps?: any;
  indentationWidth: number;
  id: string;
  title: string;
  style?: React.CSSProperties;
  collapsible?: boolean;
  onCollapse?(): void;
  wrapperRef?(node: HTMLLIElement): void;
}

export const TreeItem = React.forwardRef<HTMLDivElement, TreeItemProps>((props, ref) => {
  const {
    childCount, clone, depth, disableSelection, disableInteraction, ghost, 
    handleProps, indentationWidth, collapsed, onCollapse, style, id, title, collapsible, wrapperRef 
  } = props;
  const { form } = useComposer();
  const { editor, setHighlightedItem, setActivePage, setActiveItem, setItemOptionsActiveTab } = useEditor();
  const { config } = useBackend();
  const [highlighted, setHighlighted] = React.useState(false);
  const errorColor = useErrorColorSx(editor.errors, id);
  const textColor = errorColor ?? (highlighted ? 'primary.main' : 'text.primary');
  const item = form.data[id];

  React.useEffect(() => {
    if (editor?.highlightedItem?.id === id) {
      setHighlighted(true);
    } else {
      setHighlighted(false);
    }
  }, [editor.highlightedItem])

  const handleScrollTo = (e: React.MouseEvent) => {
    e.stopPropagation();
    setHighlightedItem(item);
    scrollToItem(id, Object.values(form.data), editor.activePage, setActivePage);
  }

  const handleOpenEditor = () => {
    setActiveItem(item);
    setItemOptionsActiveTab('label');
  }

  return (
    <StyledListItem
      id={`tree-item-${id}`}
      ref={wrapperRef}
      clone={clone}
      ghost={ghost}
      disableInteraction={disableInteraction}
      sx={{
        pl: `${indentationWidth * depth}px`
      }}
    >
      <StyledTreeItem
        ref={ref}
        clone={clone}
        ghost={ghost}
        sx={style}
      >
        <Handle item={item} highlighted={highlighted} itemconfig={config.itemEditors} {...handleProps} />
        {onCollapse && collapsible && (
          <IconButton
            size="small"
            onClick={onCollapse}
            sx={{
              transform: collapsed ? 'rotate(-90deg)' : 'none',
              transition: 'transform 250ms ease',
            }}
          >
            <KeyboardArrowDown />
          </IconButton>
        )}
        <Typography
          variant="body2"
          noWrap={clone ? false : true}
          fontWeight={highlighted ? 'bold' : 'normal'}
          color={textColor}
          onClick={handleScrollTo}
          onDoubleClick={handleOpenEditor}
          sx={{
            width: 1,
            flexGrow: 1,
            pl: 1,
            userSelect: disableSelection ? 'none' : 'auto',
            cursor: 'pointer',
          }}
        >
          {title}
        </Typography>
        {clone && childCount && childCount > 1 ? (
          <Badge
            badgeContent={childCount}
            color="primary"
            sx={{
              '& .MuiBadge-badge': {
                top: -10,
                right: -10,
              },
            }}
          />
        ) : null}
      </StyledTreeItem>
    </StyledListItem>
  )
});
