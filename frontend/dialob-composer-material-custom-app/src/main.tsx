import React from 'react'
import ReactDOM from 'react-dom/client'
import { BackendTypes, DefaultTypes, DialobComposer, DialobTypes, Editor, SimpleField } from '@dialob/dialob-composer-material';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { siteTheme } from './siteTheme';
import { Circle, Phone } from '@mui/icons-material';

const ITEM_EDITORS: DefaultTypes.ItemConfig =  {
  defaultIcon: Circle,
  items: [
      {
      matcher: (item: DialobTypes.DialobItem) => item.type === 'text' && item.view === 'phoneNumber',
      component: SimpleField,
      props: {
        icon: Phone,
        placeholder: 'placeholders.phone'
      }
    },
  ],
};

export const ITEMTYPE_CONFIG: DefaultTypes.ItemTypeConfig = {
  categories: [
    {
      title: 'Structure',
      type: 'structure',
      items: [
        {
          title: 'Group',
          propEditors: {
            columns: {
              component: Editor,
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
          title: 'Text',
          convertible: ['textBox', 'address'],
          config: {
            type: 'text',
            view: 'text'
          }
        },
        {
          title: 'Text box',
          convertible: ['text', 'address'],
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
              component: Editor,
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
        },
        {
          title: 'Boolean',
          config: {
            type: 'boolean'
          }
        },
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
        },
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
        },
        {
          title: "Phone number",
          config: {
            type: 'text',
            view: 'text',
            validations: [{
              message: {
                "en": "Phone number format is incorrect, format is optional country code starting with +, optional area code in parenthesis and 4-15 numbers",
                "fi": "Puhelinnumeron muoto on väärä, muoto on valinnainen maakoodi, joka alkaa +, valinnainen suuntanumero suluissa ja 4-15 numeroa",
                "sv": "Telefonnummerformatet är felaktigt, formatet är valfri landskod som börjar med +, valfritt riktnummer inom parentes och 4-15 siffror" 
              },
              rule: "answer not matches \"^(\\+[0-9]{1,3}\\s?)?(\\([0-9]{1,5}\\)\\s?)?(?:[0-9]\\s?){3,14}[0-9]$\""
            }
            ]
          }
        },
        {
          title: "Optional phone number",
          config: {
            type: 'text',
            view: 'text',
            validations: [{
              message: {
                "en": "Phone number format is incorrect, format is optional country code starting with +, optional area code in parenthesis and 4-15 numbers",
                "fi": "Puhelinnumeron muoto on väärä, muoto on valinnainen maakoodi, joka alkaa +, valinnainen suuntanumero suluissa ja 4-15 numeroa",
                "sv": "Telefonnummerformatet är felaktigt, formatet är valfri landskod som börjar med +, valfritt riktnummer inom parentes och 4-15 siffror" 
              },
              rule: "answer not matches \"^((\\+[0-9]{1,3}\\s?)?(\\([0-9]{1,5}\\)\\s?)?(?:[0-9]\\s?){3,14}[0-9])|()$\""
            }
            ]
          }
        },

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

const renderDialobComposer = (targetElement: HTMLElement, appConfig: BackendTypes.AppConfig) => {

  const FORM_ID = appConfig.formId;

  const baseUrl = window.location.origin;

  const DIALOB_COMPOSER_CONFIG: BackendTypes.DialobComposerConfig = {
    transport: {
      csrf: appConfig.csrfHeader ? {
        headerName: appConfig.csrfHeader,
        token: appConfig.csrf
      } : undefined,
      apiUrl: appConfig.backend_api_url.includes('://') ? appConfig.backend_api_url : baseUrl + appConfig.backend_api_url,
      previewUrl: appConfig.filling_app_url,
      tenantId: appConfig.tenantId || undefined,
      credentialMode: appConfig.credentialMode || undefined,
    },
    documentationUrl: 'https://github.com/dialob/dialob-parent/wiki/',
    itemEditors: ITEM_EDITORS,
    itemTypes: ITEMTYPE_CONFIG,
    backendVersion: appConfig.version,
    closeHandler: () => window.location.href = appConfig.adminAppUrl,
  };

  ReactDOM.createRoot(targetElement!).render(
    <React.StrictMode>
      <ThemeProvider theme={siteTheme}>
        <CssBaseline />
        <DialobComposer config={DIALOB_COMPOSER_CONFIG} formId={FORM_ID} />
      </ThemeProvider>
    </React.StrictMode>,
  )
};

window.renderDialobComposer = renderDialobComposer;

