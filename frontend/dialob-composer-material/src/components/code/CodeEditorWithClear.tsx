import React from 'react';
import { Box, IconButton, Tooltip } from '@mui/material';
import { Close } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';

export interface CodeEditorWithClearProps {
  value: string | undefined;
  onClear: () => void;
  children: React.ReactNode;
}

export const CodeEditorWithClear: React.FC<CodeEditorWithClearProps> = ({ 
  value, 
  onClear, 
  children,
}) => {
  const shouldShowClear = value !== undefined;

  return (
    <Box sx={{ 
      display: 'flex', 
      alignItems: 'center',
      gap: 0.5
    }}>
      <Box sx={{ flexGrow: 1 }}>
        {children}
      </Box>
      <Tooltip title={<FormattedMessage id="buttons.clear" />}>
        <span>
          <IconButton
            disabled={!shouldShowClear}
            onClick={onClear}
            size="small"
          >
            <Close fontSize="small" />
          </IconButton>
        </span>
      </Tooltip>
    </Box>
  );
};
