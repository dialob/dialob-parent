import React from "react";
import { TableBody } from "@mui/material";
import { LocalizedString, ValueSet, ValueSetEntry } from "../types";
import { useSave } from "../dialogs/contexts/saving/useSave";
import { SortableFlatList } from "./SortableFlatList";
import { createDefaultValueSetEntry } from "../utils/ValueSetUtils";
import { arrayMove } from "@dnd-kit/sortable";
import { scrollToChoiceItem } from "../utils/ScrollUtils";
import { SortableChoiceItem } from "./SortableChoiceItem";


const ChoiceList: React.FC<{
  valueSet?: ValueSet,
  updateValueSet?: (value: React.SetStateAction<ValueSet | undefined>) => void,
  isGlobal?: boolean
}> = ({ valueSet, updateValueSet, isGlobal }) => {
  const { deleteValueSetEntry, updateValueSetEntry, addValueSetEntry, moveValueSetEntry } = useSave();
  const entries = valueSet?.entries ?? [];
  const itemIds = entries.map((_, index) => `${valueSet?.id}-${index}`);

  const updateValueSetEntryId = (index: number, id: string) => {
    if (valueSet?.entries?.[index]) {
      const newEntry = { ...valueSet.entries[index], id };
      updateValueSetEntry(valueSet.id, index, newEntry);
      updateValueSet?.({ ...valueSet, entries: valueSet.entries.map((e, i) => i === index ? newEntry : e) });
    }
  }

  const updateValueSetEntryLabel = (index: number, label: LocalizedString) => {
    if (valueSet?.entries?.[index]) {
      const newEntry = { ...valueSet.entries[index], label };
      updateValueSetEntry(valueSet.id, index, newEntry);
      updateValueSet?.({ ...valueSet, entries: valueSet.entries.map((e, i) => i === index ? newEntry : e) });
    }
  }

  const updateValueSetEntryRule = (index: number, rule: string) => {
    if (valueSet?.entries?.[index]) {
      const newEntry: ValueSetEntry = { ...valueSet.entries[index], when: rule };
      if (rule === '') {
        delete newEntry.when;
      }
      updateValueSetEntry(valueSet.id, index, newEntry);
      updateValueSet?.({ ...valueSet, entries: valueSet.entries.map((e, i) => i === index ? newEntry : e) });
    }
  }

  const onDeleteValueSetEntry = (index: number) => {
    if (valueSet?.entries?.[index]) {
      deleteValueSetEntry(valueSet.id, index);
      updateValueSet?.({ ...valueSet, entries: valueSet.entries.filter((_, i) => i !== index) });
    }
  }

  const handleInsertBelow = (index: number) => {
    if (!valueSet) {
      return;
    }
    const newEntry = createDefaultValueSetEntry(valueSet.entries);
    addValueSetEntry(valueSet.id, newEntry, index);
    const newEntries = valueSet.entries ? [...valueSet.entries] : [];
    newEntries.splice(index + 1, 0, newEntry);
    updateValueSet?.({ ...valueSet, entries: newEntries });
    scrollToChoiceItem(index + 1);
  };

  const handleReorder = (activeId: string, overId: string) => {
    if (!valueSet?.entries) {
      return;
    }
    const fromIndex = itemIds.indexOf(activeId);
    const toIndex = itemIds.indexOf(overId);
    if (fromIndex < 0 || toIndex < 0 || fromIndex === toIndex) {
      return;
    }
    moveValueSetEntry(valueSet.id, fromIndex, toIndex);
    updateValueSet?.({ ...valueSet, entries: arrayMove([...valueSet.entries], fromIndex, toIndex) });
  };

  if (!valueSet || entries.length === 0) {
    return <TableBody />;
  }

  return (
    <SortableFlatList itemIds={itemIds} onReorder={handleReorder}>
      <TableBody>
        {entries.map((entry, index) => (
          <SortableChoiceItem
            key={itemIds[index]}
            sortableId={itemIds[index]}
            entry={entry}
            index={index}
            valueSetId={valueSet.id}
            isGlobal={isGlobal}
            onRuleEdit={updateValueSetEntryRule}
            onTextEdit={updateValueSetEntryLabel}
            onDelete={onDeleteValueSetEntry}
            onUpdateId={updateValueSetEntryId}
            onInsertBelow={handleInsertBelow}
          />
        ))}
      </TableBody>
    </SortableFlatList>
  );
};

export default ChoiceList;
