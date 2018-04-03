export function findRoot(data) {
  if (!data) {return null;}
  return data.find((v, k) => v.get('type') === 'questionnaire');
}