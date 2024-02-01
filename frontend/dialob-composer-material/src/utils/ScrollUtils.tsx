import { scroller } from "react-scroll";
import { DialobItem } from "../dialob";
import { MENU_HEIGHT } from "../theme/siteTheme";

const isItemChild = (itemId: string, items: DialobItem[], childItemIds: string[]): boolean => {
  if (childItemIds.includes(itemId)) {
    return true;
  }
  const childItems = childItemIds.map(id => items.find(i => i.id === id)!);
  return childItems.some(i => {
    return isItemChild(itemId, items, i.items || []);
  });
}

const findTopLevelParent = (itemId: string, items: DialobItem[]): DialobItem | undefined => {
  const item = items.find(i => i.id === itemId);
  if (!item) {
    return undefined;
  }
  const parent = items.find(i => i.items?.includes(item.id));
  if (!parent || parent.id === 'questionnaire') {
    return item;
  }
  return findTopLevelParent(parent.id, items);
}

export const scrollToItem = (itemId: string, items: DialobItem[], activePage: DialobItem | undefined, setActivePage: (item: DialobItem) => void) => {
  const isItemOnCurrentPage = isItemChild(itemId, items, activePage?.items || []);
  const timeout = isItemOnCurrentPage ? 0 : 1000;
  if (!isItemOnCurrentPage) {
    const parent = findTopLevelParent(itemId, items);
    if (parent) {
      setActivePage(parent);
    }
  }
  setTimeout(() => scroller.scrollTo(itemId, {
    offset: -MENU_HEIGHT - 10,
    duration: 500,
    smooth: true,
  }), timeout);
}
