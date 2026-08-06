import Papa from "papaparse";
import { ComposerState, DialobItem, LocalizedString, TranslationMetadata, ValueSet } from "../types";
import FileSaver from "file-saver";
import { ISO_LANGUAGES, MOST_USED_LANGUAGES } from "../defaults";

export type TranslationType = 'label' | 'description' | 'valueset' | 'validation';

interface TranslationData {
  [key: string]: LocalizedString | undefined | string;
}

interface MetadataEntry {
  description: string;
  richText?: boolean;
  pageId: string;
  parent: string;
}

interface Metadata {
  key: { [key: string]: MetadataEntry };
}

interface GlobalValueSet {
  label?: string;
  valueSetId: string;
}

interface ItemTranslations {
  translations: TranslationData;
  metadata: Metadata;
}

export interface ParsedImportData {
  missingInCsv: string[];
  missingInForm: string[];
}

export interface MissingTranslation {
  id: string;
  missingIn: string[];
  index?: number;
  global?: boolean;
}

export type MissingTranslations = {
  [type in TranslationType]?: MissingTranslation[];
}

export interface AITranslation {
  id: string;
  languages: string[];
  index?: number;
  global?: boolean;
}

export type AITranslations = {
  [type in TranslationType]?: AITranslation[];
}

export interface ParsedItemId {
  type: 'item';
  itemId: string;
  subType: 'label' | 'description' | 'validation';
  validationIndex?: number;
}

export interface ParsedValueSetId {
  type: 'valueset';
  valueSetId: string;
  entryIndex: number;
  entryId: string;
}

export type ParsedEntryId = ParsedItemId | ParsedValueSetId;



export function findValueset(data: ComposerState, id: string) {
  if (!data || !data.valueSets || !id) {
    return undefined;
  }
  return data.valueSets.find(v => v.id === id);
}

export function isGlobalValueSet(globalValueSets: GlobalValueSet[] | undefined, id: string) {
  const gvsIndex = globalValueSets ? globalValueSets.findIndex((vs: { valueSetId: string; }) => vs.valueSetId === id) : -1;
  return gvsIndex > -1;
}

