import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { MarkdownView } from './MarkdownView';
import { calculateMargin, getIndent } from '../util/helperFunctions';
import { Box } from '@mui/material';

export interface NoteProps {
  note: ItemAction<'note'>['item'];
};

export const Note: React.FC<NoteProps> = ({ note }) => {
  const indent = getIndent(parseInt(note.props?.indent || 0));
  const spacesTop = parseInt(note.props?.spacesTop || 0);
  const spacesBottom = parseInt(note.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);

  return (
    <Box sx={{pl: indent, mt: marginTop, mb: marginBottom}}>
      <MarkdownView text={note.label} />
    </Box>
    );
};
