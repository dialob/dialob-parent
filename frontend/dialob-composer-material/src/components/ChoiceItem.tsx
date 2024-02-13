import React from "react";
import { Button, IconButton, Table, TableBody, TableCell, TableRow, Typography, styled } from '@mui/material';
import { TreeItem } from "@atlaskit/tree";
import { TreeDraggableProvided } from "@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types";
import { StyledTextField } from "./TableEditorComponents";
import { useEditor } from "../editor";
import { ValueSetEntry } from "../dialob";
import { Close, Visibility } from "@mui/icons-material";
import ChoiceConfirmationDialog from "../dialogs/ConvertConfirmationDialog";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";


interface ChoiceItemProps {
  item: TreeItem,
  provided: TreeDraggableProvided,
  onRuleEdit: (entry: ValueSetEntry) => void,
  onTextEdit: (entry: ValueSetEntry) => void,
  onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void,
}

const MAX_CHOICE_LABEL_LENGTH = 40;

const LabelButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1.5),
  width: '100%',
  justifyContent: 'flex-start',
  textTransform: 'none',
}));

const getLabel = (entry: ValueSetEntry, language: string) => {
  const localizedLabel = entry.label[language] ? entry.label[language] : undefined;
  if (!localizedLabel) {
    return <Typography color='text.hint'>Label</Typography>;
  }
  if (localizedLabel.length > MAX_CHOICE_LABEL_LENGTH) {
    return <Typography>{localizedLabel.substring(0, MAX_CHOICE_LABEL_LENGTH) + '...'}</Typography>;
  }
  return <Typography>{localizedLabel}</Typography>;
}

const ChoiceItem: React.FC<ChoiceItemProps> = ({ item, provided, onRuleEdit, onTextEdit, onDelete, onUpdateId }) => {
  const { editor } = useEditor();
  const entry = item.data;
  const [open, setOpen] = React.useState(false);

  return (
    <>
      <ChoiceDeleteDialog open={open} itemId={entry.id} onClick={() => onDelete(entry)} onClose={() => setOpen(false)} />
      <Table>
        <TableBody>
          <TableRow key={entry.id} ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}>
            <TableCell align='center' width='5%'>
              <IconButton onClick={() => setOpen(true)}><Close color='error' /></IconButton>
              <IconButton onClick={() => onRuleEdit(entry)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>
            </TableCell>
            <TableCell width='10%'>
              <StyledTextField variant='standard' InputProps={{
                disableUnderline: true,
              }} value={entry.id} onChange={(e) => onUpdateId(entry, e.target.value)} />
            </TableCell>
            <TableCell width='10%'>
              <LabelButton variant='text' color='inherit' onClick={() => onTextEdit(entry)}>
                {getLabel(entry, editor.activeFormLanguage)}
              </LabelButton>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </>
  );
}

export default ChoiceItem;
