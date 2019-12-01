import { SessionItem, SessionError } from '@resys/dialob-fill-api';
import { useEffect, useState, useRef } from 'react';
import { useFillSession } from './useFillSession';

export interface FillItem {
  item?: SessionItem;
  errors: SessionError[];
}
export function useFillItem(id: string | undefined): FillItem {
  const session = useFillSession();

  const initialValue = id ? session.getItem(id) : undefined;
  const [item, setItem] = useState<SessionItem | undefined>(initialValue);

  const initialErrors = id ? session.getItemErrors(id) || [] : [];
  const [errors, setErrors] = useState<SessionError[]>(initialErrors);
  const prevErrors = useRef(initialErrors);

  // This is required because if a child of this item is shown or hidden, we need to update this item
  const [availableItems, setAvailableItems] = useState(() => getAvailableItems([]));
  const prevAvailableItems = useRef(availableItems);

  function getAvailableItems(prevValue: boolean[]): boolean[] {
    if(!id) {
      return prevValue.length > 0 ? [] : prevValue;
    }

    const item = session.getItem(id);
    if(!item || !item.items) {
      return prevValue.length > 0 ? [] : prevValue;
    }

    let isSame = true;
    const newValue = item.items.map((itemId, index) => {
      const visible = session.getItem(itemId) !== undefined;
      if(isSame && prevValue[index] !== visible) {
        isSame = false;
      }
      return visible;
    });

    if(isSame) return prevValue;
    return newValue;
  };


  useEffect(() => {
    if(!id) return;

    const updateItem = () => {
      setItem(session.getItem(id));
    }

    const updateErrors = () => {
      const newErrors = session.getItemErrors(id);
      if(newErrors) {
        setErrors(newErrors);
        prevErrors.current = newErrors;
      } else if(prevErrors.current.length > 0) {
        const empty: typeof errors = [];
        setErrors(empty);
        prevErrors.current = empty;
      }
    }

    const updateAvailableItems = () => {
      const newItems = getAvailableItems(prevAvailableItems.current);
      prevAvailableItems.current = newItems;
      setAvailableItems(newItems);
    }

    const listener = () => {
      updateItem();
      updateErrors();
      updateAvailableItems();
    };
    session.on('update', listener);
    listener();

    return () => {
      session.removeListener('update', listener);
    }
  }, [session, id, prevErrors, prevAvailableItems, setItem, setErrors, setAvailableItems]);

  return { item, errors };
}
