import React from 'react';
import {
  Box, Button, IconButton, TableBody, TableCell, TableContainer,
  TableHead, TableRow, TextField, Typography
} from '@mui/material';
import { useEditor } from '../editor';
import { Close } from '@mui/icons-material';
import { StyledTable, StyledTextField } from './TableEditorComponents';
import { useComposer } from '../dialob';

interface ItemProp {
  key: string;
  value: string;
}

const PropertiesEditor: React.FC = () => {
  const { editor, setActiveItem } = useEditor();
  const { setItemProp, deleteItemProp } = useComposer();
  const item = editor.activeItem;
  const [props, setProps] = React.useState<ItemProp[]>([]);
  const [newProp, setNewProp] = React.useState<string>('');

  React.useEffect(() => {
    if (item?.props) {
      setProps(Object.entries(item.props).map(([key, value]) => ({ key, value })));
    }
  }, [item?.props]);

  const handleAddProp = () => {
    if (item) {
      setItemProp(item.id, newProp, '');
      setActiveItem({ ...item, props: { ...item.props, [newProp]: '' } });
    }
  }

  const handleEditProp = (key: string, value: any) => {
    if (item) {
      setItemProp(item.id, key, value);
      setActiveItem({ ...item, props: { ...item.props, [key]: value } });
    }
  }

  const handleDeleteProp = (key: string) => {
    if (item) {
      deleteItemProp(item.id, key);
      const newProps = { ...item.props };
      delete newProps[key];
      setActiveItem({ ...item, props: newProps });
    }
  }

  if (!item) {
    return null;
  }

  return (
    <Box>
      <Typography>Add property</Typography>
      <Box sx={{ display: 'flex', mb: 2, mt: 1 }}>
        <TextField variant='outlined' value={newProp} onChange={(e) => setNewProp(e.target.value)} />
        <Button color='inherit' variant='contained' onClick={() => handleAddProp()} sx={{ ml: 2 }} disabled={newProp === ''}>
          <Typography sx={{ px: 2 }}>Add</Typography>
        </Button>
      </Box>
      <TableContainer>
        <StyledTable>
          <TableHead>
            <TableRow>
              <TableCell width='10%' />
              <TableCell width='45%' sx={{ p: 1 }}><Typography fontWeight='bold'>Key</Typography></TableCell>
              <TableCell width='45%' sx={{ p: 1 }}><Typography fontWeight='bold'>Text</Typography></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {/* @ts-ignore */}
            {props.map(prop => <TableRow key={prop.key}>
              <TableCell align='center'>
                <IconButton onClick={() => handleDeleteProp(prop.key)}><Close color='error' /></IconButton>
              </TableCell>
              <TableCell>
                <Typography sx={{ p: 1 }}>{prop.key}</Typography>
              </TableCell>
              <TableCell>
                <StyledTextField variant='standard' InputProps={{
                  disableUnderline: true,
                }} value={prop.value} onChange={(e) => handleEditProp(prop.key, e.target.value)} />
              </TableCell>
            </TableRow>)}
          </TableBody>
        </StyledTable>
      </TableContainer>
    </Box>
  );
}

export default PropertiesEditor;
