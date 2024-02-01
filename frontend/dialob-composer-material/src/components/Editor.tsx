import React from 'react';
import { Box } from '@mui/material';
import { DialobItem, DialobItems, useComposer } from '../dialob';
import { useEditor } from '../editor';
import Items from '../items';
import PageTabs from './PageTabs';


const createChildren = (items: DialobItems, activePage?: DialobItem) => {
  if (!activePage || !activePage.items) {
    return null;
  }
  return (
    activePage.items
      .map((itemId: string) => items[itemId])
      .map((item: DialobItem) => Items.itemFactory(item))
  );
};

const Editor: React.FC = () => {
  const { form } = useComposer();
  const { editor } = useEditor();

  return (
    <Box>
      <PageTabs items={form.data} />
      {createChildren(form.data, editor.activePage)}
    </Box>
  );
};

export default Editor;
