export function findRoot(data) {
  if (!data) {return null;}
  return data.find((v, k) => v.get('type') === 'questionnaire');
}

export function findValueset(data, id) {
  if (!data || !data.get('valueSets') || !id) {return null;}
  return data.get('valueSets').find(v => v.get('id') === id);
}

export function isGlobalValueSet(globalValueSets, id) {
  const gvsIndex = globalValueSets ? globalValueSets.findIndex(vs => vs.get('valueSetId') === id) : -1;
  return gvsIndex > -1;
}
