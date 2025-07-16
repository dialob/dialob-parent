import React from 'react';
import { ButtonBase, ButtonBaseProps, styled } from '@mui/material';
import { DialobItem } from '../../types';
import { ItemConfig } from '../../defaults/types';
import { DEFAULT_ITEM_CONFIG, PAGE_CONFIG } from '../../defaults';
import { DragIndicator } from '@mui/icons-material';


interface HandleProps extends ButtonBaseProps {
  item: DialobItem | undefined;
  highlighted: boolean;
  itemConfig?: ItemConfig;
}

const StyledAction = styled(ButtonBase, {
  shouldForwardProp: (prop) => prop !== 'highlighted',
})<HandleProps>(({ theme, highlighted }) => ({
  display: 'flex',
  width: theme.spacing(2),
  padding: theme.spacing(1),
  alignItems: 'center',
  justifyContent: 'center',
  borderRadius: theme.shape.borderRadius,
  cursor: 'grab',

  '& svg': {
    transition: 'fill 0.2s ease',
    fill: highlighted ? theme.palette.primary.main : theme.palette.text.secondary,
  },

  '&:hover': {
    backgroundColor: theme.palette.action.hover,
    '& svg': {
      fill: theme.palette.primary.main,
    },
  },

}));


const getTypeIcon = (item: DialobItem | undefined, highlighted: boolean, itemConfig?: ItemConfig) => {
  if (!item) {
    return <DragIndicator />;
  }
  if (item.id.startsWith('page')) {
    return <PAGE_CONFIG.icon fontSize='small' />;
  }
  const resolvedConfig = itemConfig ?? DEFAULT_ITEM_CONFIG;
  const matchedConfig = resolvedConfig.items.find(c => c.matcher(item));
  const Icon = matchedConfig?.props.icon || DEFAULT_ITEM_CONFIG.defaultIcon;
  return <Icon fontSize='small' />;
}

export const Handle = React.forwardRef<HTMLButtonElement, HandleProps>(
  (props, ref) => {
    const { item, highlighted, itemConfig } = props;
    return (
      <StyledAction
        ref={ref}
        {...props}
      >
        {getTypeIcon(item, highlighted, itemConfig)}
      </StyledAction>
    );
  }
);
