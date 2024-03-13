import React from "react";
import Tree, { moveItemOnTree, TreeData, TreeSourcePosition, TreeDestinationPosition } from '@atlaskit/tree';
import { TableBody, TableCell, TableRow } from "@mui/material";
import { LocalizedString, ValueSet, ValueSetEntry, useComposer } from "../dialob";
import ChoiceItem, { ChoiceItemProps } from "./ChoiceItem";


const INIT_TREE: TreeData = {
  rootId: 'root',
  items: {},
};

const renderItem = (props: ChoiceItemProps) => {
  const { item, provided, onRuleEdit, onTextEdit, onDelete, onUpdateId, isGlobal } = props;
  return (
    <ChoiceItem item={item} provided={provided} onRuleEdit={onRuleEdit} onTextEdit={onTextEdit}
      onDelete={onDelete} onUpdateId={onUpdateId} isGlobal={isGlobal} />
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
  updateValueSet: (value: React.SetStateAction<ValueSet | undefined>) => void,
  isGlobal?: boolean
}> = ({ valueSet, updateValueSet, isGlobal }) => {
  const { form, moveValueSetEntry, deleteValueSetEntry, updateValueSetEntry } = useComposer();
  const [tree, setTree] = React.useState<TreeData>(INIT_TREE);
  const languageNo = form.metadata.languages?.length || 0;

  React.useEffect(() => {
    setTree(buildTreeFromValueSet(valueSet));
  }, [valueSet]);

  const updateValueSetEntryId = (entry: ValueSetEntry, id: string) => {
    if (valueSet) {
      const newEntry = { ...entry, id };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const updateValueSetEntryLabel = (entry: ValueSetEntry, label: LocalizedString) => {
    if (valueSet) {
      const newEntry = { ...entry, label };
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
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
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
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);
    moveValueSetEntry(valueSet.id, source.index, destination.index!);
  };

  return (
    <TableBody>
      <TableRow>
        <TableCell colSpan={2 + languageNo}>
          <Tree
            tree={tree}
            renderItem={(props) => renderItem({
              ...props, onRuleEdit: updateValueSetEntryRule, onTextEdit: updateValueSetEntryLabel,
              onDelete: onDeleteValueSetEntry, onUpdateId: updateValueSetEntryId, isGlobal: isGlobal
            })}
            onDragEnd={onDragEnd}
            isDragEnabled
          />
        </TableCell>
      </TableRow>
    </TableBody>
  );
};

export default ChoiceList;
