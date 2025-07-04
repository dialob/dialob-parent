import React from "react";
import { Box, IconButton, Table, TableBody, TableCell, TableRow, TextField, Tooltip, Typography, alpha, useTheme } from '@mui/material';
import { ArrowDownward, ArrowUpward, Close, Visibility } from "@mui/icons-material";
import { useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import { FormattedMessage } from "react-intl";
import { useErrorColor } from "../utils/ErrorUtils";
import { useEditor } from "../editor";
import CodeMirror from "./code/CodeMirror";
import { LocalizedString, ValueSetEntry } from "../types";
import { useSave } from "../dialogs/contexts/saving/useSave";
import { on } from "events";


export interface ChoiceItemProps {
  entry: ValueSetEntry,
  index: number,
  valueSetId?: string,
  isGlobal?: boolean,
  onRuleEdit: (entry: ValueSetEntry, rule: string) => void,
  onTextEdit: (entry: ValueSetEntry, label: LocalizedString) => void,
  onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void,
  onMove?: (entry: ValueSetEntry, direction: 'up' | 'down') => void
}

const OverflowTooltipTextField: React.FC<React.ComponentProps<typeof TextField>> = ({ value, ...props }) => {
  const inputRef = React.useRef<HTMLInputElement>(null);
  const [isOverflowing, setIsOverflowing] = React.useState(false);

  React.useEffect(() => {
    const checkOverflow = () => {
      if (inputRef.current) {
        const el = inputRef.current;
        const overflow = el.scrollWidth > (el.clientWidth + 1);
        setIsOverflowing(overflow);
      }
    };

    checkOverflow();

    const resizeObserver = new ResizeObserver(() => {
      checkOverflow();
    });

    if (inputRef.current) {
      resizeObserver.observe(inputRef.current);
    }

    return () => resizeObserver.disconnect();
  }, [value]);

  const textField = (
    <TextField
      {...props}
      inputRef={inputRef}
      value={value}
    />
  );

  return isOverflowing ? (
    <Tooltip title={value + ''} placement='top' arrow>
      {textField}
    </Tooltip>
  ) : (
    textField
  );
};

const ChoiceItem: React.FC<ChoiceItemProps> = (props) => {
  const { entry, index, valueSetId, isGlobal, onRuleEdit, onTextEdit, onDelete, onUpdateId, onMove } = props;
  const { form } = useComposer();
  const { editor } = useEditor();
  const { savingState, moveValueSetEntry } = useSave();
  const theme = useTheme();
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const error = editor.errors?.find(e => e.itemId === valueSetId && e.index == index);
  const errorColor = useErrorColor(error);
  const backgroundColor = errorColor || theme.palette.background.paper;
  const [entryExpanded, setEntryExpanded] = React.useState(false);
  const [open, setOpen] = React.useState(false);
  const localizedString = entry.label;
  const inputRef = React.useRef<HTMLInputElement>(null);
  const length = savingState.valueSets?.find(v => v.id === valueSetId)?.entries?.length || 0;

  const handleUpdate = (value: string, language: string) => {
    const updatedLocalizedString = { ...localizedString, [language]: value };
    onTextEdit(entry, updatedLocalizedString);
  }

  const handleSaveRule = (value: string) => {
    if (value !== entry.when) {
      onRuleEdit(entry, value);
    }
  }

  const handleChangeId = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const newId = e.target.value.trim();
    if (newId !== entry.id) {
      onUpdateId(entry, newId);
    }
  }

  const handleMove = (direction: 'up' | 'down') => {
    if (valueSetId) {
      const newIndex = direction === 'up' ? index - 1 : index + 1;
      moveValueSetEntry(valueSetId, index, newIndex);
      onMove && onMove(entry, direction);
    }
  }

  React.useEffect(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  }, [entry.id]);

  return (
    <>
      <ChoiceDeleteDialog open={open} itemId={entry.id} onClick={() => onDelete(entry)} onClose={() => setOpen(false)} />
      <Table>
        <TableBody>
          <TableRow sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
            <TableCell align='center' width='15%'>
              <IconButton sx={{ p: 0.5 }} onClick={() => setOpen(true)}><Close color='error' /></IconButton>
              {!isGlobal && <IconButton onClick={() => setEntryExpanded(!entryExpanded)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>}
              <IconButton sx={{ p: 0.5 }} onClick={() => handleMove('up')} disabled={index === 0}>
                <ArrowUpward />
              </IconButton>
              <IconButton sx={{ p: 0.5 }} onClick={() => handleMove('down')} disabled={index === length - 1}>
                <ArrowDownward />
              </IconButton>
            </TableCell>
            <TableCell width='20%' sx={{ p: 0.5 }}>
              <TextField value={entry.id} onChange={(e) => handleChangeId(e)} 
                variant='standard' fullWidth inputRef={inputRef}
                InputProps={{
                  disableUnderline: true
                }} />
            </TableCell>
            {formLanguages?.map(lang => (
              <TableCell key={lang} width={formLanguages ? `${65 / formLanguages.length}%` : 0} sx={{ p: 0.5 }}>
                <OverflowTooltipTextField value={localizedString ? localizedString[lang] : ''} variant="standard" fullWidth InputProps={{ disableUnderline: true }} onChange={(e) => handleUpdate(e.target.value, lang)} />
              </TableCell>
            ))}
          </TableRow>
          {entryExpanded && !isGlobal && <TableRow>
            <TableCell colSpan={2 + languageNo}>
              {!isGlobal && <Box sx={{ display: 'flex', flexDirection: 'column', p: 1 }}>
                <Typography color='text.hint' variant='caption'><FormattedMessage id='dialogs.options.rules.visibility' /></Typography>
                <CodeMirror value={entry.when ?? ''} onChange={(value) => handleSaveRule(value)} />
              </Box>}
            </TableCell>
          </TableRow>}
        </TableBody>
      </Table >
    </>
  );
}

export default ChoiceItem;