export const getMissingTranslations = (form: ComposerState): MissingTranslations | undefined => {
  const missing: MissingTranslations = {
    label: [],
    description: [],
    valueset: [],
    validation: []
  };

  const languages = form.metadata.languages || [];
  if (languages.length === 0) {
    return undefined;
  }

  // iterate through items
  Object.values(form.data).forEach(item => {
    if (item.id === 'questionnaire') {
      return;
    }
    // iterate through languages
    languages.forEach(lang => {
      // check item labels
      if (item.label === undefined || (item.label[lang] === undefined || item.label[lang] === '')) {
        const entryId = buildItemLabelId(item.id);
        if (!missing.label?.find(m => m.id === entryId)) {
          missing.label?.push({ id: entryId, missingIn: [lang] });
        } else {
          missing.label?.find(m => m.id === entryId)!.missingIn.push(lang);
        }
      }
      // check item descriptions
      if (item.description && (item.description[lang] === undefined || item.description[lang] === '')) {
        const entryId = buildItemDescriptionId(item.id);
        if (!missing.description?.find(m => m.id === entryId)) {
          missing.description?.push({ id: entryId, missingIn: [lang] });
        } else {
          missing.description?.find(m => m.id === entryId)!.missingIn.push(lang);
        }
      }
      // check item validations
      if (item.validations) {
        // iterate validations and check messages
        item.validations.forEach((v, idx) => {
          if (v.message && (v.message[lang] === undefined || v.message[lang] === '')) {
            const entryId = buildValidationId(item.id, idx);
            if (!missing.validation?.find(m => m.id === entryId)) {
              missing.validation?.push({ id: entryId, missingIn: [lang], index: idx });
            } else {
              missing.validation?.find(m => m.id === entryId)!.missingIn.push(lang);
            }
          }
        })
      }
    });
  });

  // iterate through valuesets
  form.valueSets?.forEach(vs => {
    vs.entries?.forEach((vse, idx) => {
      languages.forEach(lang => {
        if (vse.label === undefined || (vse.label[lang] === undefined || vse.label[lang] === '')) {
          const entryId = buildValueSetEntryId(vs.id, idx, vse.id);
          const existingEntry = missing.valueset?.find(m => m.id === entryId);
          if (!existingEntry) {
            missing.valueset?.push({ id: entryId, missingIn: [lang], index: idx });
          } else {
            existingEntry.missingIn.push(lang);
          }
        }
      })
    })
  });

  // Determine if valuesets are global
  const processedValueSets = new Set<string>();
  
  // Mark referenced valuesets (local or global)
  Object.values(form.data).forEach(item => {
    if (item.valueSetId && !processedValueSets.has(item.valueSetId)) {
      processedValueSets.add(item.valueSetId);
      const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === item.valueSetId);
      missing.valueset?.forEach(m => {
        const parsed = parseEntryId(m.id);
        if (parsed && parsed.type === 'valueset' && parsed.valueSetId === item.valueSetId) {
          m.global = !!gvs;
        }
      });
    }
  });
  
  // Mark unreferenced valuesets as global if they're in globalValueSets
  form.valueSets?.forEach(vs => {
    if (!processedValueSets.has(vs.id)) {
      const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === vs.id);
      missing.valueset?.forEach(m => {
        const parsed = parseEntryId(m.id);
        if (parsed && parsed.type === 'valueset' && parsed.valueSetId === vs.id && m.global === undefined) {
          m.global = !!gvs;
        }
      });
    }
  });
  
  missing.valueset?.sort((a, b) => a.global === b.global ? 0 : a.global ? -1 : 1);

  // check if values are empty
  if (missing.label?.length === 0) {
    delete missing.label;
  }
  if (missing.description?.length === 0) {
    delete missing.description;
  }
  if (missing.valueset?.length === 0) {
    delete missing.valueset;
  }
  if (missing.validation?.length === 0) {
    delete missing.validation;
  }

  if (Object.keys(missing).length === 0) {
    return undefined;
  }

  return missing;
}

