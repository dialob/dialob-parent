import React from 'react';
import { ItemProp, PropValue } from "./editors/PropertiesEditor";
import { TableCell, TableRow, Typography, IconButton } from '@mui/material';
import { Close } from '@mui/icons-material';
import { InputProp } from './propEditors/InputProp';

const PropItem: React.FC<{
  prop: ItemProp,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  propEditor: { component: any, props?: any } | undefined,
  onEdit: (key: string, value: PropValue) => void,
  onDelete: (key: string) => void
}> = ({ prop, propEditor, onEdit, onDelete }) => {
  const [value, setValue] = React.useState<PropValue>(prop.value);

  React.useEffect(() => {
    if (value !== '' && value !== prop.value) {
      const id = setTimeout(() => {
        onEdit(prop.key, value);
      }, 300);
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
        {propEditor ? propEditor.component({value, setValue, ...propEditor.props}) : InputProp({value, setValue})}
      </TableCell>
    </TableRow>
  );
}

export default PropItem;
