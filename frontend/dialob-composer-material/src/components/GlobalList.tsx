import React from 'react';
import { ValueSetEntry } from '../dialob';
import { Table, TableBody, TableRow, TableCell, TableHead, Typography } from '@mui/material';
import { StyledTable } from './TableEditorComponents';
import { FormattedMessage } from 'react-intl';
import { useEditor } from '../editor';

const GlobalList: React.FC<{ entries?: ValueSetEntry[] }> = ({ entries }) => {
  const { editor } = useEditor();

  if (!entries) {
    return null;
  }

  return (
    <StyledTable sx={{ mt: 2 }}>
      <TableHead>
        <TableRow>
          <TableCell sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.options.key' /></Typography>
          </TableCell>
          <TableCell sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.options.text' /></Typography>
          </TableCell>
          <TableCell sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.options.rule' /></Typography>
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {entries.map((entry, idx) => (
          <TableRow key={entry.id}>
            <TableCell sx={{ p: 1 }}>
              {entry.id}
            </TableCell>
            <TableCell sx={{ p: 1 }}>
              {entry.label[editor.activeFormLanguage]}
            </TableCell>
            <TableCell sx={{ p: 1 }}>
              {entry.when}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </StyledTable>
  );
}

export default GlobalList;
