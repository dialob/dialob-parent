import { SvgIconProps } from "@mui/material"
import { DialobCategoryType, DialobItem, DialobItemTemplate, DialobItemType } from "../dialob"

export interface ItemConfig {
  defaultIcon: React.ComponentType<SvgIconProps>,
  items: ItemConfigItem[]
}

export interface ItemConfigItem {
  matcher: (item: DialobItem) => boolean,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  component: React.FC<{ item: DialobItem } & Record<string, any>>,
  props: ConfigItemProps
}

export interface ConfigItemProps {
  icon: React.ComponentType<SvgIconProps>,
  placeholder: string,
  treeCollapsible?: boolean,
  style?: 'normal' | 'success' | 'info' | 'warning' | 'error',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [key: string]: any
}

export interface ItemTypeConfig {
  categories: ItemTypeCategory[]
}

export interface ItemTypeCategory {
  title: string,
  type: DialobCategoryType,
  items: CategoryItem[]
}

export interface CategoryItem {
  title: string,
  optionEditors?: OptionEditor[],
  propEditors?: PropEditorsType,
  convertible?: DialobItemType[],
  config: DialobItemTemplate
}

export interface OptionEditor {
  name: string,
  editor: React.FC
}

export interface PropEditorsType {
  [key: string]: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    component: any,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    props?: any
  }
}
