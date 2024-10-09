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
	setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
}

export const TagTableRow: React.FC<TagTableRowProps> = ({
	filters,
	formConfiguration,
	copyFormConfiguration,
	deleteFormConfiguration,
	config,
	getDialobForm,
	setFetchAgain
}) => {
	const [tags, setTags] = useState<FormConfigurationTag[]>([]);
	const intl = useIntl();

	useEffect(() => {
		const fetchTags = async () => {
			try {
				const response = await getAdminFormConfigurationTags(config, formConfiguration.id);
				checkHttpResponse(response, config.setLoginRequired);
				const data = await response.json();
				const mappedTags = data?.map((tag: any) => ({
					latestTagDate: tag.created,
					latestTagName: tag.name,
				}));
				setTags(mappedTags || []);
			} catch (ex) {
				handleRejection(ex, config.setTechnicalError);
			}
		};

		fetchTags();
	}, [config, formConfiguration.id]);

	const latestTag = useMemo(() => {
		if (tags.length === 0) return undefined;

		const sortedTags = [...tags].sort((a, b) => new Date(b.latestTagDate).getTime() - new Date(a.latestTagDate).getTime());
		return sortedTags[0];
	}, [tags]);

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
			if (formConfiguration.metadata.labels) {
				formConfiguration.metadata.labels.forEach((label: any) => {
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
	}, [filters, formConfiguration.metadata.label, formConfiguration.metadata.lastSaved, latestTag]);

	const downloadFormConfiguration = async () => {
		try {
			const form = await getDialobForm(formConfiguration.id);
			downloadAsJSON(form);
		} catch (error) {
			console.error("Error fetching the form:", error);
		}
	}

	const updateLabels = async (label: any, action: LabelAction) => {
		const updatedLabels =
			action === LabelAction.DELETE
				? formConfiguration.metadata.labels.filter((l: any) => l !== label)
				: [...(formConfiguration.metadata.labels || []), label];

		const form = await getDialobForm(formConfiguration.id);
		const json = {
			...form,
			metadata: {
				...form.metadata,
				labels: updatedLabels,
			},
		};
		delete json._id;
		delete json._rev;

		try {
			const response = await editAdminFormConfiguration(json, config);
			await checkHttpResponse(response, config.setLoginRequired);
			await response.json();
			setFetchAgain(prevState => !prevState);
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
						<LabelChips labels={formConfiguration.metadata.labels} onUpdate={updateLabels} />
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
