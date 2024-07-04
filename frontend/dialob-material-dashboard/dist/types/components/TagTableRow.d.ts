import React from 'react';
import { FormConfiguration, FormConfigurationFilters } from '../types';
import { DialobAdminConfig } from '..';
interface TagTableRowProps {
    filters: FormConfigurationFilters;
    formConfiguration: FormConfiguration;
    deleteFormConfiguration: (formConfiguration: FormConfiguration) => void;
    copyFormConfiguration: (formConfiguration: FormConfiguration) => void;
    dialobForm: any;
    config: DialobAdminConfig;
}
export declare const TagTableRow: React.FC<TagTableRowProps>;
export {};
