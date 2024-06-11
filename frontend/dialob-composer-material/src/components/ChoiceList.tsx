import React, { useCallback } from "react";
import Tree, { moveItemOnTree, TreeData, TreeSourcePosition, TreeDestinationPosition } from '@atlaskit/tree';
import { TableBody, TableCell, TableRow } from "@mui/material";
import { LocalizedString, ValueSet, ValueSetEntry, useComposer } from "../dialob";
import ChoiceItem, { ChoiceItemProps } from "./ChoiceItem";
import { INIT_TREE, buildTreeFromValueSet } from "../utils/TreeUtils";


const renderItem = (props: ChoiceItemProps) => {
  const { item, valueSetId, provided, isGlobal, expanded, onToggleExpand, onRuleEdit, onTextEdit, onDelete, onUpdateId } = props;
  return (
    <ChoiceItem item={item} valueSetId={valueSetId} provided={provided} isGlobal={isGlobal} expanded={expanded}
      onToggleExpand={onToggleExpand} onRuleEdit={onRuleEdit} onTextEdit={onTextEdit}
      onDelete={onDelete} onUpdateId={onUpdateId} />
  );
}

const ChoiceList: React.FC<{
  valueSet?: ValueSet,
  updateValueSet: (value: React.SetStateAction<ValueSet | undefined>) => void,
  isGlobal?: boolean
}> = ({ valueSet, updateValueSet, isGlobal }) => {
  const { form, moveValueSetEntry, deleteValueSetEntry, updateValueSetEntry } = useComposer();
  const [tree, setTree] = React.useState<TreeData>(INIT_TREE);
  const [expanded, setExpanded] = React.useState<string[]>([]);
  const languageNo = form.metadata.languages?.length || 0;

  React.useEffect(() => {
    setTree(buildTreeFromValueSet(valueSet));
  }, [valueSet]);

  const updateValueSetEntryId = (entry: ValueSetEntry, id: string) => {
    if (valueSet && valueSet.entries) {
      const newEntry = { ...entry, id };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
      setExpanded(prevExpanded => {
        const idx = prevExpanded.indexOf(entry.id);
        if (idx > -1) {
          const newExpanded = [...prevExpanded];
          newExpanded[idx] = id;
          return newExpanded;
        }
        return prevExpanded;
      })
    }
  }

  const updateValueSetEntryLabel = (entry: ValueSetEntry, label: LocalizedString) => {
    if (valueSet && valueSet.entries) {
      const newEntry = { ...entry, label };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const updateValueSetEntryRule = (entry: ValueSetEntry, rule: string) => {
    if (valueSet && valueSet.entries) {
      const newEntry: ValueSetEntry = { ...entry, when: rule };
      if (rule === '') {
        delete newEntry.when;
      }
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const onDeleteValueSetEntry = (entry: ValueSetEntry) => {
    if (valueSet && valueSet.entries) {
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      deleteValueSetEntry(valueSet.id, idx);
      updateValueSet({ ...valueSet, entries: valueSet.entries.filter(e => e.id !== entry.id) });
    }
  }

  const toggleExpand = (id: string) => {
    if (expanded.includes(id)) {
      setExpanded(prevExpanded => prevExpanded.filter(exp => exp !== id));
    } else {
      setExpanded(prevExpanded => [...prevExpanded, id]);
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
          {valueSet?.entries && valueSet.entries?.length > 0 && <Tree
            tree={tree}
            renderItem={(props) => renderItem({
              ...props, valueSetId: valueSet?.id, isGlobal: isGlobal, expanded: expanded, onToggleExpand: toggleExpand,
              onRuleEdit: updateValueSetEntryRule, onTextEdit: updateValueSetEntryLabel,
              onDelete: onDeleteValueSetEntry, onUpdateId: updateValueSetEntryId
            })}
            onDragEnd={onDragEnd}
            isDragEnabled
          />}
        </TableCell>
      </TableRow>
    </TableBody>
  );
};

export default ChoiceList;
