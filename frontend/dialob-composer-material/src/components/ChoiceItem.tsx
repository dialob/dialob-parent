import React from "react";
import { Box, IconButton, Table, TableBody, TableCell, TableRow, TextField, Tooltip, Typography, alpha, useTheme } from '@mui/material';
import { Check, Close, Visibility } from "@mui/icons-material";
import { TreeItem } from "@atlaskit/tree";
import { TreeDraggableProvided } from "@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types";
import { useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import { FormattedMessage } from "react-intl";
import { useErrorColor } from "../utils/ErrorUtils";
import { useEditor } from "../editor";
import CodeMirror from "./code/CodeMirror";
import { LocalizedString, ValueSetEntry } from "../types";


export interface ChoiceItemProps {
  item: TreeItem,
  valueSetId?: string,
  provided: TreeDraggableProvided,
  isGlobal?: boolean,
  onRuleEdit: (entry: ValueSetEntry, rule: string) => void,
  onTextEdit: (entry: ValueSetEntry, label: LocalizedString) => void,
  onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void,
}

const OverflowTooltipTextField: React.FC<React.ComponentProps<typeof TextField>> = ({ value, ...props }) => {
  const inputRef = React.useRef<HTMLInputElement>(null);
  const [isOverflowing, setIsOverflowing] = React.useState(false);

  React.useEffect(() => {
    const checkOverflow = () => {
      if (inputRef.current) {
        const el = inputRef.current;
        const overflow = el.scrollWidth > el.clientWidth;
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
  const { item, valueSetId, provided, isGlobal, onRuleEdit, onTextEdit, onDelete, onUpdateId } = props;
  const { form } = useComposer();
  const { editor } = useEditor();
  const theme = useTheme();
  const entry: ValueSetEntry = item.data.entry;
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const error = editor.errors?.find(e => e.itemId === valueSetId && e.index == item.data.index);
  const errorColor = useErrorColor(error);
  const backgroundColor = errorColor || theme.palette.background.paper;
  const [entryExpanded, setEntryExpanded] = React.useState(false);
  const [open, setOpen] = React.useState(false);
  const [idValue, setIdValue] = React.useState<string>(entry.id);
  const [rule, setRule] = React.useState<string>(entry.when || '');
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();
  const [editMode, setEditMode] = React.useState(false);

  React.useEffect(() => {
    setLocalizedString(entry.label);
  }, [entry]);

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

  const handleChangeName = () => {
    if (idValue !== entry.id) {
      onUpdateId(entry, idValue);
    }
    setEditMode(false);
  }

  const handleCloseChange = () => {
    setIdValue(entry.id);
    setEditMode(false);
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
              <IconButton sx={{ p: 0.5 }} onClick={() => setOpen(true)}><Close color='error' /></IconButton>
              {!isGlobal && <IconButton onClick={() => setEntryExpanded(!entryExpanded)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>}
            </TableCell>
            <TableCell width='20%' sx={{ p: 0.5 }}>
              <OverflowTooltipTextField value={idValue} onChange={(e) => setIdValue(e.target.value)} variant='standard' fullWidth
                onFocus={() => setEditMode(true)} InputProps={{
                disableUnderline: true,
                endAdornment: (
                  editMode && <>
                    <IconButton onClick={handleChangeName}><Check color='success' /></IconButton>
                    <IconButton onClick={handleCloseChange}><Close color='error' /></IconButton>
                  </>
                )
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
                <CodeMirror value={rule} onChange={(value) => setRule(value)} />
              </Box>}
            </TableCell>
          </TableRow>}
        </TableBody>
      </Table >
    </>
  );
}

export default ChoiceItem;
