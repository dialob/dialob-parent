import React from "react";
import { Box, IconButton, Table, TableBody, TableCell, TableRow, TextField, Typography } from '@mui/material';
import { Close, KeyboardArrowDown, KeyboardArrowUp, Visibility } from "@mui/icons-material";
import { TreeItem } from "@atlaskit/tree";
import { TreeDraggableProvided } from "@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types";
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { LocalizedString, ValueSetEntry, useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import ChoiceTextEditor from "./editors/ChoiceTextEditor";


export interface ChoiceItemProps {
  item: TreeItem,
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
    return <Typography>{localizedLabel.substring(0, MAX_CHOICE_LABEL_LENGTH) + '...'}</Typography>;
  }
  return <Typography>{localizedLabel}</Typography>;
}

const ChoiceItem: React.FC<ChoiceItemProps> = ({ item, provided, onRuleEdit, onTextEdit, onDelete, onUpdateId, isGlobal }) => {
  const { form } = useComposer();
  const entry = item.data;
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const [open, setOpen] = React.useState(false);
  const [idValue, setIdValue] = React.useState<string>(entry.id);
  const [rule, setRule] = React.useState<string | undefined>(entry.when);
  const [expanded, setExpanded] = React.useState(false);

  React.useEffect(() => {
    const id = setTimeout(() => {
      onUpdateId(entry, idValue);
    }, 1000);
    return () => clearTimeout(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idValue]);

  React.useEffect(() => {
    if (rule) {
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
          <TableRow key={entry.id}>
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
                  <Typography color='text.hint' variant='caption'>Key</Typography>
                  <TextField value={idValue} onChange={(e) => setIdValue(e.target.value)} />
                </Box>
                {!isGlobal && <Box sx={{ display: 'flex', flexDirection: 'column', mt: 1 }}>
                  <Typography color='text.hint' variant='caption'>Visbility rule</Typography>
                  <CodeMirror value={rule || ''} onChange={(value) => setRule(value)} extensions={[javascript({ jsx: true })]} />
                </Box>}
                <Box sx={{ mt: 2 }}>
                  <ChoiceTextEditor entry={entry} onUpdate={onTextEdit} />
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
