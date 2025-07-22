import { Box, ListItem, styled } from '@mui/material';

interface StyledProps {
  clone?: boolean;
  ghost?: boolean;
  disableInteraction?: boolean;
}

export const StyledListItem = styled(ListItem, {
  shouldForwardProp: (prop) => !['clone', 'ghost', 'disableInteraction'].includes(prop.toString()),
})<StyledProps>(({ theme, clone, ghost, disableInteraction }) => ({
  marginBottom: theme.spacing(-0.125),
  pointerEvents: disableInteraction ? 'none' : 'auto',
  opacity: ghost ? 0.5 : 1,
}));

export const StyledTreeItem = styled(Box, {
  shouldForwardProp: (prop) => !['clone', 'ghost'].includes(prop.toString()),
})<StyledProps>(({ theme, ghost, clone }) => ({
  width: '100%',
  display: 'flex',
  alignItems: 'center',
  position: 'relative',
  padding: theme.spacing(1),
  border: `1px solid ${theme.palette.divider}`,
  backgroundColor: theme.palette.background.paper,
  ...(ghost && {
    zIndex: 1,
  }),
  ...(clone && {
    boxShadow: theme.shadows[3],
    borderRadius: theme.shape.borderRadius
  }),
}));