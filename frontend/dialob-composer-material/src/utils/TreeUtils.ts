import { ItemId, TreeData } from '@atlaskit/tree';
import { ContextVariable, DialobItem, ValueSet, Variable } from '../dialob';

export const INIT_TREE: TreeData = {
  rootId: 'root',
  items: {},
}

export const buildTreeFromVariables = (variables: (ContextVariable | Variable)[]): TreeData => {
  const items = variables.map((variable, index) => ({
    id: variable.name,
    children: [],
    data: {
      variable: variable,
      index: index,
    }
  }));
  return {
    rootId: 'root',
    items: {
      root: { id: 'root', children: items.map(i => i.id), data: undefined },
      ...Object.fromEntries(items.map(i => [i.id, i]))
    }
  };
}

export const buildTreeFromValueSet = (valueSet?: ValueSet): TreeData => {
  if (!valueSet || !valueSet.entries) {
    return { rootId: 'root', items: {} };
  }
  const items = valueSet.entries.map((entry, index) => ({
    id: entry.id,
    children: [],
    data: {
      entry,
      index
    }
  }));
  return {
    rootId: 'root',
    items: {
      root: { id: 'root', children: items.map(i => i.id), data: undefined },
      ...Object.fromEntries(items.map(i => [i.id, i]))
    }
  };
}

export const buildTreeFromForm = (formData: { [item: string]: DialobItem }, language: string): TreeData => {
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
    const title = item.label && item.label[language] ? item.label[language] : id;
    tree.items[id] = {
      id,
      children: [],
      data: {
        title,
        isPage: item.type === 'group' && parentId === 'root',
        item,
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
