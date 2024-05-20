import { ComposerState, ValueSet } from "../dialob";
import Papa from 'papaparse';
import FileSaver from 'file-saver';

export const downloadValueSet = (valueSet?: ValueSet) => {
  if (!valueSet) {
    return;
  }
  const entries = valueSet?.entries;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const result: { [key: string]: any }[] = [];
  entries.forEach(e => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const entry: { [key: string]: any } = { ID: e.id };
    for (const lang in e.label) {
      entry[lang] = e.label[lang];
    }
    result.push(entry);
  });
  const csv = Papa.unparse(result);
  const blob = new Blob([csv], { type: 'text/csv' });
  FileSaver.saveAs(blob, `valueSet-${valueSet.id}.csv`);
}

export const downloadForm = (form: ComposerState) => {
  const json = JSON.stringify(form, null, 2);
  const blob = new Blob([json], { type: 'application/json;charset=utf-8' });
  FileSaver.saveAs(blob, `${form._id}.json`);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const parseCsvFile = (inputFile: File): Promise<Papa.ParseResult<any>> => {
  return new Promise((resolve, reject) => {
    Papa.parse(inputFile, {
      header: true,
      transformHeader: h => h.trim(),
      skipEmptyLines: true,
      error: (error) => {
        console.error('CSV Parse error', error);
        reject(error);
      },
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      complete: (results: Papa.ParseResult<any>) => {
        resolve(results);
      }
    });
  });
};
