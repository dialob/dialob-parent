import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { MarkdownView } from './MarkdownView';
import { Alert, Box } from '@mui/material';
import { getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface NoteProps {
  note: ItemAction<'note'>['item'];
}

const validSeverities = ["error", "warning", "success", "info"];

type AlertSeverity = "error" | "warning" | "success" | "info";

const getAlertSeverity = (style?: string | null, view?: string): AlertSeverity | undefined => (
  view === "validation" && (!style || !validSeverities.includes(style))
    ? "error"
    : (style && validSeverities.includes(style) ? style as AlertSeverity : undefined)
);

export const Note: React.FC<NoteProps> = ({ note }) => {
  const severity = getAlertSeverity(note?.props?.style, note?.view);

  return (
    <DescriptionWrapper text={note.description} title={note.label}>
      <Box sx={getLayoutStyleFromProps(note.props)}>
        {severity ? (
          <Alert variant="outlined" severity={severity}>
            <MarkdownView text={note.label} />
          </Alert>
        ) : (
          <MarkdownView text={note.label} />
        )}
      </Box>
    </DescriptionWrapper>
  );
};
