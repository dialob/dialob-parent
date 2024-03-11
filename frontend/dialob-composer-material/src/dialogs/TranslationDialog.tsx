import React from "react";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";
import { Close } from "@mui/icons-material";

const TranslationDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='xl'>
      <DialogTitle>
        <Typography fontWeight='bold'>Translations</Typography>
      </DialogTitle>
      <DialogContent sx={{ height: '70vh', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
        <Box sx={{ p: 3 }}>
          Dialog content
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Close />}>Close</Button>
      </DialogActions>
    </Dialog>
  )
}

export default TranslationDialog;