export const getAITranslations = (form: ComposerState): AITranslations | undefined => {
  const aiTranslated: AITranslations = {
    label: [],
    description: [],
    valueset: [],
    validation: []
  };

  const aiTranslations = form.metadata.composer?.aiTranslations || [];
  if (aiTranslations.length === 0) {
    return undefined;
  }

  // Group AI translations by entry ID (normalize valueset entries to ignore stored index)
  const translationsByEntry: Map<string, Set<string>> = new Map();
  aiTranslations.forEach(t => {
    let key = t.entryId;
    
    // For valueset entries, normalize the key to ignore the stored index
    // e.g., v:vs1:0:choice1 and v:vs1:1:choice1 should map to the same key
    if (t.entryId.startsWith('v:')) {
      const parts = t.entryId.split(':');
      if (parts.length >= 4) {
        // Normalize to v:valueSetId:entryId
        key = `v:${parts[1]}:${parts[3]}`;
      }
    }
    
    if (!translationsByEntry.has(key)) {
      translationsByEntry.set(key, new Set());
    }
    translationsByEntry.get(key)!.add(t.targetLanguage);
  });

  // Process each entry ID
  translationsByEntry.forEach((languages, entryId) => {
    const parts = entryId.split(':');
    
    if (parts[0] === 'i') {
      // Item translation: i:itemId:type or i:itemId:v:index
      const itemId = parts[1];
      const item = form.data[itemId];
      if (!item) return;

      if (parts[2] === 'l') {
        // Label
        const fullEntryId = buildItemLabelId(itemId);
        aiTranslated.label?.push({ 
          id: fullEntryId, 
          languages: Array.from(languages)
        });
      } else if (parts[2] === 'd') {
        // Description
        const fullEntryId = buildItemDescriptionId(itemId);
        aiTranslated.description?.push({ 
          id: fullEntryId, 
          languages: Array.from(languages)
        });
      } else if (parts[2] === 'v') {
        // Validation message
        const validationIndex = parseInt(parts[3], 10);
        const fullEntryId = buildValidationId(itemId, validationIndex);
        aiTranslated.validation?.push({ 
          id: fullEntryId,
          languages: Array.from(languages),
          index: validationIndex
        });
      }
    } else if (parts[0] === 'v') {
      // ValueSet translation: normalized format v:valueSetId:entryId
      const valueSetId = parts[1];
      const entryIdPart = parts[2];
      const valueSet = form.valueSets?.find(vs => vs.id === valueSetId);
      
      // Find the current index of the entry by its ID
      const currentIndex = valueSet?.entries?.findIndex(e => e.id === entryIdPart);
      
      if (currentIndex !== undefined && currentIndex >= 0) {
        const fullEntryId = buildValueSetEntryId(valueSetId, currentIndex, entryIdPart);
        aiTranslated.valueset?.push({ 
          id: fullEntryId,
          languages: Array.from(languages),
          index: currentIndex
        });
      }
    }
  });

  // Determine if valuesets are global
  const processedValueSets = new Set<string>();
  
  // Mark referenced valuesets (local or global)
  Object.values(form.data).forEach(item => {
    if (item.valueSetId && !processedValueSets.has(item.valueSetId)) {
      processedValueSets.add(item.valueSetId);
      const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === item.valueSetId);
      aiTranslated.valueset?.forEach(ai => {
        const parsed = parseEntryId(ai.id);
        if (parsed && parsed.type === 'valueset' && parsed.valueSetId === item.valueSetId) {
          ai.global = !!gvs;
        }
      });
    }
  });
  
  // Mark unreferenced valuesets as global if they're in globalValueSets
  form.valueSets?.forEach(vs => {
    if (!processedValueSets.has(vs.id)) {
      const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === vs.id);
      aiTranslated.valueset?.forEach(ai => {
        const parsed = parseEntryId(ai.id);
        if (parsed && parsed.type === 'valueset' && parsed.valueSetId === vs.id && ai.global === undefined) {
          ai.global = !!gvs;
        }
      });
    }
  });
  
  aiTranslated.valueset?.sort((a, b) => a.global === b.global ? 0 : a.global ? -1 : 1);

  // Remove empty categories
  if (aiTranslated.label?.length === 0) {
    delete aiTranslated.label;
  }
  if (aiTranslated.description?.length === 0) {
    delete aiTranslated.description;
  }
  if (aiTranslated.valueset?.length === 0) {
    delete aiTranslated.valueset;
  }
  if (aiTranslated.validation?.length === 0) {
    delete aiTranslated.validation;
  }

  if (Object.keys(aiTranslated).length === 0) {
    return undefined;
  }

  return aiTranslated;
}

export const getAllItemTranslations = (form: ComposerState): ItemTranslations => {
  const translations: TranslationData = { key: {} };
  const metadata: Metadata = { key: {} };
  const formItems = form.data;
  const globalValueSets: GlobalValueSet[] | undefined = form.metadata.composer?.globalValueSets;

  function visitItem(item: DialobItem, pageId: string, parent: DialobItem) {
    const key = `i:${item.id}:l`;
    translations[key] = item.label || "";
    metadata.key[key] = { description: 'Item label', richText: item.type === 'note', pageId: pageId, parent: `${parent.id} ${parent.type}` };
    if (item.description) {
      const key = `i:${item.id}:d`;
      translations[key] = item.description;
      metadata.key[key] = { description: 'Item description', richText: true, pageId: pageId, parent: `${parent.id} ${parent.type}` };
    }
    if (item.validations) {
      item.validations.forEach((val, idx) => {
        const key = `i:${item.id}:v:${idx}`;
        translations[key] = val.message;
        metadata.key[key] = { description: 'Validation', pageId: pageId, parent: `${parent.id} ${parent.type}` };
        return true;
      });
    }

    if (item.valueSetId) {
      let valueSet: ValueSet | undefined;
      if (findValueset(form, item.valueSetId)) {
        valueSet = findValueset(form, item.valueSetId);
      }
      if (valueSet && valueSet.entries) {
        if (!isGlobalValueSet(globalValueSets, item.valueSetId)) {
          valueSet.entries?.forEach((entry, index) => {
            const key = `v:${valueSet!.id}:${index}:${entry.id}`;
            translations[key] = entry.label;
            metadata.key[key] = { description: 'Valueset entry', pageId: pageId, parent: `${parent.id} ${parent.type}` };
          })
        }
      }
    }

    if (item.items instanceof Array) {
      item.items.forEach((childId: string) => {
        const child: DialobItem = formItems[childId];
        visitItem(child, pageId, item);
      });
    }
  }

  const pageIds: string[] | undefined = formItems["questionnaire"].items;
  const pages: DialobItem[] = []
  pageIds?.forEach((pageId) => {
    pages.push(formItems[pageId])
  })
  pages.forEach((page) => {
    visitItem(page, page.id, formItems["questionnaire"])
  })

  return { translations, metadata };
}

