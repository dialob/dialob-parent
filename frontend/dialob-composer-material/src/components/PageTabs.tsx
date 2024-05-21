import React from "react";
import {
  Button, IconButton, Box, Table, TableRow, TableBody, TableContainer, CircularProgress, Typography,
  Paper, TableCell, Grid
} from '@mui/material';
import { Add } from '@mui/icons-material';
import { DialobItem, DialobItems, useComposer } from "../dialob";
import { useEditor } from "../editor";
import { LabelField, OptionsMenu, VisibilityField } from "../items/ItemComponents";
import { DEFAULT_ITEMTYPE_CONFIG } from "../defaults";


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

const PageTabs: React.FC<{ items: DialobItems }> = ({ items }) => {
  const { addItem } = useComposer();
  const { editor, setActivePage } = useEditor();
  const rootItemId = Object.values(items).find((item: DialobItem) => item.type === 'questionnaire')?.id;
  const rootItem = rootItemId ? items[rootItemId] : undefined;

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
    const groupTemplate = DEFAULT_ITEMTYPE_CONFIG.categories.find(c => c.type === 'structure')!.items.find(i => i.config.type === 'group')!.config;
    addItem(groupTemplate, 'questionnaire');
  }

  const pages =
    rootItem &&
    rootItem.items &&
    rootItem.items.map((itemId: string, index: number) => {
      const item = items[itemId];
      const isActive = item.id === editor.activePage?.id;
      const variant = isActive ? 'contained' : 'text';
      const activeSx = isActive ? { backgroundColor: 'primary.main' } : {};
      return (
        <Box sx={{ border: 1, borderColor: 'divider', ...activeSx }}>
          <Button
            onClick={(e) => handlePageClick(e, itemId)}
            variant={variant}
            key={index}
            endIcon={<OptionsMenu item={item} isPage light={isActive} />}
          >
            <Typography>{getPageTabTitle(item, editor.activeFormLanguage)}</Typography>
          </Button >
        </Box>
      );
    });

  if (editor.activePage === undefined) {
    return <CircularProgress />;
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
