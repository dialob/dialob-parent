import React from "react";
import {
  Button, IconButton, Box, Table, TableRow, TableBody, TableContainer, CircularProgress, Typography,
  Paper, TableCell, Grid
} from '@mui/material';
import { Add } from '@mui/icons-material';
import { useComposer } from "../dialob";
import { useEditor } from "../editor";
import { LabelField, OptionsMenu, VisibilityField } from "../items/ItemComponents";
import { DEFAULT_ITEMTYPE_CONFIG } from "../defaults";
import { FormattedMessage } from "react-intl";
import { useBackend } from "../backend/useBackend";
import { DialobItem, DialobItems } from "../types";


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

const PageTab: React.FC<{ item: DialobItem, onClick: (e: React.MouseEvent<HTMLElement>) => void }> = ({ item, onClick }) => {
  const { editor } = useEditor();
  const isActive = item.id === editor.activePage?.id;
  const variant = isActive ? 'contained' : 'text';
  const fontWeight = isActive ? 'bold' : 'normal';

  return (
    <Box sx={{ border: 1, borderColor: 'divider' }}>
      <Button
        onClick={onClick}
        variant={variant}
        color={'primary'}
        endIcon={<OptionsMenu item={item} isPage light={isActive} />}
      >
        <Typography textTransform='none' fontWeight={fontWeight}>{getPageTabTitle(item, editor.activeFormLanguage)}</Typography>
      </Button >
    </Box>
  );
}

const PageTabs: React.FC<{ items: DialobItems }> = ({ items }) => {
  const { addItem } = useComposer();
  const { editor, setActivePage } = useEditor();
  const { config } = useBackend();
  const rootItemId = Object.values(items).find((item: DialobItem) => item.type === 'questionnaire')?.id;
  const rootItem = rootItemId ? items[rootItemId] : undefined;
  const noPages = rootItem && (!rootItem.items || rootItem.items.length === 0);
  const noActivePage = rootItem && rootItem.items && rootItem.items.length > 0 && !editor.activePage;

  React.useEffect(() => {
    const defaultActivePage = rootItem && rootItem.items ? items[rootItem.items[0]] : undefined;
    const activePage = editor.activePage;
    if (activePage) {
      setActivePage(items[activePage.id]);
    } else if (defaultActivePage) {
      setActivePage(defaultActivePage);
    }
  }, [rootItem, items, editor.activePage, setActivePage]);

  const handlePageClick = (e: React.MouseEvent<HTMLElement>, id: string) => {
    setActivePage(items[id]);
    e.stopPropagation();
  };

  const handleCreate = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    const resolvedConfig = config.itemTypes ?? DEFAULT_ITEMTYPE_CONFIG;
    const groupTemplate = resolvedConfig.categories.find(c => c.type === 'structure')!.items.find(i => i.config.type === 'group')!.config;
    const pageTemplate = { ...groupTemplate, view: 'page' };
    addItem(pageTemplate, 'questionnaire');
  }

  const pages =
    rootItem &&
    rootItem.items &&
    rootItem.items.map((itemId: string, index: number) => {
      const item = items[itemId];
      return (
        <PageTab
          key={index}
          item={item}
          onClick={(e) => handlePageClick(e, itemId)}
        />
      );
    });

  if (noActivePage) {
    return <CircularProgress />;
  }

  if (noPages) {
    return (
      <Box sx={{ mb: 1, display: 'flex', flexDirection: 'row', justifyContent: 'flex-end' }}>
        <Button onClick={handleCreate} color='primary' endIcon={<Add />} variant='contained'>
          <Typography textTransform='none'><FormattedMessage id='page.none' /></Typography>
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ mb: 1 }}>
      <Box sx={{ display: 'flex' }}>
        <Grid container>
          {pages && pages.map((page, index) => (
            <Grid item key={index}>
              {page}
            </Grid>
          ))}
        </Grid>
        <Box sx={{ flexGrow: 1 }} />
        <IconButton sx={{ alignSelf: 'center' }} onClick={handleCreate}>
          <Add color='primary' />
        </IconButton>
      </Box>
      <PageHeader item={editor.activePage} />
    </Box>
  )
}

export default PageTabs;