export const getGlobalValueSetTranslations = (form: ComposerState): ItemTranslations | undefined => {
  const globalValueSets: GlobalValueSet[] | undefined = form.metadata.composer?.globalValueSets;
  if (globalValueSets && globalValueSets?.length > 0) {
    const translations: TranslationData = { key: {} };
    const metadata: Metadata = { key: {} };

    globalValueSets.forEach((globalValueSet) => {
      let valueSet: ValueSet | undefined;
      if (findValueset(form, globalValueSet.valueSetId)) {
        valueSet = findValueset(form, globalValueSet.valueSetId);
      }
      if (valueSet && valueSet.entries) {
        valueSet.entries?.forEach((entry, index) => {
          const key = `v:${valueSet!.id}:${index}:${entry.id}`;
          translations[key] = entry.label;
          metadata.key[key] = { description: 'Valueset entry', pageId: "Root", parent: "Global list" };
        })
      }
    })
    return { translations, metadata }
  } else {
    return undefined;
  }
}

export const parse = (inputFile: File) => {
  return new Promise((resolve, reject) => {
    Papa.parse(inputFile, {
      header: false,
      transformHeader: h => h.trim(),
      skipEmptyLines: true,
      error: (error) => {
        console.error('CSV Parse error', error);
        reject(error);
      },
      complete: (results) => {
        resolve(results);
      }
    });
  });
};

export const validateParsedFileHeaders = (data: string[][], form: ComposerState): boolean => {
  const formLabel = form.metadata.label;
  if (data[0][0] !== formLabel || data[1][0] !== 'Item ID' || data[1][1] !== 'PageID'
    || data[1][2] !== 'ParentID ItemType' || data[1][3] !== 'Description') {
    return false
  }
  // Checking if each language in CSV has exactly 2 letters
  for (let i = 4; i < data[1].length; i++) {
    if (data[1][i].length !== 2)
      return false
  }
  // Check if the number of languages match
  if (data[1].length - 4 !== form.metadata.languages?.length) {
    return false
  }
  return true
}

const getItemKeys = (form: ComposerState): string[] => {
  const formItems = form.data;
  const keys = Object.keys(formItems);
  // eslint-disable-next-line prefer-const
  let itemKeys: string[] = [];
  for (const key of keys) {
    if (key === "questionnaire") {
      continue;
    }
    const resultingKey = `i:${formItems[key].id}:`;
    if (formItems[key].description) {
      itemKeys.push(resultingKey + "d");
    }
    if (formItems[key].validations) {
      formItems[key].validations?.forEach((_validation, index) => {
        itemKeys.push(`${resultingKey}v:${index}`);
      })
    }
    itemKeys.push(resultingKey + "l");
  }
  return itemKeys;
}

