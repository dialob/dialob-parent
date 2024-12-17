import { SvgIconProps } from "@mui/material"
import { DialobCategoryType, DialobItem, DialobItemTemplate, DialobItemType } from "../dialob"

export interface ItemConfig {
  defaultIcon: React.ComponentType<SvgIconProps>,
  items: {
    matcher: (item: DialobItem) => boolean,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    component: React.FC<{ item: DialobItem, props?: any }>,
    props: {
      icon: React.ComponentType<SvgIconProps>,
      placeholder: string,
      treeCollapsible?: boolean,
      style?: 'normal' | 'success' | 'info' | 'warning' | 'error',
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      [key: string]: any
    }
  }[]
}

export interface ItemTypeConfig {
  categories: {
    title: string,
    type: DialobCategoryType,
    items: {
      title: string,
      optionEditors?: {
        name: string,
        editor: React.FC
      }[],
      propEditors?: {
        [key: string]: {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          component: any,
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          props?: any
        }
      },
      convertible?: DialobItemType[],
      config: DialobItemTemplate
    }[]
  }[]
}
