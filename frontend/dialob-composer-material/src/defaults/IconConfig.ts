import { SvgIconProps } from "@mui/material";
import { 
  BlurLinear, CalendarMonth, CheckBox, CropSquare, Euro, FolderOpen, 
  KeyboardArrowDown, List, MoreHoriz, Note, Place, Schedule, 
  TableRows, Tag, TextFormat 
} from "@mui/icons-material";


export const DEFAULT_ICON_CONFIG: Record<string, React.ComponentType<SvgIconProps>> = {
  group: CropSquare,
  surveygroup: BlurLinear,
  rowgroup: TableRows,
  survey: MoreHoriz,
  address: Place,
  text: TextFormat,
  time: Schedule,
  date: CalendarMonth,
  number: Tag,
  decimal: Euro,
  boolean: CheckBox,
  list: KeyboardArrowDown,
  multichoice: List,
  note: Note,
  page: FolderOpen,
}
