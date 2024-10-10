import { DialobItem } from "../dialob";
import { DEFAULT_ITEM_CONFIG } from "../defaults";
import { ItemConfig } from "../defaults/types";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const itemFactory = (item: DialobItem, itemConfig?: ItemConfig, props?: any) => {
  if (!item) {
    return null;
  }
  const resolvedConfig = itemConfig ?? DEFAULT_ITEM_CONFIG;
  const matchedConfig = resolvedConfig.items.find(c => c.matcher(item));
  if (!matchedConfig) {
    console.warn('Unknown type:', item.type);
    return null;
  }
  const Component = matchedConfig.component;
  return <Component key={item.id} item={item} {...props} />;
}

export { itemFactory };
