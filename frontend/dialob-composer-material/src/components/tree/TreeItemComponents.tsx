import { Box, ListItem, styled } from '@mui/material';

interface StyledProps {
  clone?: boolean;
  ghost?: boolean;
  indicator?: boolean;
  disableInteraction?: boolean;
  disableSelection?: boolean;
}

export const StyledListItem = styled(ListItem, {
  shouldForwardProp: (prop) =>
    !['clone', 'ghost', 'indicator', 'disableInteraction', 'disableSelection', 'highlighted'].includes(prop.toString()),
})<StyledProps>(({ theme, clone, ghost, indicator, disableInteraction }) => ({
  listStyle: 'none',
  boxSizing: 'border-box',
  marginBottom: '-1px',
  pointerEvents: disableInteraction ? 'none' : 'auto',
  opacity: ghost && !indicator ? 0.5 : 1,
  paddingRight: 0,
  ...(clone && {
    display: 'inline-block',
    pointerEvents: 'none',
    padding: 0,
  }),
}));

export const StyledTreeItem = styled(Box, {
  shouldForwardProp: (prop) => !['clone', 'ghost', 'indicator'].includes(prop.toString()),
})<StyledProps>(({ theme, ghost, indicator, clone }) => ({
  width: '100%',
  display: 'flex',
  alignItems: 'center',
  position: 'relative',
  padding: ghost && indicator ? 0 : '10px',
  height: ghost && indicator ? '8px' : 'auto',
  border: ghost && indicator ? '1px solid #2389ff' : '1px solid #dedede',
  backgroundColor: ghost && indicator ? '#56a1f8' : '#fff',
  boxSizing: 'border-box',
  ...(ghost && indicator && {
    position: 'relative',
    zIndex: 1,
    '&:before': {
      content: "''",
      position: 'absolute',
      left: -8,
      top: -4,
      display: 'block',
      width: 12,
      height: 12,
      borderRadius: '50%',
      border: '1px solid #2389ff',
      backgroundColor: '#fff',
    },
  }),
  ...(clone && {
    boxShadow: '0px 15px 15px 0 rgba(34, 33, 81, 0.1)',
    borderRadius: 4,
    paddingRight: 24,
  }),
}));