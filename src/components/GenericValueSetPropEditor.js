import React, { Component } from 'react';
import {Input} from 'semantic-ui-react';

const GenericValueSetPropEditor = ({value, onChange}) => {
  return (
    <Input transparent fluid value={value || ''} onChange={e => onChange(e.target.value)} />
  );
};

export {
  GenericValueSetPropEditor as default
}