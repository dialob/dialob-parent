import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { MarkdownView } from './MarkdownView';
import { Box } from '@mui/material';
import { getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface NoteProps {
  note: ItemAction<'note'>['item'];
};

export const Note: React.FC<NoteProps> = ({ note }) => {

  return (
    <Box sx={getLayoutStyleFromProps(note.props)}>
      <MarkdownView text={note.label} />
    </Box>
    );
};
