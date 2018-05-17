export const DEFAULT_ITEMTYPE_CONFIG = {
  categories: [
    {
      title: 'Structure',
      type: 'structure',
      items: [
        {
          title: 'Group',
          config: {
            type: 'group',
            props: {
              columns: 1
            }
          }
        },
        {
          title: 'Survey group',
          config: {
            type: 'surveygroup'
          }
        },
        {
          title: 'Multi-Row',
          config: {
            type: 'rowgroup'
          }
        }
      ]
    },
    {
      title: 'Inputs',
      type: 'input',
      items: [
        {
          title: 'Survey item',
          config: {
            type: 'survey'
          }
        },
        {
          title: 'Text',
          config: {
            type: 'text'
          }
        },
        {
          title: 'Text box',
          config: {
            type: 'text',
            styleClass: ['textbox']
          }
        },
        {
          title: 'Decimal',
          config: {
            type: 'decimal'
          }
        },
        {
          title: 'Integer',
          config: {
            type: 'number'
          }
        },
        {
          title: 'Boolean',
          config: {
            type: 'boolean'
          }
        },
        {
          title: 'Choice',
          config: {
            type: 'list'
          }
        },
        {
          title: 'Multi-choice',
          config: {
            type: 'multichoice'
          }
        }
      ]
    },
    {
      title: 'Outputs',
      type: 'output',
      items: [
        {
          title: 'Note',
          config: {
            type: 'note'
          }
        }
      ]
    }
  ]
};