import React from "react";
import { Box, IconButton, Table, TableBody, TableCell, TableRow, TextField, Typography, alpha, useTheme } from '@mui/material';
import { Close, KeyboardArrowDown, KeyboardArrowUp, Visibility } from "@mui/icons-material";
import { TreeItem } from "@atlaskit/tree";
import { TreeDraggableProvided } from "@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types";
import { LocalizedString, ValueSetEntry, useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import Editors from "./editors";
import { FormattedMessage } from "react-intl";
import { useErrorColor } from "../utils/ErrorUtils";
import { useEditor } from "../editor";
import CodeMirror from "./code/CodeMirror";


export interface ChoiceItemProps {
  item: TreeItem,
  valueSetId?: string,
  provided: TreeDraggableProvided,
  onRuleEdit: (entry: ValueSetEntry, rule: string) => void,
  onTextEdit: (entry: ValueSetEntry, label: LocalizedString) => void,
  onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void,
  isGlobal?: boolean
}

const MAX_CHOICE_LABEL_LENGTH = 40;

const getLabel = (entry: ValueSetEntry, language: string) => {
  const localizedLabel = entry.label[language] ? entry.label[language] : undefined;
  if (!localizedLabel) {
    return <></>;
  }
  if (localizedLabel.length > MAX_CHOICE_LABEL_LENGTH) {
    // eslint-disable-next-line formatjs/no-literal-string-in-jsx
    return <Typography>{localizedLabel.substring(0, MAX_CHOICE_LABEL_LENGTH) + '...'}</Typography>;
  }
  return <Typography>{localizedLabel}</Typography>;
}

const ChoiceItem: React.FC<ChoiceItemProps> = ({ item, valueSetId, provided, onRuleEdit, onTextEdit, onDelete, onUpdateId, isGlobal }) => {
  const { form } = useComposer();
  const { editor } = useEditor();
  const theme = useTheme();
  const entry = item.data.entry;
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const error = editor.errors.find(e => e.itemId === valueSetId && e.index == item.data.index);
  const errorColor = useErrorColor(error);
  const backgroundColor = errorColor || theme.palette.background.paper;
  const [open, setOpen] = React.useState(false);
  const [idValue, setIdValue] = React.useState<string>(entry.id);
  const [rule, setRule] = React.useState<string | undefined>(entry.when);
  const [expanded, setExpanded] = React.useState(false);

  React.useEffect(() => {
    if (idValue !== entry.id) {
      const id = setTimeout(() => {
        onUpdateId(entry, idValue);
      }, 1000);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idValue]);

  React.useEffect(() => {
    if (rule && rule !== entry.when) {
      const id = setTimeout(() => {
        onRuleEdit(entry, rule);
      }, 1000);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rule]);

  return (
    <>
      <ChoiceDeleteDialog open={open} itemId={entry.id} onClick={() => onDelete(entry)} onClose={() => setOpen(false)} />
      <Table ref={provided.innerRef}
        {...provided.draggableProps}
        {...provided.dragHandleProps}>
        <TableBody>
          <TableRow key={entry.id} sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
            <TableCell align='center' width='20%'>
              <IconButton onClick={() => setExpanded(!expanded)}>{expanded ? <KeyboardArrowUp /> : <KeyboardArrowDown />}</IconButton>
              <IconButton onClick={() => setOpen(true)}><Close color='error' /></IconButton>
              {!isGlobal && <IconButton onClick={() => setExpanded(true)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>}
            </TableCell>
            <TableCell width='30%' sx={{ p: 1 }}>
              <Typography>{idValue}</Typography>
            </TableCell>
            {formLanguages?.map(lang => (
              <TableCell key={lang} width={formLanguages ? `${50 / formLanguages.length}%` : 0} sx={{ p: 1 }}>
                {getLabel(entry, lang)}
              </TableCell>
            ))}
          </TableRow>
          {expanded && <TableRow>
            <TableCell colSpan={2 + languageNo}>
              <Box sx={{ p: 1 }}>
                <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                  <Typography color='text.hint' variant='caption'><FormattedMessage id='dialogs.options.key' /></Typography>
                  <TextField value={idValue} onChange={(e) => setIdValue(e.target.value)} />
                </Box>
                {!isGlobal && <Box sx={{ display: 'flex', flexDirection: 'column', mt: 1 }}>
                  <Typography color='text.hint' variant='caption'><FormattedMessage id='dialogs.options.rules.visibility' /></Typography>
                  <CodeMirror value={rule} onChange={(value) => setRule(value)} />
                </Box>}
                <Box sx={{ mt: 2 }}>
                  <Editors.ChoiceText entry={entry} onUpdate={onTextEdit} />
                </Box>
              </Box>
            </TableCell>
          </TableRow>}
        </TableBody>
      </Table >
    </>
  );
}

export default ChoiceItem;
