import { ComposerState } from "../dialob";

export type TranslationType = 'label' | 'description' | 'valueset' | 'validation';

export interface MissingTranslation {
  id: string;
  missingIn: string[];
  index?: number;
  global?: boolean;
}

export type MissingTranslations = {
  [type in TranslationType]?: MissingTranslation[];
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
    // iterate through languages
    languages.forEach(lang => {
      // check item labels
      if (item.label && (item.label[lang] === undefined || item.label[lang] === '')) {
        if (!missing.label?.find(m => m.id === item.id)) {
          missing.label?.push({ id: item.id, missingIn: [lang] });
        } else {
          missing.label?.find(m => m.id === item.id)!.missingIn.push(lang);
        }
      }
      // check item descriptions
      if (item.description && (item.description[lang] === undefined || item.description[lang] === '')) {
        if (!missing.description?.find(m => m.id === item.id)) {
          missing.description?.push({ id: item.id, missingIn: [lang] });
        } else {
          missing.description?.find(m => m.id === item.id)!.missingIn.push(lang);
        }
      }
      // check item validations
      if (item.validations) {
        // iterate validations and check messages
        item.validations.forEach((v, idx) => {
          if (v.message && (v.message[lang] === undefined || v.message[lang] === '')) {
            if (!missing.validation?.find(m => m.id === item.id)) {
              missing.validation?.push({ id: `${item.id}-rule${idx + 1} (v:${idx})`, missingIn: [lang], index: idx });
            } else {
              missing.validation?.find(m => m.id === item.id)!.missingIn.push(lang);
            }
          }
        })
      }
    });
  });

  // iterate through valuesets
  form.valueSets?.forEach(vs => {
    vs.entries.forEach((vse, idx) => {
      languages.forEach(lang => {
        if (vse.label && (vse.label[lang] === undefined || vse.label[lang] === '')) {
          if (!missing.valueset?.find(m => m.id === vse.id)) {
            missing.valueset?.push({ id: `${vs.id}-${vse.id} (${vs.id}:${idx})`, missingIn: [lang], index: idx });
          } else {
            missing.valueset?.find(m => m.id === vse.id)!.missingIn.push(lang);
          }
        }
      })
    })
  });

  // match valuesets with names
  Object.values(form.data).forEach(item => {
    if (item.valueSetId) {
      const vs = form.valueSets?.find(v => v.id === item.valueSetId);
      const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === item.valueSetId);
      if (vs) {
        missing.valueset?.forEach(m => {
          if (m.id.startsWith(vs.id + '-')) {
            const id = m.id.split('-');
            m.id = `${gvs ? (gvs.label ?? id[0]) : item.id}-${id[1]}`;
            m.global = !!gvs;
          }
        });
      }
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

// TODO: Add functions for CSV generation and validation here
