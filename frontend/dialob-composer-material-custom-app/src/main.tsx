import React from 'react'
import ReactDOM from 'react-dom/client'
import { BackendTypes, DefaultTypes, DialobComposer, PropEditors, SimpleField, Group, Note } from '@dialob/dialob-composer-material';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { siteTheme } from './siteTheme';
import { Circle, Phone, CropSquare, BlurLinear, TableRows, MoreHoriz, Place, TextFormat, Schedule, CalendarMonth, Tag, Euro, CheckBox, KeyboardArrowDown, List, ErrorOutline, Note as NoteIcon } from '@mui/icons-material';

const ITEM_EDITORS: DefaultTypes.ItemConfig =  {
  defaultIcon: Circle,
  items: [
    {
      matcher: item => item.type === 'text' && item.view === 'phoneNumber',
      component: SimpleField,
      props: {
        icon: Phone,
        placeholder: 'placeholders.phone'
      }
    },
		{
			matcher: item => item.type === 'group',
			component: Group,
			props: {
				icon: CropSquare,
				placeholder: 'placeholders.group',
				treeCollapsible: true,
			}
		},
		{
			matcher: item => item.type === 'surveygroup',
			component: Group,
			props: {
				icon: BlurLinear,
				placeholder: 'placeholders.surveygroup',
				treeCollapsible: true
			}
		},
		{
			matcher: item => item.type === 'rowgroup',
			component: Group,
			props: {
				icon: TableRows,
				placeholder: 'placeholders.rowgroup',
				treeCollapsible: true
			}
		},
		{
			matcher: item => item.type === 'survey',
			component: SimpleField,
			props: {
				icon: MoreHoriz,
				placeholder: 'placeholders.survey'
			}
		},
		{
			matcher: item => item.view === 'address',
			component: SimpleField,
			props: {
				icon: Place,
				placeholder: 'placeholders.address'
			}
		},
		{
			matcher: item => item.type === 'text',
			component: SimpleField,
			props: {
				icon: TextFormat,
				placeholder: 'placeholders.text'
			}
		},
		{
			matcher: item => item.type === 'time',
			component: SimpleField,
			props: {
				icon: Schedule,
				placeholder: 'placeholders.time'
			}
		},
		{
			matcher: item => item.type === 'date',
			component: SimpleField,
			props: {
				icon: CalendarMonth,
				placeholder: 'placeholders.date'
			}
		},
		{
			matcher: item => item.type === 'number',
			component: SimpleField,
			props: {
				icon: Tag,
				placeholder: 'placeholders.number'
			}
		},
		{
			matcher: item => item.type === 'decimal',
			component: SimpleField,
			props: {
				icon: Euro,
				placeholder: 'placeholders.decimal'
			}
		},
		{
			matcher: item => item.type === 'boolean',
			component: SimpleField,
			props: {
				icon: CheckBox,
				placeholder: 'placeholders.boolean'
			}
		},
		{
			matcher: item => item.type === 'list',
			component: SimpleField,
			props: {
				icon: KeyboardArrowDown,
				placeholder: 'placeholders.list'
			}
		},
		{
			matcher: item => item.type === 'multichoice',
			component: SimpleField,
			props: {
				icon: List,
				placeholder: 'placeholders.multichoice'
			}
		},
		{
			matcher: item => item.type === 'note' && item.view === 'validation',
			component: Note,
			props: {
				icon: ErrorOutline,
				placeholder: 'placeholders.validation',
				style: 'error'
			}
		},
		{
			matcher: item => item.type === 'note',
			component: Note,
			props: {
				icon: NoteIcon,
				placeholder: 'placeholders.note',
				style: 'normal'
			}
		}
	]
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
              component: PropEditors.InputProp,
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
            view: 'phoneNumber',
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
            view: 'phoneNumber',
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
