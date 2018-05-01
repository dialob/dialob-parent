export function findRoot(data) {
  if (!data) {return null;}
  return data.find((v, k) => v.get('type') === 'questionnaire');
}

export function findValueset(data, id) {
  if (!data || !data.get('valueSets') || !id) {return null;}
  return data.get('valueSets').find(v => v.get('id') === id);
}

/*

  "valueSets": [
      {
          "id": "question5_valueset1",
          "entries": [
              {
                  "id": "educationA",
                  "label": {
                      "en": "Peruskoulu"
                  }
              },

*/