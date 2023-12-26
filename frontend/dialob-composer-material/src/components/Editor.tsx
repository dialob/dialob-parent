import React from 'react';
import { Menu, MenuItem, Button, IconButton, Box, TextField, Table, TableRow, styled, Tab, TableBody, TableContainer } from '@mui/material';
import { Add, Menu as MenuIcon } from '@mui/icons-material';
import Items from '../items';
import { DialobItem, useComposer } from '../dialob';


const MAX_PAGE_NAME_LENGTH = 40;

const createChildren = (items: { [key: string]: DialobItem }, activePage: DialobItem | undefined) => {
  return (
    activePage &&
    activePage.items &&
    activePage.items
      .map((itemId: string) => items[itemId])
      .map((item: DialobItem) => Items.itemFactory(item))
  );
};

const getPageTabTitle = (item: DialobItem): string => {
  const rawLabel = item.label ? item.label['en'] : null;
  if (!rawLabel) {
    return item.id;
  } else {
    return rawLabel.length > MAX_PAGE_NAME_LENGTH
      ? rawLabel.substring(0, MAX_PAGE_NAME_LENGTH) + '…'
      : rawLabel;
  }
};

const PageHeader: React.FC<{ item: DialobItem }> = ({ item }) => {
  return (
    <TableContainer sx={{ borderRadius: 2, borderTopLeftRadius: 0, border: 1, borderColor: 'divider' }}>
      <Table>
        <TableBody>
          <TableRow sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <TextField
              placeholder='Page label'
              variant='standard'
              InputProps={{ disableUnderline: true }}
              sx={{ p: 1 }}
            />
          </TableRow>
          <TableRow>
            <TextField
              placeholder='Visibility'
              variant='standard'
              InputProps={{ disableUnderline: true }}
              sx={{ p: 1 }}
            />
          </TableRow>
        </TableBody>
      </Table>
    </TableContainer>
  );
}

const PageMenuButton: React.FC = () => {
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
  const handleItemClick = (e: React.MouseEvent<HTMLElement>) => {
    handleClose(e);
  }

  return (
    <Box>
      <IconButton onClick={handleClick} component='span' sx={{ ml: 1 }}>
        <MenuIcon />
      </IconButton>
      <Menu open={open} onClose={handleClose} anchorEl={anchorEl}>
        <MenuItem onClick={handleItemClick}>
          Options...
        </MenuItem>
        <MenuItem onClick={handleItemClick}>
          Change ID...
        </MenuItem>
        <MenuItem onClick={handleItemClick}>
          Delete
        </MenuItem>
        <MenuItem onClick={handleItemClick}>
          Duplicate
        </MenuItem>
      </Menu>
    </Box>
  );
}

const Editor: React.FC = () => {
  const state = useComposer();
  const items = state.form.data;
  const rootItemId = Object.values(items).find((item: DialobItem) => item.type === 'questionnaire')?.id;

  if (!rootItemId) {
    return null;
  }

  const rootItem = items[rootItemId];
  const defaultActivePage = rootItem.items ? items[rootItem.items[0]] : undefined;
  const [activePage, setActivePage] = React.useState<DialobItem | undefined>(defaultActivePage);

  const handlePageClick = (e: React.MouseEvent<HTMLElement>, id: string) => {
    setActivePage(items[id]);
    e.stopPropagation();
  };

  const pages =
    rootItem &&
    rootItem.items &&
    rootItem.items.map((itemId: string, index: number) => {
      const item = items[itemId];
      const isActive = item === activePage;
      const variant = isActive ? 'outlined' : 'text';
      const activeSx = isActive ? { marginBottom: -0.1, borderBottom: 2, borderBottomColor: 'mainContent.light' } : {};
      return (
        <Button
          onClick={(e) => handlePageClick(e, itemId)}
          variant={variant}
          color='inherit'
          sx={{ borderColor: 'divider', ...activeSx }}
          key={index}
        >
          {getPageTabTitle(item)}
          < PageMenuButton />
        </Button >
      );
    });

  if (activePage === undefined && pages && pages.length > 0) {
    return null;
  }

  return (
    <Box>
      <Box sx={{ display: 'flex' }}>
        {pages}
        <Box sx={{ flexGrow: 1 }} />
        <IconButton sx={{ alignSelf: 'center' }}>
          <Add />
        </IconButton>
      </Box>
      <Box>
        <PageHeader item={activePage!} />
      </Box>
      {createChildren(items, activePage)}
    </Box>
  );
};

export default Editor;
