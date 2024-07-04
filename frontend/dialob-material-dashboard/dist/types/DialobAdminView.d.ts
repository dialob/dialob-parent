import React from 'react';
import { DialobAdminConfig } from './index';
interface DialobAdminViewProps {
    config: DialobAdminConfig;
    showSnackbar?: (message: string, severity: 'success' | 'error') => void;
}
export declare const DialobAdminView: React.FC<DialobAdminViewProps>;
export {};
