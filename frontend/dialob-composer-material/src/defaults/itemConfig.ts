import { Group } from "../items/Group";
import { SimpleField } from "../items/SimpleField";
import { Note } from "../items/Note";
import {
  BlurLinear, CalendarMonth, CheckBox, Circle, CropSquare, ErrorOutline, Euro, KeyboardArrowDown,
  List, MoreHoriz, Note as NoteIcon, Place, Schedule, TableRows, Tag, TextFormat
} from "@mui/icons-material";
import { ItemConfig } from "../defaults/types";

export const DEFAULT_ITEM_CONFIG: ItemConfig = {
  defaultIcon: Circle,
  items: [
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

