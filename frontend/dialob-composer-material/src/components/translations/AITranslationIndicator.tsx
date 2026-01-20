import React from 'react';
import { Chip, Tooltip, Box } from '@mui/material';
import { AutoFixHigh } from '@mui/icons-material';
import { useIntl } from 'react-intl';
import { getLanguageName } from '../../utils/TranslationUtils';
import { TranslationMetadata } from '../../types';

export interface AITranslationIndicatorProps {
  metadata: TranslationMetadata;
  translatedText?: string;
  onClick?: () => void;
}

const AITranslationIndicator: React.FC<AITranslationIndicatorProps> = ({ metadata, translatedText, onClick }) => {
  const intl = useIntl();
  
  const dateText = intl.formatMessage(
    { id: 'dialogs.translations.ai.indicator.tooltip' },
    {
      source: getLanguageName(metadata.sourceLanguage),
      target: getLanguageName(metadata.targetLanguage),
      date: new Date(metadata.timestamp).toLocaleString('en-GB')
    }
  );
  
  const clickHint = onClick 
    ? `\n\n${intl.formatMessage({ id: 'dialogs.translations.ai.indicator.clickHint' })}` 
    : '';
  
  const tooltipText = translatedText 
    ? `${translatedText}\n\n${dateText}${clickHint}`
    : `${dateText}${clickHint}`;

  return (
    <Tooltip title={<Box sx={{ whiteSpace: 'pre-line' }}>{tooltipText}</Box>} arrow>
      <Chip
        icon={<AutoFixHigh fontSize="small" />}
        label={intl.formatMessage({ id: 'dialogs.translations.ai.indicator.label' })}
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
