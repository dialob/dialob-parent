import { ItemTypeConfig } from "./types";
import * as PropEditors from "../components/propEditors";

const ALERTSTYLE_PROP = {
  component: PropEditors.ChoiceProp,
  props: {
    options: [
      { key: 'normal', label: 'Normal (default)' },
      { key: 'info', label: 'Info' },
      { key: 'success', label: 'Success' },
      { key: 'warning', label: 'Warning' },
      { key: 'error', label: 'Error' }
    ]
  }
}


export const DEFAULT_ITEMTYPE_CONFIG: ItemTypeConfig = {
  categories: [
    {
      title: 'Structure',
      type: 'structure',
      items: [
        {
          title: 'Group',
          convertible: ['rowgroup'],
          optionEditors: [
            { name: 'Additional option', editor: PropEditors.InputProp }
          ],
          propEditors: {
            columns: {
              component: PropEditors.InputProp,
              props: {
                type: 'number',
                min: 1,
                max: 20
              }
            }
          },
          config: {
            type: 'group',
            props: {
              columns: 1
            }
          }
        },
        {
          title: 'Survey group',
          convertible: ['verticalSurveygroup'],
          config: {
            type: 'surveygroup'
          }
        },
        {
          title: 'Survey group (vertical)',
          convertible: ['surveygroup'],
          config: {
            type: 'surveygroup',
            view: 'verticalSurveygroup',
          }
        },
        {
          title: 'Multi-Row',
          convertible: ['group'],
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
            type: 'survey',
            view: 'survey'
          }
        },
        {
          title: 'Boolean',
          config: {
            type: 'boolean'
          }
        }
      ],
      subcategories: [
        {
          title: 'Text fields',
          items: [
            {
              title: 'Text',
              convertible: ['textBox', 'address', 'ytunnus', 'iban', 'hetu'],
              config: {
                type: 'text',
                view: 'text'
              }
            },
            {
              title: 'Text box',
              convertible: ['text', 'address', 'ytunnus', 'iban', 'hetu'],
              config: {
                type: 'text',
                view: 'textBox'
              }
            },
            {
              title: 'Address',
              convertible: ['text', 'textBox'],
              propEditors: {
                country: {
                  component: PropEditors.MultiChoiceProp,
                  props: {
                    allowAdditions: true,
                    options: [
                      { key: 'fi', label: 'Finland' },
                      { key: 'sv', label: 'Sweden' },
                      { key: 'ee', label: 'Estonia' }
                    ]
                  }
                }
              },
              config: {
                type: 'text',
                view: 'address',
                props: {
                  country: []
                }
              }
            },
            {
              title: 'Personal identity code (HETU)',
              convertible: ['text', 'textBox', 'ytunnus', 'iban'],
              config: {
                type: 'text',
                view: 'hetu',
                validations: [
                  {
                    rule: 'isNotHetu(answer)',
                    message: {
                      en: 'Check your personal FI identity code.',
                      fi: 'Kontrollera ditt FI personnummer.',
                      sv: 'Invalid Finnish personal ID!'
                    }
                  }
                ]
              }
            },
            {
              title: 'Company business ID (y-tunnus)',
              convertible: ['text', 'textBox', 'hetu', 'iban'],
              config: {
                type: 'text',
                view: 'ytunnus',
                validations: [
                  {
                    rule: 'isNotLyt(answer)',
                    message: {
                      en: 'Check the business ID you provided.',
                      fi: 'Tarkista antamasi yritystunnus.',
                      sv: 'Kontrollera ditt företagsnummer.'
                    }
                  }
                ]
              }
            },
            {
              title: 'Account number in IBAN format',
              convertible: ['text', 'textBox', 'ytunnus', 'hetu'],
              config: {
                type: 'text',
                view: 'iban',
                validations: [
                  {
                    rule: 'isNotIban(answer)',
                    message: {
                      en: 'Check that the account number you provided is correct and in IBAN format.',
                      fi: 'Tarkista, että antamasi tilinumero on oikein ja IBAN -muodossa.',
                      sv: 'Kontrollera att det kontonummer du angivit är korrekt och i IBAN-format.'
                    }
                  }
                ]
              }
            }
          ]
        },
        {
          title: 'Numbers',
          items: [
            {
              title: 'Decimal',
              convertible: ['number'],
              config: {
                type: 'decimal'
              }
            },
            {
              title: 'Integer',
              convertible: ['decimal'],
              config: {
                type: 'number'
              }
            }
          ]
        },
        {
          title: 'Date & Time',
          items: [
            {
              title: 'Date',
              config: {
                type: 'date'
              }
            },
            {
              title: 'Time',
              config: {
                type: 'time'
              }
            }
          ]
        },
        {
          title: 'Selections',
          items: [
            {
              title: 'Choice',
              convertible: ['multichoice'],
              config: {
                type: 'list'
              }
            },
            {
              title: 'Multi-choice',
              convertible: ['list'],
              config: {
                type: 'multichoice'
              }
            }
          ]
        }
      ]
    },
    {
      title: 'Outputs',
      type: 'output',
      items: [
        {
          title: 'Note',
          propEditors: {
            style: ALERTSTYLE_PROP
          },
          config: {
            type: 'note',
            props: {
              style: 'normal'
            }
          }
        },
        {
          title: 'Validation message',
          propEditors: {
            style: ALERTSTYLE_PROP
          },
          config: {
            type: 'note',
            view: 'validation',
            required: 'true',
            props: {
              style: 'error'
            }
          }
        }
      ]
    }
  ]
};
