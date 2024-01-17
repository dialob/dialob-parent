import React from "react";
import Items from "../items";
import { DialobItem } from "../dialob";
import { SvgIconProps } from "@mui/material";
import { BlurLinear, CalendarMonth, CheckBox, Circle, CropSquare, ErrorOutline, Euro, KeyboardArrowDown, List, MoreHoriz, Note, Place, Schedule, TableRows, Tag, TextFormat } from "@mui/icons-material";


interface ItemConfig {
  defaultIcon: React.ComponentType<SvgIconProps>,
  items: {
    matcher: (item: DialobItem) => boolean,
    component: React.FC<{ item: DialobItem, props?: any }>,
    props: {
      icon: React.ComponentType<SvgIconProps>,
      placeholder: string,
      treeCollapsible?: boolean
    }
  }[]
}

export const DEFAULT_ITEM_CONFIG: ItemConfig = {
  defaultIcon: Circle,
  items: [
    {
      matcher: item => item.type === 'group',
      component: Items.Group,
      props: {
        icon: CropSquare,
        placeholder: 'Group label',
        treeCollapsible: true,
      }
    },
    {
      matcher: item => item.type === 'surveygroup',
      component: Items.Group,
      props: {
        icon: BlurLinear,
        placeholder: 'Survey group label',
        treeCollapsible: true
      }
    },
    {
      matcher: item => item.type === 'rowgroup',
      component: Items.Group,
      props: {
        icon: TableRows,
        placeholder: 'Multi-row group label',
        treeCollapsible: true
      }
    },
    {
      matcher: item => item.type === 'survey',
      component: Items.SimpleField,
      props: {
        icon: MoreHoriz,
        placeholder: 'Survey field label'
      }
    },
    {
      matcher: item => item.view === 'address',
      component: Items.SimpleField,
      props: {
        icon: Place,
        placeholder: 'Address field label'
      }
    },
    {
      matcher: item => item.type === 'text',
      component: Items.SimpleField,
      props: {
        icon: TextFormat,
        placeholder: 'Text field label'
      }
    },
    {
      matcher: item => item.type === 'time',
      component: Items.SimpleField,
      props: {
        icon: Schedule,
        placeholder: 'Time field label'
      }
    },
    {
      matcher: item => item.type === 'date',
      component: Items.SimpleField,
      props: {
        icon: CalendarMonth,
        placeholder: 'Date field label'
      }
    },
    {
      matcher: item => item.type === 'number',
      component: Items.SimpleField,
      props: {
        icon: Tag,
        placeholder: 'Number field label'
      }
    },
    {
      matcher: item => item.type === 'decimal',
      component: Items.SimpleField,
      props: {
        icon: Euro,
        placeholder: 'Decimal field label'
      }
    },
    {
      matcher: item => item.type === 'boolean',
      component: Items.SimpleField,
      props: {
        icon: CheckBox,
        placeholder: 'Boolean field label'
      }
    },
    {
      matcher: item => item.type === 'list',
      component: Items.SimpleField,
      props: {
        icon: KeyboardArrowDown,
        placeholder: 'List field label'
      }
    },
    {
      matcher: item => item.type === 'multichoice',
      component: Items.SimpleField,
      props: {
        icon: List,
        placeholder: 'Multi-choice field label'
      }
    },
    {
      matcher: item => item.type === 'note' && item.view === 'validation',
      component: Items.Note,
      props: {
        icon: ErrorOutline,
        placeholder: 'Validation message text'
      }
    },
    {
      matcher: item => item.type === 'note',
      component: Items.Note,
      props: {
        icon: Note,
        placeholder: 'Note text'
      }
    }
  ]
};

