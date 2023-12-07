import { ItemAction } from '@dialob/fill-api';
import React from 'react';

export interface RowGroupContextType {
  rowGroup?: ItemAction<'rowgroup'>['item'];
}

const context = React.createContext<RowGroupContextType>({rowGroup: undefined});
export const RowGroupContext = context;
