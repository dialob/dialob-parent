import { FolderOpen } from '@mui/icons-material';

export const FALLBACK_LANGUAGE = 'en';
export const TREE_WIDTH = '300px';
export const MAX_VARIABLE_DESCRIPTION_LENGTH = 50;

export const PAGE_CONFIG = {
  type: 'group',
  view: 'page',
  icon: FolderOpen,
};

export const CHOICE_ITEM_TYPES = ['list', 'multichoice', 'surveygroup'];

export * from './itemConfig';
export * from './itemTypes';
export * from './languageConfig';
export * from './containment';
export * from './valueSetProps';
