import { Box, ListItem, styled } from '@mui/material';

interface StyledProps {
  clone?: boolean;
  ghost?: boolean;
  indicator?: boolean;
  disableInteraction?: boolean;
}

export const StyledListItem = styled(ListItem, {
  shouldForwardProp: (prop) =>
    !['clone', 'ghost', 'indicator', 'disableInteraction', 'disableSelection', 'highlighted'].includes(prop.toString()),
})<StyledProps>(({ theme, clone, ghost, indicator, disableInteraction }) => ({
  listStyle: 'none',
  boxSizing: 'border-box',
  marginBottom: theme.spacing(-0.125),
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
  padding: ghost && indicator ? 0 : theme.spacing(1.25),
  height: ghost && indicator ? theme.spacing(1) : 'auto',
  border: ghost && indicator ? `1px solid ${theme.palette.primary.main}` : `1px solid ${theme.palette.divider}`,
  backgroundColor: ghost && indicator ? theme.palette.primary.light : theme.palette.background.paper,
  boxSizing: 'border-box',
  ...(ghost && indicator && {
    position: 'relative',
    zIndex: 1,
    '&:before': {
      content: "''",
      position: 'absolute',
      left: theme.spacing(-1),
      top: theme.spacing(-0.5),
      display: 'block',
      width: theme.spacing(1.5),
      height: theme.spacing(1.5),
      borderRadius: '50%',
      border: `1px solid ${theme.palette.primary.main}`,
      backgroundColor: theme.palette.background.paper,
    },
  }),
  ...(clone && {
    boxShadow: theme.shadows[3],
    borderRadius: theme.shape.borderRadius,
    paddingRight: theme.spacing(3),
  }),
}));