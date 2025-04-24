import { ItemTypeConfig } from "../defaults/types";

export const findItemTypeConfig = (itemTypes: ItemTypeConfig, type: string, view?: string) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    const resultConfig = c.items.find(v => v.config.type === type && (!view || v.config.view === view));
    if (resultConfig) {
      return resultConfig;
    }
  }
  return null;
}

export const findItemTypeConvertible = (itemTypes: ItemTypeConfig, view: string) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    const resultConfig = c.items.find(v => v.config.type === view || v.config.view === view);
    if (resultConfig) {
      return resultConfig;
    }
  }
  return null;
}

export const findItemPropEditor = (itemTypes: ItemTypeConfig, view: string) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    const resultConfig = c.items.find(v => v.config.type === view || v.config.view === view);
    if (resultConfig && resultConfig.propEditors) {
      return resultConfig.propEditors;
    }
  }
  return undefined;
}
