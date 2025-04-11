import React from "react";
import { Box, Grid, IconButton, Table, TableBody, TableCell, TableRow, TextField, Typography, alpha, useTheme } from '@mui/material';
import { Close, KeyboardArrowDown, KeyboardArrowUp, Visibility } from "@mui/icons-material";
import { TreeItem } from "@atlaskit/tree";
import { TreeDraggableProvided } from "@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types";
import { LocalizedString, ValueSetEntry, useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import { FormattedMessage } from "react-intl";
import { useErrorColor } from "../utils/ErrorUtils";
import { useEditor } from "../editor";
import CodeMirror from "./code/CodeMirror";
import { getLanguageName } from "../utils/TranslationUtils";


export interface ChoiceItemProps {
  item: TreeItem,
  valueSetId?: string,
  provided: TreeDraggableProvided,
  isGlobal?: boolean,
  expanded: string[],
  onToggleExpand: (id: string) => void,
  onRuleEdit: (entry: ValueSetEntry, rule: string) => void,
  onTextEdit: (entry: ValueSetEntry, label: LocalizedString) => void,
  onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void,
}

const MAX_CHOICE_LABEL_LENGTH = 40;

const getLabel = (entry: ValueSetEntry, language: string) => {
  const localizedLabel = entry.label[language] ? entry.label[language] : undefined;
  if (!localizedLabel) {
    return <></>;
  }
  if (localizedLabel.length > MAX_CHOICE_LABEL_LENGTH) {
    return <Typography>{localizedLabel.substring(0, MAX_CHOICE_LABEL_LENGTH) + '...'}</Typography>;
  }
  return <Typography>{localizedLabel}</Typography>;
}

const ChoiceItem: React.FC<ChoiceItemProps> = (props) => {
  const { item, valueSetId, provided, isGlobal, expanded, onToggleExpand, onRuleEdit, onTextEdit, onDelete, onUpdateId } = props;
  const { form } = useComposer();
  const { editor } = useEditor();
  const theme = useTheme();
  const entry: ValueSetEntry = item.data.entry;
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const error = editor.errors?.find(e => e.itemId === valueSetId && e.index == item.data.index);
  const errorColor = useErrorColor(error);
  const backgroundColor = errorColor || theme.palette.background.paper;
  const entryExpanded = expanded.includes(entry.id);
  const [open, setOpen] = React.useState(false);
  const [idValue, setIdValue] = React.useState<string>(entry.id);
  const [rule, setRule] = React.useState<string>(entry.when || '');
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();

  React.useEffect(() => {
    setLocalizedString(entry.label);
  }, [entry]);

  React.useEffect(() => {
    if (idValue !== entry.id) {
      const id = setTimeout(() => {
        onUpdateId(entry, idValue);
      }, 300);
      return () => clearTimeout(id);
    }
  }, [idValue]);

  React.useEffect(() => {
    if (rule !== entry.when) {
      if (rule === '' && entry.when === undefined) {
        return;
      }
      const id = setTimeout(() => {
        onRuleEdit(entry, rule);
      }, 300);
      return () => clearTimeout(id);
    }
  }, [rule]);

  React.useEffect(() => {
    if (localizedString && localizedString !== entry.label) {
      const id = setTimeout(() => {
        onTextEdit(entry, localizedString)
      }, 300);
      return () => clearTimeout(id);
    }
  }, [localizedString]);

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString({ ...localizedString, [language]: value });
  }

  return (
    <>
      <ChoiceDeleteDialog open={open} itemId={entry.id} onClick={() => onDelete(entry)} onClose={() => setOpen(false)} />
      <Table ref={provided.innerRef}
        {...provided.draggableProps}
        {...provided.dragHandleProps}>
        <TableBody>
          <TableRow key={entry.id} sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
            <TableCell align='center' width='15%'>
              <IconButton sx={{ p: 0.5 }} onClick={() => onToggleExpand(entry.id)}>{entryExpanded ? <KeyboardArrowUp /> : <KeyboardArrowDown />}</IconButton>
              <IconButton sx={{ p: 0.5 }} onClick={() => setOpen(true)}><Close color='error' /></IconButton>
              {!isGlobal && <IconButton onClick={() => onToggleExpand(entry.id)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>}
            </TableCell>
            <TableCell width='20%' sx={{ p: 0.5 }}>
              <Typography>{entry.id}</Typography>
            </TableCell>
            {formLanguages?.map(lang => (
              <TableCell key={lang} width={formLanguages ? `${65 / formLanguages.length}%` : 0} sx={{ p: 0.5 }}>
                {getLabel(entry, lang)}
              </TableCell>
            ))}
          </TableRow>
          {entryExpanded && <TableRow>
            <TableCell colSpan={2 + languageNo}>
              <Box sx={{ p: 1 }}>
                <Grid container spacing={1}>
                  <Grid item xs={4} sx={{ display: 'flex', flexDirection: 'column' }}>
                    <Typography color='text.hint' variant='caption'><FormattedMessage id='dialogs.options.key' /></Typography>
                    <TextField value={idValue} onChange={(e) => setIdValue(e.target.value)} />
                  </Grid>
                  {formLanguages?.map((language) => {
                    const localizedText = localizedString ? localizedString[language] : '';
                    return (
                      <Grid item xs={4} key={language} sx={{ display: 'flex', flexDirection: 'column' }}>
                        <Typography color='text.hint' variant='caption'>{getLanguageName(language)}</Typography>
                        <TextField value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />
                      </Grid>
                    );
                  })}
                </Grid>
                {!isGlobal && <Box sx={{ display: 'flex', flexDirection: 'column', mt: 1 }}>
                  <Typography color='text.hint' variant='caption'><FormattedMessage id='dialogs.options.rules.visibility' /></Typography>
                  <CodeMirror value={rule} onChange={(value) => setRule(value)} />
                </Box>}
              </Box>
            </TableCell>
          </TableRow>}
        </TableBody>
      </Table >
    </>
  );
}

export default ChoiceItem;
