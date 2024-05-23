import { Completion } from "@codemirror/autocomplete";

export const RESERVED_WORDS: Completion[] = [
  { label: 'now', type: 'function' },
  { label: 'in', type: 'keyword' },
  { label: 'not', type: 'keyword' },
  { label: 'and', type: 'keyword' },
  { label: 'or', type: 'keyword' },
  { label: 'true', type: 'keyword' },
  { label: 'false', type: 'keyword' },
  { label: 'matches', type: 'function' },
  { label: 'today', type: 'function' },
  { label: 'now', type: 'function' },
  { label: 'year', type: 'function' },
  { label: 'years', type: 'function' },
  { label: 'minute', type: 'function' },
  { label: 'minutes', type: 'function' },
  { label: 'second', type: 'function' },
  { label: 'seconds', type: 'function' },
  { label: 'day', type: 'function' },
  { label: 'days', type: 'function' },
  { label: 'week', type: 'function' },
  { label: 'weeks', type: 'function' },
  { label: 'hour', type: 'function' },
  { label: 'hours', type: 'function' },
  { label: 'answered', type: 'function' },
  { label: 'answer', type: 'function' },
  { label: 'is', type: 'function' },
  { label: 'lengthOf', type: 'function' },
  { label: 'valid', type: 'function' },
  { label: 'count', type: 'function' },
  { label: 'of', type: 'keyword' },
  { label: 'sum', type: 'function' },
  { label: 'min', type: 'function' },
  { label: 'max', type: 'function' },
  { label: 'any', type: 'function' },
  { label: 'all', type: 'function' }
];

export const IGNORED_ITEM_TYPES = [
  'questionnaire',
  'group',
  'note'
];

export const VALID_ID_PATTERN = /^[a-zA-Z][_a-zA-Z\d]*$/;
