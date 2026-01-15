import React from 'react';
import { CircularProgress, IconButton, Tooltip } from '@mui/material';
import { Translate } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { getLanguageName } from '../../utils/TranslationUtils';

interface TranslateButtonProps {
  sourceLanguage: string;
  targetLanguage: string;
  isTranslating: boolean;
  onClick: () => void;
  color?: 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success';
}

const TranslateButton: React.FC<TranslateButtonProps> = ({
  sourceLanguage,
  targetLanguage,
  isTranslating,
  onClick,
  color = 'primary'
}) => {
  const sourceLanguageName = getLanguageName(sourceLanguage);
  const targetLanguageName = getLanguageName(targetLanguage);

  return (
    <Tooltip 
      title={
        <FormattedMessage 
          id='buttons.translate.tooltip' 
          values={{ source: sourceLanguageName, target: targetLanguageName }}
        />
      }
    >
      <IconButton
        onClick={onClick}
        disabled={isTranslating}
        sx={{ p: 0.5 }}
      >
        {isTranslating ? <CircularProgress size={16} /> : <Translate color={color} />}
      </IconButton>
    </Tooltip>
  );
};

export default TranslateButton;
