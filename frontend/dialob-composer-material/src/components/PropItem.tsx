import React from 'react';
import { ItemProp } from "./editors/PropertiesEditor";
import { TableCell, TableRow, Typography, IconButton } from '@mui/material';
import { Close } from '@mui/icons-material';
import { StyledTextField } from './TableEditorComponents';

const PropItem: React.FC<{
  prop: ItemProp,
  onEdit: (key: string, value: string) => void,
  onDelete: (key: string) => void
}> = ({ prop, onEdit, onDelete }) => {
  const [value, setValue] = React.useState<string>(prop.value || '');

  React.useEffect(() => {
    if (value !== '' && value !== prop.value) {
      const id = setTimeout(() => {
        onEdit(prop.key, value);
      }, 1000);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [value]);

  return (
    <TableRow key={prop.key}>
      <TableCell align='center'>
        <IconButton onClick={() => onDelete(prop.key)}><Close color='error' /></IconButton>
      </TableCell>
      <TableCell>
        <Typography sx={{ p: 1 }}>{prop.key}</Typography>
      </TableCell>
      <TableCell>
        <StyledTextField variant='standard' InputProps={{
          disableUnderline: true,
        }} value={value} onChange={(e) => setValue(e.target.value)} />
      </TableCell>
    </TableRow>
  );
}

export default PropItem;
