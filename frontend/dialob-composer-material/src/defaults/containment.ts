const CONTAINER_TYPES = ['group', 'rowgroup', 'surveygroup'];
const ITEM_TYPES = ['text', 'number', 'decimal', 'boolean', 'note', 'time', 'date', 'list', 'multichoice'];
const ALL_TYPES = CONTAINER_TYPES.concat(ITEM_TYPES, ['survey']);

const CONTAINMENT: { [key: string]: string[] } = {
  page: CONTAINER_TYPES,
  group: CONTAINER_TYPES.concat(ITEM_TYPES),
  rowgroup: ALL_TYPES,
  surveygroup: ITEM_TYPES.concat(['survey']),
  questionnaire: ['page'],
};

export const canContain = (container: string, item: string): boolean => {
  const allowedTypes = CONTAINMENT[container];
  if (!allowedTypes) {
    return false;
  }
  return allowedTypes.indexOf(item) > -1;
}
