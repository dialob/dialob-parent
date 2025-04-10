import React from 'react'
import { FormattedMessage, useIntl } from 'react-intl';
import { DialogContent, DialogTitle, Box, TextField, Divider, Typography, FormHelperText, Button, Dialog } from '@mui/material';
import { Form, Formik } from 'formik';
import * as Yup from 'yup';
import { addAdminFormConfiguration, getAdminFormConfiguration } from '../backend';
import { checkHttpResponse, handleRejection } from '../middleware/checkHttpResponse';
import { DEFAULT_FORM, DefaultForm, FormConfiguration } from '../types';
import { DialobAdminConfig } from '..';

interface CreateDialogProps {
	createModalOpen: boolean;
	handleCreateModalClose: () => void;
	formConfiguration?: FormConfiguration;
	setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
	config: DialobAdminConfig;
}

interface RestFormConfigurationType {
	name: string | undefined;
	label: string | undefined;
}

export const CreateDialog: React.FC<CreateDialogProps> = ({
	setFetchAgain,
	createModalOpen,
	handleCreateModalClose,
	formConfiguration,
	config
}) => {
	const intl = useIntl();

	const tagFormSchema = () => Yup.object().shape({
		name: Yup.string().required(intl.formatMessage({ id: "error.valueRequired" })).matches(/^[_\-a-zA-Z\d]*$/g, intl.formatMessage({ id: "error.invalidFormName" })),
	});

	const handleSubmit = async (values: RestFormConfigurationType) => {
		const handleResponse = async (response: any) => {
			try {
				const jsonResponse = await response.json();
				setFetchAgain((prevState) => !prevState);
				handleCreateModalClose();
				return jsonResponse;
			} catch (ex) {
				handleRejection(ex, config.setTechnicalError);
				throw ex;
			}
		};

		const checkHttpResponseAsync = async (response: any, setLoginRequired: any) => {
			try {
				return await checkHttpResponse(response, setLoginRequired);
			} catch (ex) {
				handleRejection(ex, config.setTechnicalError);
				throw ex;
			}
		};

		if (formConfiguration) {    // Copy
			try {
				const response = await getAdminFormConfiguration(
					formConfiguration.id!,
					config
				);
				const checkedResponse = await checkHttpResponseAsync(response, config.setLoginRequired);
				const json = await handleResponse(checkedResponse);
				delete json._id;
				delete json._rev;
				json.name = values.name!;
				json.metadata.label = values.label || "";
				const addResponse = await addAdminFormConfiguration(json, config);
				await checkHttpResponseAsync(addResponse, config.setLoginRequired);
				await handleResponse({ json: () => json });
			} catch (ex) {
				handleRejection(ex, config.setTechnicalError);
			}
		} else {    // Create new
			const result: DefaultForm = DEFAULT_FORM;
			result.name = values.name!;
			result.metadata.label = values.label || "";
			try {
				const addResponse = await addAdminFormConfiguration(result, config);
				await checkHttpResponseAsync(addResponse, config.setLoginRequired);
				await handleResponse(addResponse);
			} catch (ex) {
				handleRejection(ex, config.setTechnicalError);
			}
		}
	};

	return (
		<Box>
			<Dialog
				sx={{    
					padding: '0 20px 20px 20px',
					border: 'none',
					height: '50%',
					top: "20%"
				}}
				open={createModalOpen}
				onClose={handleCreateModalClose}
				maxWidth={'lg'}
			>
				<DialogTitle sx={{ m: 0, py: 2, px: 4 }}>
					{formConfiguration ? (
						<Typography variant="h4"><FormattedMessage id='heading.copyDialog' /></Typography>
					) : (
						<Typography variant="h4"><FormattedMessage id='heading.addDialog' /></Typography>
					)}
				</DialogTitle>
				<Divider />
				<DialogContent>
					<Formik
						initialValues={{
							name: undefined,
							label: formConfiguration ? "Copy of " + formConfiguration.metadata.label : "New form",
						}}
						onSubmit={(values) => {
							handleSubmit(values)
						}}
						validationSchema={tagFormSchema}
					>
						{({ isSubmitting, dirty, isValid, errors, submitForm, values, setFieldValue }) => (
							<Form>
								<Box sx={{ px: 3, pt: 1, pb: 2, display: "flex", flexDirection: "column" }}>
									<Box sx={{ display: "flex", flexDirection: "column" }}>
										<Typography sx={{ my: 1, mx: 0 }}><FormattedMessage id="adminUI.dialog.formName" /></Typography>
										<TextField
											name='name'
											error={errors.name ? true : false}
											required
											onChange={e => setFieldValue('name', e.target.value)}
											value={values.name}
											sx={{ minWidth: "500px" }}
										/>
										{errors.name && <FormHelperText error={errors.name ? true : false}>{errors.name}</FormHelperText>}
										<Typography sx={{ my: 1, mx: 0 }}><FormattedMessage id="adminUI.dialog.formLabel" /></Typography>
										<TextField
											name='label'
											onChange={e => setFieldValue('label', e.target.value)}
											value={values.label}
											sx={{ minWidth: "500px" }}
										/>
									</Box>
									<Box sx={{ display: "flex", mt: 2, justifyContent: "space-between" }}>
										<Button onClick={handleCreateModalClose}><FormattedMessage id={'button.cancel'} /></Button>
										<Button onClick={submitForm} disabled={!dirty || (isSubmitting || !isValid)}><FormattedMessage id={'button.accept'} /></Button>
									</Box>
								</Box>
							</Form>
						)}
					</Formik>
				</DialogContent>
			</Dialog>
		</Box>
	)
}
