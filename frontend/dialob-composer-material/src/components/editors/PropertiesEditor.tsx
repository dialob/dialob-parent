import React from 'react';
import { Box, Button, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { useEditor } from '../../editor'
import { BorderedTable } from '../TableEditorComponents';
import { useComposer } from '../../dialob';
import { FormattedMessage } from 'react-intl';
import PropItem from './PropItem';
import { useBackend } from '../../backend/useBackend';
import { DEFAULT_ITEMTYPE_CONFIG } from '../../defaults';
import { findItemPropEditor } from '../../utils/ConfigUtils';
import { ItemProp } from './types';

const PropertiesEditor: React.FC = () => {
  const { editor, setActiveItem } = useEditor();
  const { setItemProp, deleteItemProp } = useComposer();
  const { config } = useBackend();
  const item = editor.activeItem;
  const [props, setProps] = React.useState<ItemProp[]>([]);
  const [newProp, setNewProp] = React.useState<string>('');

  React.useEffect(() => {
    if (item?.props) {
      setProps(Object.entries(item.props).map(([key, value]) => ({ key, value })));
    }
  }, [item?.props]);

  if (!item) {
    return null;
  }

  const itemTypeConfig = config.itemTypes ?? DEFAULT_ITEMTYPE_CONFIG;
  const propEditors = findItemPropEditor(itemTypeConfig, item.view ?? item.type);

  const handleAddProp = () => {
    if (item) {
      const isArray = propEditors && propEditors[newProp]?.props?.options?.length > 0;
      setItemProp(item.id, newProp, isArray ? [] : '');
      setActiveItem({ ...item, props: { ...item.props, [newProp]: isArray ? [] : '' } });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
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

  return (
    <Box>
      <Typography><FormattedMessage id='dialogs.options.properties.add' /></Typography>
      <Box sx={{ display: 'flex', mb: 2, mt: 1 }}>
        <TextField variant='outlined' value={newProp} onChange={(e) => setNewProp(e.target.value)} />
        <Button color='inherit' variant='contained' onClick={() => handleAddProp()} sx={{ ml: 2 }} disabled={newProp === ''}>
          <Typography sx={{ px: 2 }}><FormattedMessage id='dialogs.options.properties.add.short' /></Typography>
        </Button>
      </Box>
      {props.length > 0 && <TableContainer>
        <BorderedTable>
          <TableHead>
            <TableRow>
              <TableCell width='10%' />
              <TableCell width='45%' sx={{ p: 1 }}><Typography fontWeight='bold'><FormattedMessage id='dialogs.options.key' /></Typography></TableCell>
              <TableCell width='45%' sx={{ p: 1 }}><Typography fontWeight='bold'><FormattedMessage id='dialogs.options.value' /></Typography></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {props.map(prop => <PropItem key={prop.key} prop={prop} propEditor={propEditors && propEditors[prop.key]} onEdit={handleEditProp} onDelete={handleDeleteProp} />)}
          </TableBody>
        </BorderedTable>
      </TableContainer>}
    </Box>
  );
}

export { PropertiesEditor };
