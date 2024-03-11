import Papa from "papaparse";

export const parse = (inputFile: File): Promise<Papa.ParseResult<any>> => {
  return new Promise((resolve, reject) => {
    Papa.parse(inputFile, {
      header: true,
      transformHeader: h => h.trim(),
      skipEmptyLines: true,
      error: (error) => {
        console.error('CSV Parse error', error);
        reject(error);
      },
      complete: (results: Papa.ParseResult<any>) => {
        resolve(results);
      }
    });
  });
};

// TODO: Add functions for CSV generation and validation here
