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
