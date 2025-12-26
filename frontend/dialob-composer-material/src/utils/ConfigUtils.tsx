import { CategoryItem, ItemTypeConfig, ItemTypeCategory } from "../defaults/types";

export const getCategoryItems = (category: ItemTypeCategory): CategoryItem[] => {
  const directItems = category.items || [];
  const subcategoryItems = category.subcategories 
    ? category.subcategories.flatMap(sub => sub.items)
    : [];
  return [...directItems, ...subcategoryItems];
};

export const findItemTypeConfig = (itemTypes: ItemTypeConfig, type: string, view?: string) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    // Check direct items
    if (c.items) {
      const resultConfig = c.items.find(v => v.config.type === type && (!view || v.config.view === view));
      if (resultConfig) {
        return resultConfig;
      }
    }
    // Check subcategories
    if (c.subcategories) {
      for (const sub of c.subcategories) {
        const resultConfig = sub.items.find(v => v.config.type === type && (!view || v.config.view === view));
        if (resultConfig) {
          return resultConfig;
        }
      }
    }
  }
  return null;
}

export const findItemTypeConvertible = (itemTypes: ItemTypeConfig, view: string) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    // Check direct items
    if (c.items) {
      const resultConfig = c.items.find(v => v.config.type === view || v.config.view === view);
      if (resultConfig) {
        return resultConfig;
      }
    }
    // Check subcategories
    if (c.subcategories) {
      for (const sub of c.subcategories) {
        const resultConfig = sub.items.find(v => v.config.type === view || v.config.view === view);
        if (resultConfig) {
          return resultConfig;
        }
      }
    }
  }
  return null;
}

export const findItemPropEditor = (itemTypes: ItemTypeConfig, view: string) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    // Check direct items
    if (c.items) {
      const resultConfig = c.items.find(v => v.config.type === view || v.config.view === view);
      if (resultConfig && resultConfig.propEditors) {
        return resultConfig.propEditors;
      }
    }
    // Check subcategories
    if (c.subcategories) {
      for (const sub of c.subcategories) {
        const resultConfig = sub.items.find(v => v.config.type === view || v.config.view === view);
        if (resultConfig && resultConfig.propEditors) {
          return resultConfig.propEditors;
        }
      }
    }
  }
  return undefined;
}
