import React, { useEffect, useMemo, useState } from 'react'
import EditIcon from '@mui/icons-material/Edit';
import CloseIcon from '@mui/icons-material/Close';
import { IconButton, SvgIcon, TableCell, TableRow, Tooltip } from '@mui/material';
import { checkHttpResponse, handleRejection } from '../middleware/checkHttpResponse';
import { DEFAULT_CONFIGURATION_FILTERS, FormConfiguration, FormConfigurationFilters, FormConfigurationTag, LabelAction } from '../types';
import { editAdminFormConfiguration, getAdminFormConfigurationTags } from '../backend';
import { useIntl } from 'react-intl';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { dateOptions } from '../util/constants';
import { downloadAsJSON, extractDate } from '../util/helperFunctions';
import DownloadIcon from '@mui/icons-material/Download';
import { DialobAdminConfig } from '..';
import { LabelChips } from './LabelChips';

interface TagTableRowProps {
	filters: FormConfigurationFilters;
	formConfiguration: FormConfiguration;
	deleteFormConfiguration: (formConfiguration: FormConfiguration) => void;
	copyFormConfiguration: (formConfiguration: FormConfiguration) => void;
	config: DialobAdminConfig;
	getDialobForm: (formName: string) => Promise<any>;
}

export const TagTableRow: React.FC<TagTableRowProps> = ({
	filters,
	formConfiguration,
	copyFormConfiguration,
	deleteFormConfiguration,
	config,
	getDialobForm
}) => {
	const [tags, setTags] = useState<FormConfigurationTag[]>([]);
	const [dialobForm, setDialobForm] = useState<any>(undefined);
	const [fetchAgain, setFetchAgain] = useState<boolean>(false);
	const intl = useIntl();

	useEffect(() => {
		const fetchForm = async () => {
			try {
				const form = await getDialobForm(formConfiguration.id);
				setDialobForm(form);
			} catch (error) {
				console.error("Error fetching the form:", error);
			}
		};

		fetchForm();
	}, [formConfiguration.id, fetchAgain]);

	useEffect(() => {
		getAdminFormConfigurationTags(config, formConfiguration.id)
			.then((response: Response) => checkHttpResponse(response, config.setLoginRequired))
			.then((response: { json: () => any; }) => response.json())
			.then((tags: any) => {
				setTags(tags?.map((tag: any) => {
					return {
						latestTagDate: tag.created,
						latestTagName: tag.name
					}
				}))
			})
			.catch((ex: any) => {
				handleRejection(ex, config.setTechnicalError);
			});
	},
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[])

	const latestTag: FormConfigurationTag | undefined = useMemo(() => {
		if (tags.length === 0) {
			return undefined;
		} else {
			tags.sort((a, b) => { return new Date(b.latestTagDate).getTime() - new Date(a.latestTagDate).getTime() });
			return tags[0];
		}
	}, [tags])

	const filteredRow: FormConfigurationFilters | undefined = useMemo(() => {
		const result: FormConfigurationFilters = {
			...DEFAULT_CONFIGURATION_FILTERS,
			lastSaved: formConfiguration.metadata.lastSaved,
			label: formConfiguration.metadata.label || "",
			latestTagName: latestTag?.latestTagName || "",
			latestTagDate: latestTag?.latestTagDate,
			labels: ""
		};

		if (filters.label && !result.label?.toLowerCase().includes(filters.label.toLowerCase())) {
			return undefined;
		}

		if (filters.latestTagName && !result.latestTagName?.toLowerCase().includes(filters.latestTagName.toLowerCase())) {
			return undefined;
		}

		if (filters.latestTagDate) {
			if (!latestTag) {
				return undefined;
			} else {
				const filterDate = extractDate(filters.latestTagDate);
				const tagDate = extractDate(latestTag?.latestTagDate);
				if (filterDate && tagDate && filterDate.getTime() !== tagDate.getTime()) {
					return undefined;
				}
			}
		}

		if (filters.lastSaved) {
			const filterDate = extractDate(filters.lastSaved);
			const savedDate = extractDate(formConfiguration.metadata.lastSaved);
			if (filterDate && savedDate && filterDate.getTime() !== savedDate.getTime()) {
				return undefined;
			}
		}

		if (filters.labels) {
			let exists = false;
			if (dialobForm?.metadata.labels) {
				dialobForm.metadata.labels.forEach((label: any) => {
					if (label.toLowerCase().includes(filters.labels!.toLowerCase())) {
						exists = true;
					}
				})
			}
			if (!exists) {
				return undefined;
			}
		}

		return result;
	}, [filters, formConfiguration.metadata.label, formConfiguration.metadata.lastSaved, latestTag, dialobForm]);

	const downloadFormConfiguration = () => {
		downloadAsJSON(dialobForm);
	}

	const updateLabels = async (label: any, action: LabelAction) => {
		try {
			const updatedLabels =
				action === LabelAction.DELETE
					? dialobForm.metadata.labels.filter((l: any) => l !== label)
					: [...(dialobForm.metadata.labels || []), label];

			const json = {
				...dialobForm,
				metadata: {
					...dialobForm.metadata,
					labels: updatedLabels,
				},
			};

			delete json._id;
			delete json._rev;

			const response = await editAdminFormConfiguration(json, config);
			await checkHttpResponse(response, config.setLoginRequired);
			await response.json();
			setFetchAgain((prevState) => !prevState);
		} catch (ex: any) {
			handleRejection(ex, config.setTechnicalError);
		}
	};

	return (
		<>
			{filteredRow && (
				<TableRow>
					<TableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.edit" })} placement='top-end' arrow>
							<IconButton
								onClick={function (e: any) {
									e.preventDefault();
									window.location.replace(`${config.dialobApiUrl}/composer/${formConfiguration.id}`);
								}}
							>
								<SvgIcon fontSize="small"><EditIcon /></SvgIcon>
							</IconButton>
						</Tooltip>
					</TableCell>
					<TableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.copy" })} placement='top-end' arrow>
							<IconButton
								onClick={function (e: any) {
									e.preventDefault();
									copyFormConfiguration(formConfiguration)
								}}
							>
								<SvgIcon fontSize="small"><ContentCopyIcon /></SvgIcon>
							</IconButton>
						</Tooltip>
					</TableCell>
					<TableCell>{formConfiguration.metadata.label || intl.formatMessage({ id: "adminUI.dialog.emptyTitle" })}</TableCell>
					<TableCell>{latestTag?.latestTagName}</TableCell>
					<TableCell>{latestTag && new Intl.DateTimeFormat(config.language, dateOptions).format(new Date(latestTag.latestTagDate))}</TableCell>
					<TableCell>{new Intl.DateTimeFormat(config.language, dateOptions).format(new Date(formConfiguration.metadata.lastSaved))}</TableCell>
					<TableCell>
						<LabelChips labels={dialobForm?.metadata.labels} onUpdate={updateLabels} />
					</TableCell>
					<TableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.delete" })} placement='top-end' arrow>
							<IconButton
								onClick={function (e: any) {
									e.preventDefault();
									deleteFormConfiguration(formConfiguration);
								}}
								color='error'
							>
								<SvgIcon fontSize="small"><CloseIcon /></SvgIcon>
							</IconButton>
						</Tooltip>
					</TableCell>
					<TableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "download" })} placement='top-end' arrow>
							<IconButton
								onClick={function (e: any) {
									e.preventDefault();
									downloadFormConfiguration();
								}}
							>
								<SvgIcon fontSize="small"><DownloadIcon /></SvgIcon>
							</IconButton>
						</Tooltip>
					</TableCell>
				</TableRow>
			)}
		</>
	);
}
