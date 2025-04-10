import React from 'react';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import { Box } from '@mui/material';
import { FormattedMessage } from 'react-intl';

interface SortFieldProps {
  active: boolean;
  direction: 'asc' | 'desc';
  name: string;
  handleSort: (field: string) => void;
}

const getArrowSx = (active: boolean) => {
  return { opacity: active ? 1 : 0.3, cursor: "pointer" }
};

const SortField: React.FC<SortFieldProps> = ({ active, direction, name, handleSort }) => {
  return (
    <Box display="flex" alignItems="center">
      <FormattedMessage id={`adminUI.formConfiguration.${name}`} />
      {direction === 'asc' ?
        <ArrowUpwardIcon fontSize="small" sx={getArrowSx(active)} onClick={() => handleSort(name)} /> :
        <ArrowDownwardIcon fontSize="small" sx={getArrowSx(active)} onClick={() => handleSort(name)} />
      }
    </Box>
  );
};

export default SortField;