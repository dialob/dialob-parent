import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { MarkdownView } from './MarkdownView';
import { Box } from '@mui/material';

export interface NoteProps {
  note: ItemAction<'note'>['item'];
};

export const Note: React.FC<NoteProps> = ({ note }) => {
  const indent = parseInt(note.props?.indent || 0);
  const spacesTop = parseInt(note.props?.spacesTop || 0);
  const spacesBottom = parseInt(note.props?.spacesBottom || 0);

  return (
    <Box sx={{paddingLeft: (theme) => theme.spacing(indent), marginTop: (theme) => theme.spacing(spacesTop), marginBottom: (theme) => theme.spacing(spacesBottom)}}>
      <MarkdownView text={note.label} />
    </Box>
    );
};
