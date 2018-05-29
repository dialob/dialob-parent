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

export function translateErrorType(error) {
  switch (error.get('type')) {
    case 'VARIABLE':
      return 'Variable';
    case 'VISIBILITY':
      return 'Visibility';
    case 'GENERAL':
      return error.get('message') === 'INVALID_DEFAULT_VALUE' ? 'Default' : 'General';
    case 'REQUIREMENT':
      return 'Requirement';
    case 'VALIDATION':
      return 'Validation';
    default:
      return error.get('type');
  };
}

export function translateErrorMessage(error) {
  switch (error.get('message')) {
    case 'RB_VARIABLE_NEEDS_EXPRESSION':
      return 'Missing expression';
    case 'INVALID_DEFAULT_VALUE':
      return 'Invalid value';
    case 'UNKNOWN_VARIABLE':
      return 'Unknown variable';
    case 'SYNTAX_ERROR':
      return 'Syntax error';
    case 'COULD_NOT_DEDUCE_TYPE':
      return 'Can\'t deduce type ';
    default:
      return error.get('message');
  };
}