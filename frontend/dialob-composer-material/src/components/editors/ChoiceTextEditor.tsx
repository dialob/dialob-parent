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
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  React.useEffect(() => {
    setLocalizedString(entry.label);
  }, [entry]);

  React.useEffect(() => {
    if (localizedString) {
      const id = setTimeout(() => {
        onUpdate(entry, localizedString)
      }, 1000);
      return () => clearTimeout(id);
    }
  }, [localizedString]);

  const handleAdd = (language: string) => {
    setLocalizedString({ ...localizedString, [language]: '' });
  }

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString({ ...localizedString, [language]: value });
  }

  return (
    <>
      <Box display='flex'>
        <Box flexGrow={1} />
        <Button variant='outlined' onClick={(e) => setAnchorEl(e.currentTarget)}
          disabled={formLanguages?.length === (localizedString ? Object.keys(localizedString).length : 0)}
          sx={{ textTransform: 'none', ml: 1 }} endIcon={<Add />}>
          <FormattedMessage id={`dialogs.options.choices.text.add`} />
        </Button>
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={() => setAnchorEl(null)}
        >
          {formLanguages?.filter((language) => !localizedString?.[language])
            .map((language) => (
              <MenuItem key={language} onClick={() => {
                handleAdd(language);
                setAnchorEl(null);
              }}><FormattedMessage id={`locales.${language}`} /></MenuItem>
            ))}
        </Menu>
      </Box>
      {localizedString && Object.keys(localizedString).map((language) => {
        const localizedText = localizedString[language];
        return (
          <Box key={language}>
            <Typography color='text.hint'><FormattedMessage id={`locales.${language}`} /></Typography>
            <TextareaAutosize style={{ width: '100%' }} minRows={2} value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />
          </Box>
        );
      })}
    </>
  );
}

export default ChoiceTextEditor;
