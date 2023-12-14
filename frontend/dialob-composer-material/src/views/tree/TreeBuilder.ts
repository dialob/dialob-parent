import { ItemId, TreeData } from "@atlaskit/tree";
import { DialobItem } from "../../dialob";


export const buildTreeFromForm = (formData: { [item: string]: DialobItem }): TreeData => {
  const tree: TreeData = {
    rootId: 'root',
    items: {
      root: {
        id: 'root',
        children: [],
        data: {
          title: 'root',
        },
      },
    },
  };

  const addChildrenFromParent = (item: DialobItem, parentId: ItemId) => {
    if (item.items) {
      const childItems = Object.values(formData)
        .filter(child => item.items?.includes(child.id))
        .sort((a, b) => item.items!.indexOf(a.id) - item.items!.indexOf(b.id));
      childItems.forEach(child => {
        addChild(child, parentId);
        addChildrenFromParent(child, child.id);
      });
    }
  }

  const addChild = (item: DialobItem, parentId: ItemId) => {
    const id = item.id;
    const type = item.type === 'group' && parentId === 'root' ? 'page' : item.type;
    tree.items[id] = {
      id,
      children: [],
      data: {
        title: id,
        type
      },
      isExpanded: true,
      hasChildren: item.items?.length ? true : false,
    };
    tree.items[parentId].children.push(id);
  };

  const formRoot = Object.values(formData).find(item => item.type === 'questionnaire');

  if (!formRoot) {
    return tree;
  }
  addChildrenFromParent(formRoot, 'root');

  return tree;

}
