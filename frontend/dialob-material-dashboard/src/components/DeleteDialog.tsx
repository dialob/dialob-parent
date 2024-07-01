import React from 'react'
import { FormattedMessage, useIntl } from 'react-intl';
import { DialogContent, DialogTitle, Box, Divider, Typography, DialogActions, Theme } from '@mui/material';
import { StyledDialog, StyledModalTypography, StyledTagEditDialogButton } from '../style';
import { deleteAdminFormConfiguration } from '../backend';
import { checkHttpResponse, handleRejection } from '../middleware/checkHttpResponse';
import { FormConfiguration } from '../types';
import { DialobAdminConfig } from '..';

interface DeleteDialogProps {
  deleteModalOpen: boolean;
  handleDeleteModalClose: () => void;
  formConfiguration: FormConfiguration | undefined;
  setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
	config: DialobAdminConfig;
}

export const DeleteDialog: React.FC<DeleteDialogProps> = ({
  setFetchAgain,
  deleteModalOpen,
  handleDeleteModalClose,
  formConfiguration,
	config
}) => {
  const intl = useIntl();

  const deleteDialog = async () => {
    deleteAdminFormConfiguration(formConfiguration?.id!, config)
        .then((response: Response) => checkHttpResponse(response, config.setLoginRequired))
        .then((response: { json: () => any; }) => response.json())
        .then((response: any) => {
            handleDeleteModalClose();
            setFetchAgain(prevState => !prevState);
        })
        .catch((ex: any) => {handleRejection(ex, config.setTechnicalError);
    });
  };

  return (
    <Box>
      <StyledDialog
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        maxWidth={'lg'}
      >
        <DialogTitle sx={{ m: 0, p: 2 }}>
            <StyledModalTypography><FormattedMessage id='heading.deleteDialog' /></StyledModalTypography>
        </DialogTitle>
        <Divider />
        <DialogContent>
            <Typography sx={{padding: "20px 4px 4px 2px"}}><FormattedMessage id="adminUI.dialog.deleteQuestion"/> {`"${formConfiguration?.metadata.label || intl.formatMessage({id: "adminUI.dialog.emptyTitle"})}"?`}</Typography>
        </DialogContent>
        <Divider />
        <DialogActions sx={{display: "flex", justifyContent: "space-between", padding: "12px"}}>
            <StyledTagEditDialogButton onClick={handleDeleteModalClose}><FormattedMessage id={'button.cancel'} /></StyledTagEditDialogButton>
            <StyledTagEditDialogButton 
                sx={{backgroundColor: (theme: Theme) => theme.palette.error.main, color: (theme: Theme) => theme.palette.common.white}} 
                onClick={() => deleteDialog()}
            >
                <FormattedMessage id={'button.accept'} />
            </StyledTagEditDialogButton>
        </DialogActions>
      </StyledDialog>
    </Box>
  )
}
