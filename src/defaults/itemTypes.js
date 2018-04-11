export const DEFAULT_ITEMTYPE_CONFIG = {
  categories: [
    {
      title: 'Structure',
      type: 'structure',
      items: [
        {
          title: 'Group',
          config: {
            type: 'group'
          }
        },
        {
          title: 'Survey',
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