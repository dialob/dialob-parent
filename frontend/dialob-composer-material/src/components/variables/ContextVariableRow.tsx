import React from 'react';
import { Button, IconButton, Menu, MenuItem, Switch, TableCell, TableRow, TextField, Typography } from '@mui/material';
import { Close, Delete, KeyboardArrowDown } from '@mui/icons-material';
import { ContextVariable, ContextVariableType, useComposer } from '../../dialob';

const types: ContextVariableType[] = [
  'text',
  'boolean',
  'number',
  'decimal',
  'date',
  'time'
]

const ContextTypeMenu: React.FC<{ variable: ContextVariable }> = ({ variable }) => {
  const { updateContextVariable } = useComposer();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleClick = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(e.currentTarget);
    e.stopPropagation();
  };

  const handleClose = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(null);
    e.stopPropagation();
  };

  const handleConvertType = (e: React.MouseEvent<HTMLElement>, type: ContextVariableType) => {
    handleClose(e);
    updateContextVariable(variable.name, type, variable.defaultValue);
  }

  return (
    <>
      <Button onClick={handleClick} component='span' sx={{ ml: 1 }} endIcon={<KeyboardArrowDown />} variant='text'>
        <Typography variant='subtitle2'>
          {variable.contextType}
        </Typography>
      </Button>
      <Menu open={open} onClose={handleClose} anchorEl={anchorEl}>
        {types.length > 0 && types.filter(type => type !== variable.contextType)
          .map((type, index) => (
            <MenuItem key={index} onClick={(e) => handleConvertType(e, type)}>
              <Typography textTransform='capitalize'>{type}</Typography>
            </MenuItem>
          ))}
      </Menu>
    </>
  );
}

const ContextVariableRow: React.FC<{ variable: ContextVariable }> = ({ variable }) => {
  const { updateContextVariable, deleteVariable } = useComposer();
  // name and published updates need to be connected to backend
  const [name, setName] = React.useState<string>(variable.name);
  const [published, setPublished] = React.useState<boolean>(variable.published ?? false);
  const [defaultValue, setDefaultValue] = React.useState<string | undefined>(variable.defaultValue);

  React.useEffect(() => {
    const id = setTimeout(() => {
      updateContextVariable(variable.name, variable.contextType, defaultValue);
    }, 1000);
    return () => clearTimeout(id);
  }, [defaultValue]);

  return (
    <TableRow key={variable.name}>
      <TableCell>
        <IconButton sx={{ p: 1, m: 1 }} onClick={() => deleteVariable(variable.name)}><Delete /></IconButton>
      </TableCell>
      <TableCell>
        <Switch sx={{ m: 1 }} checked={published} onChange={(e) => setPublished(e.target.checked)} />
      </TableCell>
      <TableCell>
        <TextField value={name} onChange={(e) => setName(e.target.value)} variant='standard' InputProps={{ disableUnderline: true }} fullWidth />
      </TableCell>
      <TableCell>
        <ContextTypeMenu variable={variable} />
      </TableCell>
      <TableCell>
        <TextField
          value={defaultValue}
          onChange={(e) => setDefaultValue(e.target.value)}
          variant='standard'
          InputProps={{ disableUnderline: true, endAdornment: <IconButton onClick={() => setDefaultValue('')}><Close /></IconButton> }}
          fullWidth
        />
      </TableCell>
    </TableRow>
  );
}

export default ContextVariableRow;
