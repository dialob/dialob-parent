import React from 'react';
import { IconButton, IconButtonProps, styled } from '@mui/material';
import { DragIndicator } from '@mui/icons-material';

const StyledDragHandle = styled(IconButton)({
  padding: 4,
  cursor: 'grab',
  '&:active': {
    cursor: 'grabbing',
  },
});

export type DragHandleProps = IconButtonProps;

export const DragHandle = React.forwardRef<HTMLButtonElement, DragHandleProps>(
  (props, ref) => (
    <StyledDragHandle ref={ref} size="small" {...props}>
      <DragIndicator fontSize="small" />
    </StyledDragHandle>
  )
);

DragHandle.displayName = 'DragHandle';
