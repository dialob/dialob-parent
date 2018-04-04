import {Group, SimpleField, Note} from '.';

export const DEFAULT_ITEM_CONFIG = {
  items: [
    /*
    {
      matcher: item => item.get('type') === 'questionnaire',
      component: Questionnaire
    },
    {
      matcher: item => item.get('type') === 'page',
      component: Page
    },
    */
    {
      matcher: item => item.get('type') === 'group',
      component: Group,
      props: {
        icon: 'square outline',
        placeholder: 'Group label'
      }
    },
    {
      matcher: item => item.get('type') === 'text',
      component: SimpleField,
      props: {
        icon: 'font',
        placeholder: 'Text field label'
      }
    },
    {
      matcher: item => item.get('type') === 'number',
      component: SimpleField,
      props: {
        icon: 'hashtag',
        placeholder: 'Number field label'
      }
    },
    {
      matcher: item => item.get('type') === 'decimal',
      component: SimpleField,
      props: {
        icon: 'currency',
        placeholder: 'Decimal field label'
      }
    },
    {
      matcher: item => item.get('type') === 'boolean',
      component: SimpleField,
      props: {
        icon: 'checkmark box',
        placeholder: 'Boolean field label'
      }
    },
    {
      matcher: item => item.get('type') === 'list',
      component: SimpleField,
      props: {
        icon: 'caret down',
        placeholder: 'List field label'
      }
    },
    {
      matcher: item => item.get('type') === 'multichoice',
      component: SimpleField,
      props: {
        icon: 'list',
        placeholder: 'Multi-choice field label'
      }
    },
    {
      matcher: item => item.get('type') === 'note',
      component: Note,
      props: {
        icon: 'caret down',
        placeholder: 'List field label'
      }
    },
    /*
    {
      matcher: item => item.get('type') === 'note',
      component: Note
    },
    {
      matcher: item => item.get('type') === 'boolean',
      component: Boolean
    },
    {
      matcher: item => item.get('type') === 'list',
      component: Choice
    },
    {
      matcher: item => item.get('type') === 'multichoice',
      component: Choice,
      props: { multi: true }
    }
    */
  ]
};

