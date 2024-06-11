import React from 'react';
import { Button, Typography, Box, TextareaAutosize, Menu, MenuItem } from '@mui/material';
import { Add } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { LocalizedString, ValueSetEntry, useComposer } from '../../dialob';


const ChoiceTextEditor: React.FC<{
  entry: ValueSetEntry,
  onUpdate: (entry: ValueSetEntry, label: LocalizedString) => void
}> = ({ entry, onUpdate }) => {
  const { form } = useComposer();
  const formLanguages = form.metadata.languages;
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();

  React.useEffect(() => {
    setLocalizedString(entry.label);
  }, [entry]);

  React.useEffect(() => {
    if (localizedString && localizedString !== entry.label) {
      const id = setTimeout(() => {
        onUpdate(entry, localizedString)
      }, 300);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [localizedString]);

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString({ ...localizedString, [language]: value });
  }

  return (
    <>
      {formLanguages?.map((language) => {
        const localizedText = localizedString ? localizedString[language] : '';
        return (
          <Box key={language}>
            <Typography color='text.hint' variant='caption'><FormattedMessage id={`locales.${language}`} /></Typography>
            <TextareaAutosize style={{ width: '100%' }} minRows={2} value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />
          </Box>
        );
      })}
    </>
  );
}

export { ChoiceTextEditor };
