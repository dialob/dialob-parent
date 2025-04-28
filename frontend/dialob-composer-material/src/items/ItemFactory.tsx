import { DialobItem } from "../types";
import { ItemConfig } from "../defaults/types";
import { scrollToTreeItem } from "../utils/ScrollUtils";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const itemFactory = (item: DialobItem, itemConfig: ItemConfig, props?: any) => {
  if (!item) {
    return null;
  }
  const matchedConfig = itemConfig.items.find(c => c.matcher(item));
  if (!matchedConfig) {
    console.warn('Unknown type:', item.type);
    return null;
  }

  const onClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    scrollToTreeItem(item.id);
  }

  const Component = matchedConfig.component;
  const componentProps = { onClick, ...props };
  return <Component key={item.id} item={item} {...componentProps} />;
}

export { itemFactory };
