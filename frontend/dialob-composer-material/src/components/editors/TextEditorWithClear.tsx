import React from 'react';
import { TextField, IconButton, TextFieldProps, Tooltip } from '@mui/material';
import { Close } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';

export interface TextEditorWithClearProps extends Omit<TextFieldProps, 'value' | 'onChange' | 'onClear'> {
  value: string | undefined;
  onChange: (value: string) => void;
  onClear?: () => void;
}

export const TextEditorWithClear: React.FC<TextEditorWithClearProps> = ({
  value,
  onChange,
  onClear,
  InputProps,
  ...props
}) => {
  const handleClear = () => {
    if (onClear) {
      onClear();
    }
  };

  const shouldShowClear = onClear && value !== undefined;

  return (
    <TextField
      {...props}
      value={value ?? ''}
      onChange={(e) => onChange(e.target.value)}
      InputProps={{
        ...InputProps,
        endAdornment: (
          <Tooltip title={<FormattedMessage id="buttons.clear" />}>
            <span>
              <IconButton
                onClick={handleClear}
                disabled={!shouldShowClear}
                size="small"
              >
                <Close fontSize="small" />
              </IconButton>
            </span>
          </Tooltip>
        ),
      }}
    />
  );
};
