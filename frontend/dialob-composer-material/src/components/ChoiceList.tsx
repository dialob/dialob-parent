import React from "react";
import Tree, { moveItemOnTree, TreeData, TreeSourcePosition, TreeDestinationPosition, RenderItemParams } from '@atlaskit/tree';
import { TableBody, TableCell, TableRow, useTheme } from "@mui/material";
import { ValueSet, ValueSetEntry, useComposer } from "../dialob";
import ChoiceItem from "./ChoiceItem";
import ChoiceRuleEditDialog from "../dialogs/ChoiceRuleEditDialog";
import ChoiceTextEditDialog from "../dialogs/ChoiceTextEditDialog";


const INIT_TREE: TreeData = {
  rootId: 'root',
  items: {},
};

const renderItem = ({ item, provided }: RenderItemParams, onRuleEdit: (entry: ValueSetEntry) => void,
  onTextEdit: (entry: ValueSetEntry) => void, onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void) => {
  return (
    <ChoiceItem item={item} provided={provided} onRuleEdit={onRuleEdit} onTextEdit={onTextEdit}
      onDelete={onDelete} onUpdateId={onUpdateId} />
  );
}

const buildTreeFromValueSet = (valueSet?: ValueSet): TreeData => {
  if (!valueSet) {
    return { rootId: 'root', items: {} };
  }
  const items = valueSet.entries.map((entry, idx) => ({
    id: entry.id,
    children: [],
    data: entry,
  }));
  return {
    rootId: 'root',
    items: {
      root: { id: 'root', children: items.map(i => i.id), data: undefined },
      ...Object.fromEntries(items.map(i => [i.id, i]))
    }
  };
}

const ChoiceList: React.FC<{
  valueSet?: ValueSet,
  updateValueSet: (value: React.SetStateAction<ValueSet | undefined>) => void
}> = ({ valueSet, updateValueSet }) => {
  const { moveValueSetEntry, deleteValueSetEntry, updateValueSetEntry } = useComposer();
  const theme = useTheme();
  const [tree, setTree] = React.useState<TreeData>(INIT_TREE);
  const [activeValueSetEntry, setActiveValueSetEntry] = React.useState<ValueSetEntry | undefined>(undefined);
  const [activeDialog, setActiveDialog] = React.useState<'rule' | 'text' | undefined>(undefined);

  React.useEffect(() => {
    setTree(buildTreeFromValueSet(valueSet));
  }, [valueSet]);

  const handleEditRule = (entry: ValueSetEntry) => {
    setActiveValueSetEntry(entry);
    setActiveDialog('rule');
  }

  const handleEditText = (entry: ValueSetEntry) => {
    setActiveValueSetEntry(entry);
    setActiveDialog('text');
  }

  const handleCloseChoiceDialog = () => {
    setActiveValueSetEntry(undefined);
    setActiveDialog(undefined);
  }

  const updateValueSetEntryId = (entry: ValueSetEntry, id: string) => {
    if (valueSet) {
      const newEntry = { ...entry, id };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const updateValueSetEntryLabel = (entry: ValueSetEntry, label: string, language: string) => {
    if (valueSet) {
      const newEntry = { ...entry, label: { ...entry.label, [language]: label } };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const updateValueSetEntryRule = (entry: ValueSetEntry, rule: string) => {
    if (valueSet) {
      const newEntry = { ...entry, when: rule };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const onDeleteValueSetEntry = (entry: ValueSetEntry) => {
    if (valueSet) {
      const idx = valueSet.entries.findIndex(e => e.id !== entry.id);
      deleteValueSetEntry(valueSet.id, idx);
      updateValueSet({ ...valueSet, entries: valueSet.entries.filter(e => e.id !== entry.id) });
    }
  }

  const onDragEnd = (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination || !valueSet) {
      return;
    }
    moveItemOnTree(tree, source, destination);
    moveValueSetEntry(valueSet.id, source.index, destination.index!);
  };

  return (
    <>
      <ChoiceRuleEditDialog open={activeDialog === 'rule'} valueSetEntry={activeValueSetEntry}
        onUpdate={updateValueSetEntryRule} onClose={handleCloseChoiceDialog} />
      <ChoiceTextEditDialog open={activeDialog === 'text'} valueSetEntry={activeValueSetEntry}
        onUpdate={updateValueSetEntryLabel} onClose={handleCloseChoiceDialog} />
      <TableBody>
        <TableRow>
          <TableCell colSpan={3}>
            <Tree
              tree={tree}
              renderItem={(props) => renderItem(props, handleEditRule, handleEditText, onDeleteValueSetEntry, updateValueSetEntryId)}
              onDragEnd={onDragEnd}
              isDragEnabled
            />
          </TableCell>
        </TableRow>
      </TableBody>
    </>
  );
};

export default ChoiceList;
