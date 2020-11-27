import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { MarkdownView } from './MarkdownView';

export interface NoteProps {
  note: ItemAction<'note'>['item'];
};
export const Note: React.FC<NoteProps> = ({ note }) => {
  return (
      <MarkdownView text={note.label} />
    );
};
