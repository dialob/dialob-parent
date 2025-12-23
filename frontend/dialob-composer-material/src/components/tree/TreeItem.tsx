import React from 'react';
import { IconButton, Typography, Badge, alpha, Theme, Tooltip } from '@mui/material';
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
  isFallbackLabel?: boolean;
  isIdAsLabel?: boolean;
}

export const TreeItem = React.forwardRef<HTMLDivElement, TreeItemProps>((props, ref) => {
  const {
    childCount, clone, depth, disableSelection, disableInteraction, ghost,
    handleProps, indentationWidth, collapsed, onCollapse, style, id, title, collapsible, wrapperRef,
    isFallbackLabel, isIdAsLabel
  } = props;
  const { form } = useComposer();
  const { editor, setHighlightedItem, setActivePage, setActiveItem, setItemOptionsActiveTab } = useEditor();
  const { config } = useBackend();
  const [highlighted, setHighlighted] = React.useState(false);
  const errorColor = useErrorColorSx(editor.errors, id);
  const textColor = errorColor ?? (highlighted ? 'primary.main' : 'text.primary');
  const backgroundColor = (theme: Theme) => errorColor ? alpha(theme.palette.error.main, 0.1) : (highlighted ? alpha(theme.palette.mainContent.contrastText, 0.1) : theme.palette.background.paper);
  const item = form.data[id];

  const maxTextLength = 50 - (depth * 5);
  const truncatedTitle = title.length > maxTextLength
    ? title.substring(0, maxTextLength) + '...'
    : title;

  const showTooltip = !isIdAsLabel;
  const tooltipTitle = showTooltip ? `ID: ${id}` : '';

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
        sx={{ ...style, backgroundColor }}
      >
        <Handle item={item} highlighted={highlighted} error={errorColor !== undefined} itemconfig={config.itemEditors} {...handleProps} />
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
        <Tooltip title={tooltipTitle} placement="right" arrow>
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
              fontStyle: isFallbackLabel ? 'italic' : 'normal',
            }}
          >
            {truncatedTitle}
          </Typography>
        </Tooltip>
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
