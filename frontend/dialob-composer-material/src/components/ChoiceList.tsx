import React from "react";
import { TableBody, TableCell, TableRow } from "@mui/material";
import { useComposer } from "../dialob";
import ChoiceItem from "./ChoiceItem";
import { LocalizedString, ValueSet, ValueSetEntry } from "../types";
import { useSave } from "../dialogs/contexts/saving/useSave";


const ChoiceList: React.FC<{
  valueSet?: ValueSet,
  updateValueSet?: (value: React.SetStateAction<ValueSet | undefined>) => void,
  isGlobal?: boolean
}> = ({ valueSet, updateValueSet, isGlobal }) => {
  const { form } = useComposer();
  const { deleteValueSetEntry, updateValueSetEntry } = useSave();
  const languageNo = form.metadata.languages?.length || 0;

  const updateValueSetEntryId = (entry: ValueSetEntry, id: string) => {
    if (valueSet && valueSet.entries) {
      const newEntry = { ...entry, id };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet && updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const updateValueSetEntryLabel = (entry: ValueSetEntry, label: LocalizedString) => {
    if (valueSet && valueSet.entries) {
      const newEntry = { ...entry, label };
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(valueSet.id, idx, newEntry);
      updateValueSet && updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
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
      updateValueSet && updateValueSet({ ...valueSet, entries: valueSet.entries.map(e => e.id === entry.id ? newEntry : e) });
    }
  }

  const onDeleteValueSetEntry = (entry: ValueSetEntry) => {
    if (valueSet && valueSet.entries) {
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      deleteValueSetEntry(valueSet.id, idx);
      updateValueSet && updateValueSet({ ...valueSet, entries: valueSet.entries.filter(e => e.id !== entry.id) });
    }
  }

  const moveValueSetEntry = (entry: ValueSetEntry, direction: 'up' | 'down') => {
    if (valueSet && valueSet.entries) {
      const idx = valueSet.entries.findIndex(e => e.id === entry.id);
      if (idx < 0 || (direction === 'up' && idx === 0) || (direction === 'down' && idx === valueSet.entries.length - 1)) {
        return;
      }
      const newEntries = [...valueSet.entries];
      const swapIndex = direction === 'up' ? idx - 1 : idx + 1;
      newEntries[idx] = valueSet.entries[swapIndex];
      newEntries[swapIndex] = entry;
      updateValueSet && updateValueSet({ ...valueSet, entries: newEntries });
    }
  }

  return (
    <TableBody>
      <TableRow>
        <TableCell colSpan={2 + languageNo}>
          {valueSet?.entries && valueSet.entries?.length > 0 && valueSet.entries.map((entry, index) => (
            <ChoiceItem 
              key={entry.id}
              entry={entry}
              index={index}
              valueSetId={valueSet.id}
              isGlobal={isGlobal}
              onRuleEdit={updateValueSetEntryRule}
              onTextEdit={updateValueSetEntryLabel}
              onDelete={onDeleteValueSetEntry}
              onUpdateId={updateValueSetEntryId}
              onMove={moveValueSetEntry}
            />
          ))}
        </TableCell>
      </TableRow>
    </TableBody>
  );
};

export default ChoiceList;
