import { DialobItem } from "../dialob";
import { DEFAULT_ITEM_CONFIG } from "../defaults";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const itemFactory = (item: DialobItem, props?: any) => {
  if (!item) {
    return null;
  }
  const itemConfig = DEFAULT_ITEM_CONFIG.items.find(c => c.matcher(item));
  if (!itemConfig) {
    console.warn('Unknown type:', item.type);
    return null;
  }
  const Component = itemConfig.component;
  return <Component key={item.id} item={item} {...props} />;
}

export { itemFactory };
