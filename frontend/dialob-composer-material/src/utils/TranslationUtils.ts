import Papa from "papaparse";
import { ComposerState, DialobItem, LocalizedString, ValueSet } from "../dialob";

export type TranslationType = 'label' | 'description' | 'valueset' | 'validation';

export interface TranslationData {
	[key: string]: LocalizedString | undefined | string;
}

export interface MetadataEntry {
	description: string;
	richText?: boolean;
	pageId: string;
	parent: string;
}

export interface Metadata {
	key: { [key: string]: MetadataEntry };
}

export interface GlobalValueSet {
	label?: string;
	valueSetId: string;
}

export interface ItemTranslations {
	translations: TranslationData;
	metadata: Metadata;
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

export const getAllItemTranslations = (form: ComposerState) => {
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
					valueSet.entries.forEach((entry, index) => {
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

export const getGlobalValueSetTranslations = (form: ComposerState) => {
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
				valueSet.entries.forEach((entry, index) => {
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
