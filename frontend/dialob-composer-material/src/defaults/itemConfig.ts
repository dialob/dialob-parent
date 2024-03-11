import React from "react";
import Items from "../items";
import { DialobItem } from "../dialob";
import { SvgIconProps } from "@mui/material";
import { BlurLinear, CalendarMonth, CheckBox, Circle, CropSquare, ErrorOutline, Euro, KeyboardArrowDown, List, MoreHoriz, Note, Place, Schedule, TableRows, Tag, TextFormat } from "@mui/icons-material";


interface ItemConfig {
  defaultIcon: React.ComponentType<SvgIconProps>,
  items: {
    matcher: (item: DialobItem) => boolean,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
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
        placeholder: 'placeholders.group',
        treeCollapsible: true,
      }
    },
    {
      matcher: item => item.type === 'surveygroup',
      component: Items.Group,
      props: {
        icon: BlurLinear,
        placeholder: 'placeholders.surveygroup',
        treeCollapsible: true
      }
    },
    {
      matcher: item => item.type === 'rowgroup',
      component: Items.Group,
      props: {
        icon: TableRows,
        placeholder: 'placeholders.rowgroup',
        treeCollapsible: true
      }
    },
    {
      matcher: item => item.type === 'survey',
      component: Items.SimpleField,
      props: {
        icon: MoreHoriz,
        placeholder: 'placeholders.survey'
      }
    },
    {
      matcher: item => item.view === 'address',
      component: Items.SimpleField,
      props: {
        icon: Place,
        placeholder: 'placeholders.address'
      }
    },
    {
      matcher: item => item.type === 'text',
      component: Items.SimpleField,
      props: {
        icon: TextFormat,
        placeholder: 'placeholders.text'
      }
    },
    {
      matcher: item => item.type === 'time',
      component: Items.SimpleField,
      props: {
        icon: Schedule,
        placeholder: 'placeholders.time'
      }
    },
    {
      matcher: item => item.type === 'date',
      component: Items.SimpleField,
      props: {
        icon: CalendarMonth,
        placeholder: 'placeholders.date'
      }
    },
    {
      matcher: item => item.type === 'number',
      component: Items.SimpleField,
      props: {
        icon: Tag,
        placeholder: 'placeholders.number'
      }
    },
    {
      matcher: item => item.type === 'decimal',
      component: Items.SimpleField,
      props: {
        icon: Euro,
        placeholder: 'placeholders.decimal'
      }
    },
    {
      matcher: item => item.type === 'boolean',
      component: Items.SimpleField,
      props: {
        icon: CheckBox,
        placeholder: 'placeholders.boolean'
      }
    },
    {
      matcher: item => item.type === 'list',
      component: Items.SimpleField,
      props: {
        icon: KeyboardArrowDown,
        placeholder: 'placeholders.list'
      }
    },
    {
      matcher: item => item.type === 'multichoice',
      component: Items.SimpleField,
      props: {
        icon: List,
        placeholder: 'placeholders.multichoice'
      }
    },
    {
      matcher: item => item.type === 'note' && item.view === 'validation',
      component: Items.Note,
      props: {
        icon: ErrorOutline,
        placeholder: 'placeholders.validation'
      }
    },
    {
      matcher: item => item.type === 'note',
      component: Items.Note,
      props: {
        icon: Note,
        placeholder: 'placeholders.note'
      }
    }
  ]
};

