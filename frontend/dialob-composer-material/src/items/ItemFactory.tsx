import { DialobItem } from "../types";
import { ItemConfig } from "../defaults/types";
import { scrollToTreeItem } from "../utils/ScrollUtils";
import { ExpressionVariable } from "./ExpressionVariable";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const itemFactory = (item: DialobItem | string, itemConfig: ItemConfig, setHighlightedItem?: (item: DialobItem) => void, props?: any) => {
  if (!item) {
    return null;
  }

  // Handle case where item is a variable ID (string)
  if (typeof item === 'string') {
    const onClick = (e: React.MouseEvent) => {
      e.stopPropagation();
      scrollToTreeItem(item);
      const pseudoItem = { id: item } as DialobItem;
      if (setHighlightedItem) {
        setHighlightedItem(pseudoItem);
      }
    };
    return <ExpressionVariable key={item} variableId={item} onClick={onClick} {...props} />;
  }

  // Handle regular items
  const matchedConfig = itemConfig.items.find(c => c.matcher(item));
  if (!matchedConfig) {
    console.warn('Unknown type:', item.type);
    return null;
  }

  const onClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    scrollToTreeItem(item.id);
    if (setHighlightedItem) {
      setHighlightedItem(item);
    }
  };

  const Component = matchedConfig.component;
  const componentProps = { onClick, ...props };
  return <Component key={item.id} item={item} {...componentProps} />;
}

export { itemFactory };
