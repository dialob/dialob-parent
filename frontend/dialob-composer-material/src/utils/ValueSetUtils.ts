import { ValueSetEntry, TranslationMetadata } from '../types';

export const createDefaultValueSetEntry = (existingEntries?: ValueSetEntry[]): ValueSetEntry => {
  if (!existingEntries || existingEntries.length === 0) {
    return { id: 'choice1', label: {} };
  }
  return {
    id: 'choice' + (existingEntries.length + 1),
    label: {},
  };
};

const getValueSetEntryIdPart = (entryId: string, valueSetId: string): string | undefined => {
  const prefix = `v:${valueSetId}:`;
  if (!entryId.startsWith(prefix)) {
    return undefined;
  }
  const rest = entryId.slice(prefix.length);
  const colonIdx = rest.indexOf(':');
  if (colonIdx === -1) {
    return undefined;
  }
  return rest.slice(colonIdx + 1);
};

export const shiftValueSetTranslationIndices = (
  translations: TranslationMetadata[] | undefined,
  valueSetId: string,
  fromIndex: number,
  delta: number
): void => {
  if (!translations || delta === 0) {
    return;
  }
  const prefix = `v:${valueSetId}:`;
  translations.forEach(t => {
    if (t.entryId.startsWith(prefix)) {
      const entryIdPart = getValueSetEntryIdPart(t.entryId, valueSetId);
      if (entryIdPart === undefined) {
        return;
      }
      const entryIndex = parseInt(t.entryId.slice(prefix.length).split(':')[0], 10);
      if (entryIndex >= fromIndex) {
        t.entryId = `v:${valueSetId}:${entryIndex + delta}:${entryIdPart}`;
      }
    }
  });
};

export const syncValueSetTranslationIndices = (
  translations: TranslationMetadata[] | undefined,
  valueSetId: string,
  entries: ValueSetEntry[]
): void => {
  if (!translations) {
    return;
  }
  const prefix = `v:${valueSetId}:`;
  translations.forEach(t => {
    if (t.entryId.startsWith(prefix)) {
      const entryIdPart = getValueSetEntryIdPart(t.entryId, valueSetId);
      if (entryIdPart === undefined) {
        return;
      }
      const newIndex = entries.findIndex(e => e.id === entryIdPart);
      if (newIndex >= 0) {
        t.entryId = `v:${valueSetId}:${newIndex}:${entryIdPart}`;
      }
    }
  });
};
