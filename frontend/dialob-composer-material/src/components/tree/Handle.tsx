import React from 'react';
import { ButtonBase, ButtonBaseProps, styled } from '@mui/material';
import { DragIndicator } from '@mui/icons-material';


const StyledAction = styled(ButtonBase)(({ theme }) => ({
  display: 'flex',
  width: theme.spacing(2),
  padding: theme.spacing(1),
  alignItems: 'center',
  justifyContent: 'center',
  borderRadius: theme.shape.borderRadius,
  cursor: 'grab',

  '& svg': {
    fill: theme.palette.text.secondary,
    transition: 'fill 0.2s ease',
  },

  '&:hover': {
    backgroundColor: theme.palette.action.hover,
    '& svg': {
      fill: theme.palette.primary.main,
    },
  },

}));

export const Handle = React.forwardRef<HTMLButtonElement, ButtonBaseProps>(
  (props, ref) => {
    return (
      <StyledAction
        ref={ref}
        {...props}
      >
        <DragIndicator />
      </StyledAction>
    );
  }
);
