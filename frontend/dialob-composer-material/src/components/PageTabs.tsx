import React from "react";
import { Menu, MenuItem, Button, IconButton, Box, TextField, Table, TableRow, styled, Tab, TableBody, TableContainer, CircularProgress, Typography } from '@mui/material';
import { Add, Close, ContentCopy, Key, Menu as MenuIcon, Tune, Visibility } from '@mui/icons-material';
import { DialobItem, DialobItems, useEditor } from "../dialob";
import { FormattedMessage, useIntl } from "react-intl";


const MAX_PAGE_NAME_LENGTH = 40;

const getPageTabTitle = (item: DialobItem, language: string): string => {
  const rawLabel = item.label ? item.label[language] : null;
  if (!rawLabel) {
    return item.id;
  } else {
    return rawLabel.length > MAX_PAGE_NAME_LENGTH
      ? rawLabel.substring(0, MAX_PAGE_NAME_LENGTH) + '…'
      : rawLabel;
  }
};

const PageHeader: React.FC<{ item?: DialobItem }> = ({ item }) => {
  const intl = useIntl();
  if (!item) {
    return null;
  }
  return (
    <TableContainer sx={{ borderRadius: 2, borderTopLeftRadius: 0, border: 1, borderColor: 'divider' }}>
      <Table>
        <TableBody>
          <TableRow sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <TextField
              component='td'
              placeholder={intl.formatMessage({ id: 'page.label' })}
              variant='standard'
              fullWidth
              InputProps={{ disableUnderline: true }}
              sx={{ p: 1 }}
            />
          </TableRow>
          <TableRow>
            <Button
              variant='text'
              fullWidth
              color='inherit'
              component='td'
              endIcon={<Visibility color='disabled' sx={{ mr: 1 }} />}
              sx={{ p: 1, justifyContent: 'space-between' }}
            >
              <Typography color='text.hint'>
                <FormattedMessage id='visibility' />
              </Typography>
            </Button>
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
          <Tune sx={{ mr: 1 }} fontSize='small' />
          Options
        </MenuItem>
        <MenuItem onClick={handleItemClick}>
          <Key sx={{ mr: 1 }} fontSize='small' />
          Change ID
        </MenuItem>
        <MenuItem onClick={handleItemClick}>
          <Close sx={{ mr: 1 }} fontSize='small' />
          Delete
        </MenuItem>
        <MenuItem onClick={handleItemClick}>
          <ContentCopy sx={{ mr: 1 }} fontSize='small' />
          Duplicate
        </MenuItem>
      </Menu>
    </Box>
  );
}

const PageTabs: React.FC<{ items: DialobItems }> = ({ items }) => {
  const { editor, setActivePage } = useEditor();
  const rootItemId = Object.values(items).find((item: DialobItem) => item.type === 'questionnaire')?.id;
  const rootItem = rootItemId ? items[rootItemId] : undefined;

  React.useEffect(() => {
    const defaultActivePage = rootItem && rootItem.items ? items[rootItem.items[0]] : undefined;
    if (defaultActivePage) {
      setActivePage(defaultActivePage);
    }
  }, []);

  const handlePageClick = (e: React.MouseEvent<HTMLElement>, id: string) => {
    setActivePage(items[id]);
    e.stopPropagation();
  };

  const pages =
    rootItem &&
    rootItem.items &&
    rootItem.items.map((itemId: string, index: number) => {
      const item = items[itemId];
      const isActive = item === editor.activePage;
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
          {getPageTabTitle(item, editor.activeFormLanguage)}
          < PageMenuButton />
        </Button >
      );
    });

  if (editor.activePage === undefined) {
    return <CircularProgress />;
  }
  return (
    <Box sx={{ mb: 1 }}>
      <Box sx={{ display: 'flex' }}>
        {pages}
        <Box sx={{ flexGrow: 1 }} />
        <IconButton sx={{ alignSelf: 'center' }}>
          <Add />
        </IconButton>
      </Box>
      <PageHeader item={editor.activePage} />
    </Box>

  )
}

export default PageTabs;
