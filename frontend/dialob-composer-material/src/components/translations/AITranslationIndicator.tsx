import React from 'react';
import { Chip, Tooltip, Box } from '@mui/material';
import { AutoFixHigh } from '@mui/icons-material';
import { getLanguageName } from '../../utils/TranslationUtils';
import { TranslationMetadata } from '../../types';

export interface AITranslationIndicatorProps {
  metadata: TranslationMetadata;
  translatedText?: string;
  onClick?: () => void;
}

const AITranslationIndicator: React.FC<AITranslationIndicatorProps> = ({ metadata, translatedText, onClick }) => {
  const dateText = `AI translated from ${getLanguageName(metadata.sourceLanguage)} to ${getLanguageName(metadata.targetLanguage)} on ${new Date(metadata.timestamp).toLocaleString('en-GB')}`;
  const clickHint = onClick ? '\n\nClick to validate and remove AI flag' : '';
  const tooltipText = translatedText 
    ? `${translatedText}\n\n${dateText}${clickHint}`
    : `${dateText}${clickHint}`;

  return (
    <Tooltip title={<Box sx={{ whiteSpace: 'pre-line' }}>{tooltipText}</Box>} arrow>
      <Chip
        icon={<AutoFixHigh fontSize="small" />}
        label="AI"
        size="small"
        color="info"
        variant="outlined"
        onClick={onClick}
        sx={{ 
          ml: 1, 
          height: '20px', 
          fontSize: '0.7rem',
          cursor: onClick ? 'pointer' : 'default',
          '&:hover': onClick ? { backgroundColor: 'action.hover' } : {}
        }}
      />
    </Tooltip>
  );
};

export default AITranslationIndicator;
