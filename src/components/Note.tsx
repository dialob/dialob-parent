import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { MarkdownView } from './MarkdownView';
import { Box } from '@mui/material';
import { buildSxFromProps } from './helpers';

export interface NoteProps {
  note: ItemAction<'note'>['item'];
};

export const Note: React.FC<NoteProps> = ({ note }) => {

  return (
    <Box sx={buildSxFromProps(note.props)}>
      <MarkdownView text={note.label} />
    </Box>
    );
};
