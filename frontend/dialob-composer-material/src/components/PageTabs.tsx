import React from "react";
import { Menu, MenuItem, Button, IconButton, Box, Table, TableRow, TableBody, TableContainer, CircularProgress, Typography, SxProps, styled, Paper, TableCell } from '@mui/material';
import { Add, Close, ContentCopy, Key, Menu as MenuIcon, Tune, Visibility } from '@mui/icons-material';
import { DialobItem, DialobItems, useEditor } from "../dialob";
import { FormattedMessage } from "react-intl";


const MAX_PAGE_NAME_LENGTH = 40;
const MAX_LABEL_LENGTH = 60;
const MAX_RULE_LENGTH = 80;

const LabelButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1),
  paddingLeft: theme.spacing(2),
  justifyContent: 'flex-start',
  textTransform: 'none',
  width: '100%',
}));

const VisibilityButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1),
  paddingLeft: theme.spacing(2),
  justifyContent: 'space-between',
  textTransform: 'none',
  width: '100%',
}));

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

const LabelField: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { editor } = useEditor();
  const [label, setLabel] = React.useState<string>('');

  React.useEffect(() => {
    const localizedLabel = item && item.label && item.label[editor.activeFormLanguage];
    const formattedLabel = localizedLabel && localizedLabel.length > MAX_LABEL_LENGTH ?
      localizedLabel.substring(0, MAX_LABEL_LENGTH) + '...' :
      localizedLabel;
    setLabel(formattedLabel || '');
  }, [item, editor.activeFormLanguage]);

  return (
    <LabelButton variant='text' color='inherit'>
      {label ?
        <Typography>
          {label}
        </Typography> :
        <Typography color='text.hint'>
          <FormattedMessage id={`${item.type}.label`} />
        </Typography>
      }
    </LabelButton>
  );
}

export const VisibilityField: React.FC<{ item: DialobItem }> = ({ item }) => {
  return (
    <VisibilityButton
      variant='text'
      color='inherit'
      endIcon={<Visibility color='disabled' sx={{ mr: 1 }} />}
    >
      {item.activeWhen ?
        <Typography fontFamily='monospace'>
          {item.activeWhen.length > MAX_RULE_LENGTH ?
            item.activeWhen.substring(0, MAX_RULE_LENGTH) + '...' :
            item.activeWhen
          }
        </Typography> :
        <Typography color='text.hint'>
          <FormattedMessage id='visibility' />
        </Typography>
      }
    </VisibilityButton>
  );
}

const PageHeader: React.FC<{ item?: DialobItem }> = ({ item }) => {
  if (!item) {
    return null;
  }

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableBody>
          <TableRow>
            <TableCell>
              <LabelField item={item} />
            </TableCell>
          </TableRow>
          <TableRow>
            <TableCell>
              <VisibilityField item={item} />
            </TableCell>
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
      <Menu open={open} onClose={handleClose} anchorEl={anchorEl} disableScrollLock={true}>
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
      const variant = isActive ? 'contained' : 'text';
      return (
        <Button
          onClick={(e) => handlePageClick(e, itemId)}
          variant={variant}
          color='inherit'
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
