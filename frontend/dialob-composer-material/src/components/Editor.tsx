import React from 'react';
import { Box } from '@mui/material';
import { useComposer } from '../dialob';
import { useEditor } from '../editor';
import Items from '../items';
import PageTabs from './PageTabs';
import { AddItemMenu } from '../items/ItemComponents';
import { ItemConfig } from '../defaults/types';
import { useBackend } from '../backend/useBackend';
import { DialobItem, DialobItems } from '../types';


const createChildren = (items: DialobItems, itemConfig: ItemConfig, activePage?: DialobItem) => {
  if (!activePage || !activePage.items) {
    return null;
  }
  return (
    activePage.items
      .map((itemId: string) => items[itemId])
      .map((item: DialobItem) => Items.itemFactory(item, itemConfig))
  );
};

const Editor: React.FC = () => {
  const { form } = useComposer();
  const { editor } = useEditor();
  const { config } = useBackend();

  return (
    <Box id='scroll-container'>
      <PageTabs items={form.data} />
      {createChildren(form.data, config.itemEditors, editor.activePage)}
      {editor.activePage && <Box sx={{ mb: 2 }}><AddItemMenu item={editor.activePage} /></Box>}
    </Box>
  );
};

export default Editor;
