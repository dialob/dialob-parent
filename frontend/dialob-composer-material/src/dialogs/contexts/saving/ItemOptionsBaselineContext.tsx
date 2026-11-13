import React from 'react';
import { ComposerMetadata, DialobItem, ValueSet } from '../../../types';

export type ItemOptionsBaseline = {
  item: DialobItem;
  valueSets: ValueSet[];
  composerMetadata?: ComposerMetadata;
};

export const ItemOptionsBaselineContext = React.createContext<ItemOptionsBaseline | null>(null);

export const useItemOptionsBaseline = (): ItemOptionsBaseline | null =>
  React.useContext(ItemOptionsBaselineContext);
