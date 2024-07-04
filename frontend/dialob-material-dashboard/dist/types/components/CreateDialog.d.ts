import React from 'react';
import { FormConfiguration } from '../types';
import { DialobAdminConfig } from '..';
interface CreateDialogProps {
    createModalOpen: boolean;
    handleCreateModalClose: () => void;
    formConfiguration?: FormConfiguration;
    setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
    config: DialobAdminConfig;
}
export declare const CreateDialog: React.FC<CreateDialogProps>;
export {};
