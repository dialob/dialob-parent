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

const PageTab: React.FC<{ item: DialobItem, onClick: (e: React.MouseEvent<HTMLElement>) => void }> = ({ item, onClick }) => {
  const { editor } = useEditor();
  const isActive = item.id === editor.activePage?.id;
  const variant = isActive ? 'contained' : 'text';
  const [highlighted, setHighlighted] = React.useState<boolean>(false);

  React.useEffect(() => {
    if (editor?.highlightedItem?.id === item.id) {
      setHighlighted(true);
    }
    const id = setTimeout(() => {
      setHighlighted(false);
    }, 3000);
    return () => clearTimeout(id);
  }, [editor.highlightedItem, item.id])

  return (
    <Box sx={{ border: 1, borderColor: 'divider' }}>
      <Button
        onClick={onClick}
        variant={variant}
        color={highlighted ? 'info' : 'primary'}
        endIcon={<OptionsMenu item={item} isPage light={isActive} />}
      >
        <Typography>{getPageTabTitle(item, editor.activeFormLanguage)}</Typography>
      </Button >
    </Box>
  );
}

const PageTabs: React.FC<{ items: DialobItems }> = ({ items }) => {
  const { addItem } = useComposer();
  const { editor, setActivePage } = useEditor();
  const rootItemId = Object.values(items).find((item: DialobItem) => item.type === 'questionnaire')?.id;
  const rootItem = rootItemId ? items[rootItemId] : undefined;
  const noPages = rootItem && !rootItem.items;
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
    const groupTemplate = DEFAULT_ITEMTYPE_CONFIG.categories.find(c => c.type === 'structure')!.items.find(i => i.config.type === 'group')!.config;
    addItem(groupTemplate, 'questionnaire');
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
        {noPages ?
          <Button onClick={handleCreate} color='primary' endIcon={<Add />} variant='contained'>
            <Typography textTransform='none'>No pages yet, click here to add one</Typography>
          </Button> :
          <IconButton sx={{ alignSelf: 'center' }} onClick={handleCreate}>
            <Add color='primary' />
          </IconButton>
        }
      </Box>
      <PageHeader item={editor.activePage} />
    </Box>
  )
}

export default PageTabs;
