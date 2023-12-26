import { DialobItem } from "../dialob";
import { DEFAULT_ITEM_CONFIG } from "../defaults";

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
  return <Component key={item.id} itemType={item.type} itemId={item.id} {...itemConfig.props} {...props} />;
}

export { itemFactory };