const getValueSetKeys = (form: ComposerState): string[] => {
  const valueSets = form.valueSets;
  const valueSetKeys: string[] = []
  if (valueSets) {
    valueSets.forEach((valueSet) => {
      const resultingKey = `v:${valueSet.id}:`;
      valueSet.entries?.forEach((entry, index) => {
        valueSetKeys.push(`${resultingKey}${index}:${entry.id}`)
      })
    })
  }
  return valueSetKeys;
}

export const validateParsedFileData = (data: string[][], form: ComposerState): ParsedImportData => {
  let itemKeys: string[] = getItemKeys(form);
  let valueSetKeys: string[] = getValueSetKeys(form);

  const parsedKeys: Set<string> = new Set();
  const missingInForm: string[] = [];
  for (let i = 2; i < data.length; i++) {
    parsedKeys.add(data[i][0]);
  }

  // Finding missing items in csv and missing items in form
  parsedKeys.forEach((descriptionItem: string) => {
    if (itemKeys?.includes(descriptionItem)) {
      itemKeys = itemKeys.filter(itemID => itemID !== descriptionItem);
    } else if (valueSetKeys?.includes(descriptionItem)) {
      valueSetKeys = valueSetKeys.filter(valueSetID => valueSetID !== descriptionItem);
    } else {
      missingInForm.push(descriptionItem);
    }
  });

  const missingInCsv: string[] = [...itemKeys, ...valueSetKeys];
  return { missingInCsv, missingInForm }
}


const createTranslationCSVRow = (value: MetadataEntry, key: string, translations: ItemTranslations, form: ComposerState): string[] => {
  const languages = form.metadata.languages || [];
  const row = [];
  row.push(key)
  row.push(value?.pageId);
  row.push(value?.parent);
  row.push(`${value.description} for ${key.split(":")[1]}`)
  languages.forEach(l => {
    const name = translations.translations[key];
    if (typeof name === "object") {
      row.push(name[l])
    } else {
      row.push("")
    }
  })
  return row;
}

export const createTranslationCSVformat = (
  allItemTranslations: ItemTranslations,
  globalValueSetTranslations: ItemTranslations | undefined,
  result: (string | undefined)[][],
  form: ComposerState): (string | undefined)[][] => {
  for (const [key, value] of Object.entries(allItemTranslations.metadata.key)) {
    const row = createTranslationCSVRow(value, key, allItemTranslations, form);
    result.push(row)
  }
  if (globalValueSetTranslations) {
    for (const [key, value] of Object.entries(globalValueSetTranslations.metadata.key)) {
      const row = createTranslationCSVRow(value, key, globalValueSetTranslations, form);
      result.push(row)
    }
  }
  return result
}

export const downloadFormData = (form: ComposerState): void => {
  const formLabel = form.metadata.label;
  const languages = form.metadata.languages || [];
  const allItemTranslations = getAllItemTranslations(form);
  const globalValueSetTranslations = getGlobalValueSetTranslations(form);

  let result = [];
  const firstRow = [formLabel]
  result.push(firstRow)
  const secondRow = ["Item ID", "PageID", "ParentID ItemType", "Description"];
  languages.forEach(l => {
    secondRow.push(l);
  })
  result.push(secondRow)
  result = createTranslationCSVformat(allItemTranslations, globalValueSetTranslations, result, form)

  const csv = Papa.unparse(result);
  const blob = new Blob([csv], { type: 'text/csv' });
  FileSaver.saveAs(blob, `translation_${formLabel}.csv`);
}

export const overwiewTextFormatter = (key: string) => {
  const keys = key.split(":");
  const type = keys[0];
  const id = keys[1];
  if (type === "i") {
    if (keys[2] === "l") {
      return `Item label for ${id}`;
    } else if (keys[2] === "d") {
      return `Item description for ${id}`;
    } else {
      return `Item validation rule[${keys[3]}] for ${id}`;
    }
  } else {
    // valueSet
    return `Valueset entry[${keys[2]}] for ${id}`;
  }
}

export const getLanguageName = (language: string) => {
  return MOST_USED_LANGUAGES[language]?.name || ISO_LANGUAGES[language]?.name || language;
}



