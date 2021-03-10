import {ItemProps} from '../components/componentTypes';
import {Page, Group, DateItem, Time, Text, Boolean, Choice, MultiChoice, Note, RowGroup, SurveyGroup} from '../components';
import { Decimal } from '../components/Decimal';

interface ItemConfigEntry {
  matcher: (item: any, isMainGroupItem: boolean) => boolean;
  component: React.Component<ItemProps> | React.FC<ItemProps>;
  answerRequired: boolean;
  childrenRequired: boolean;
}

export interface ItemconfigType {
  items: ItemConfigEntry[]
}

export const DEFAULT_ITEM_CONFIG: ItemconfigType = {
  items: [
    {
      matcher: (item, isMainGroupItem) => isMainGroupItem,
      component: Page,
      answerRequired: false,
      childrenRequired: true
    },
    {
      matcher: item => item.type === 'group' && item.view === 'page',
      component: Page,
      answerRequired: false,
      childrenRequired: true
    },
    {
      matcher: item => item.type === 'group',
      component: Group,
      answerRequired: false,
      childrenRequired: true
    },
    {
      matcher: item => item.type === 'surveygroup',
      component: SurveyGroup,
      answerRequired: false,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'rowgroup',
      component: RowGroup,
      answerRequired: false,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'boolean',
      component: Boolean,
      answerRequired: true,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'text',
      component: Text,
      answerRequired: true,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'list',
      component: Choice,
      answerRequired: true,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'multichoice',
      component: MultiChoice,
      answerRequired: true,
      childrenRequired: false
    },
    /*
    {
      matcher: item => item.type === 'survey', // Survey is handled within survey group
      component: SurveyItem,
      answerRequired: true,
      childrenRequired: false
    },
    */
    {
      matcher: item => item.type === 'note',
      component: Note,
      answerRequired: false,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'date',
      component: DateItem,
      answerRequired: true,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'time',
      component: Time,
      answerRequired: true,
      childrenRequired: false
    },
    {
      matcher: item => item.type === 'number' || item.type === 'decimal',
      component: Decimal,
      answerRequired: true,
      childrenRequired: false
    }
  ]
};
