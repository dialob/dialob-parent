import React from "react";
import { useEditor } from "../editor";
import { FormattedMessage } from "react-intl";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Box, Table, TableRow, TableCell, TableHead, TableContainer, TableBody, Paper } from "@mui/material";
import { Close } from "@mui/icons-material";
import markdownContent from './MARKDOWN_EDITOR.md?raw';
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { markdownComponents } from "../defaults/markdown";


const MarkdownHelpDialog: React.FC = () => {
  const { editor, setMarkdownHelpDialogOpen } = useEditor();

  const open = editor.markdownHelpDialogOpen || false;

  const handleClose = () => {
    setMarkdownHelpDialogOpen(false);
  }

  if (!open) {
    return null;
  }


  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth='xl'>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        <FormattedMessage id='dialogs.markdown.help.title' />
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '90vh' }}>
        <Box sx={{ p: 3, width: 1 }}>
          <Markdown children={markdownContent} remarkPlugins={[remarkGfm]} components={markdownComponents} />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default MarkdownHelpDialog;