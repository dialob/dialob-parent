import React from 'react';
import { ValueSetEntry, useComposer } from '../dialob';
import { TableBody, TableRow, TableCell, TableHead, Typography } from '@mui/material';
import { StyledTable } from './TableEditorComponents';
import { FormattedMessage } from 'react-intl';

const GlobalList: React.FC<{ entries?: ValueSetEntry[] }> = ({ entries }) => {
  const { form } = useComposer();
  const formLanguages = form.metadata.languages;

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
          {formLanguages?.map(lang =>
            <TableCell sx={{ p: 1 }} key={lang}>
              <Typography fontWeight='bold'><FormattedMessage id='dialogs.options.text' /> - <FormattedMessage id={`locales.${lang}`} /></Typography>
            </TableCell>
          )}
        </TableRow>
      </TableHead>
      <TableBody>
        {entries.map((entry) => (
          <TableRow key={entry.id}>
            <TableCell sx={{ p: 1 }}>
              {entry.id}
            </TableCell>
            {formLanguages?.map(lang =>
              <TableCell sx={{ p: 1 }} key={lang}>
                {entry.label[lang]}
              </TableCell>
            )}
          </TableRow>
        ))}
      </TableBody>
    </StyledTable>
  );
}

export default GlobalList;
