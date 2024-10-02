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
      treeCollapsible?: boolean
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
          component: React.FC,
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          props?: any
        }
      },
      convertible?: DialobItemType[],
      config: DialobItemTemplate
    }[]
  }[]
}