export const buildItemLabelId = (itemId: string): string => {
  return `i:${itemId}:l`;
};

export const buildItemDescriptionId = (itemId: string): string => {
  return `i:${itemId}:d`;
};

export const buildValidationId = (itemId: string, index: number): string => {
  return `i:${itemId}:v:${index}`;
};

export const buildValueSetEntryId = (valueSetId: string, index: number, entryId: string): string => {
  return `v:${valueSetId}:${index}:${entryId}`;
};



export const parseEntryId = (entryId: string): ParsedEntryId | null => {
  const parts = entryId.split(':');
  
  if (parts[0] === 'i') {
    // Item entry
    if (parts.length < 3) return null;
    
    const itemId = parts[1];
    
    if (parts[2] === 'l') {
      return { type: 'item', itemId, subType: 'label' };
    } else if (parts[2] === 'd') {
      return { type: 'item', itemId, subType: 'description' };
    } else if (parts[2] === 'v' && parts.length >= 4) {
      const validationIndex = parseInt(parts[3], 10);
      return { type: 'item', itemId, subType: 'validation', validationIndex };
    }
  } else if (parts[0] === 'v' && parts.length >= 4) {
    // ValueSet entry
    const valueSetId = parts[1];
    const entryIndex = parseInt(parts[2], 10);
    const entryId = parts[3];
    
    return { type: 'valueset', valueSetId, entryIndex, entryId };
  }
  
  return null;
};


export const getTranslationType = (entryId: string): TranslationType | null => {
  const parsed = parseEntryId(entryId);
  if (!parsed) return null;
  
  if (parsed.type === 'item') {
    if (parsed.subType === 'label') return 'label';
    if (parsed.subType === 'description') return 'description';
    if (parsed.subType === 'validation') return 'validation';
  } else if (parsed.type === 'valueset') {
    return 'valueset';
  }
  
  return null;
};

export const getEntryText = (
  entryId: string,
  language: string,
  form: ComposerState
): string | undefined => {
  const parsed = parseEntryId(entryId);
  if (!parsed) return undefined;
  
  if (parsed.type === 'item') {
    const item = form.data[parsed.itemId];
    if (!item) return undefined;
    
    if (parsed.subType === 'label') {
      return item.label?.[language];
    } else if (parsed.subType === 'description') {
      return item.description?.[language];
    } else if (parsed.subType === 'validation' && parsed.validationIndex !== undefined) {
      return item.validations?.[parsed.validationIndex]?.message?.[language];
    }
  } else if (parsed.type === 'valueset') {
    const valueSet = form.valueSets?.find(vs => vs.id === parsed.valueSetId);
    return valueSet?.entries?.[parsed.entryIndex]?.label?.[language];
  }
  
  return undefined;
};


export const buildDisplayId = (entryId: string, form: ComposerState): string => {
  const parsed = parseEntryId(entryId);
  if (!parsed) return entryId;
  
  if (parsed.type === 'item') {
    if (parsed.subType === 'validation' && parsed.validationIndex !== undefined) {
      return `${parsed.itemId}-rule${parsed.validationIndex + 1}`;
    }
    return parsed.itemId;
  } else if (parsed.type === 'valueset') {
    // Check if it's a global valueset and use label if available
    const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === parsed.valueSetId);
    const valueSetDisplay = gvs?.label || parsed.valueSetId;
    return `${valueSetDisplay}-${parsed.entryId}`;
  }
  
  return entryId;
};



export const isAITranslated = (
  entryId: string,
  language: string,
  aiTranslations: TranslationMetadata[]
): boolean => {
  return aiTranslations.some(t => t.entryId === entryId && t.targetLanguage === language);
};

export const getAITranslationMetadata = (
  entryId: string,
  language: string,
  aiTranslations: TranslationMetadata[]
): TranslationMetadata | undefined => {
  return aiTranslations.find(t => t.entryId === entryId && t.targetLanguage === language);
};
