import React from 'react';
import NavigationTreeView from '../../components/tree/NavigationTreeView';
import { useComposer } from '../../dialob';

const NavigationPane: React.FC = () => {
  const { form } = useComposer();
  const hasItems = Object.values(form.data).length > 1;

  if (!hasItems) {
    return null;
  }

  return (
    <>
      <NavigationTreeView />
    </>
  );
};

export default NavigationPane;
