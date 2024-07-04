import React from 'react';
import { FormConfiguration } from '../types';
import { DialobAdminConfig } from '..';
interface DeleteDialogProps {
    deleteModalOpen: boolean;
    handleDeleteModalClose: () => void;
    formConfiguration: FormConfiguration | undefined;
    setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
    config: DialobAdminConfig;
}
export declare const DeleteDialog: React.FC<DeleteDialogProps>;
export {};
