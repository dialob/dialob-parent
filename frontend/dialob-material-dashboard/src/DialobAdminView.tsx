import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import AddIcon from '@mui/icons-material/Add';
import {
  Box, TableContainer, Typography, Table, TableRow, TableHead, TableBody,
  Tooltip, IconButton, SvgIcon, OutlinedInput, TableCell, Button
} from '@mui/material';
import { Spinner } from './components/Spinner';
import { checkHttpResponse, handleRejection } from './middleware/checkHttpResponse';
import { DEFAULT_CONFIGURATION_FILTERS, FormConfiguration, FormConfigurationFilters, Metadata } from './types';
import {
  addAdminFormConfiguration, editAdminFormConfiguration, getAdminFormConfiguration,
  getAdminFormConfigurationList, getAdminFormConfigurationTags
} from './backend';
import { FormattedMessage, useIntl } from 'react-intl';
import { CreateDialog } from './components/CreateDialog';
import { DeleteDialog } from './components/DeleteDialog';
import { TagTableRow } from './components/TagTableRow';
import DownloadIcon from '@mui/icons-material/Download';
import { downloadAsJSON } from './util/helperFunctions';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { DialobAdminConfig } from './index';
import CustomDatePicker from './components/CustomDatePicker';
import SortField from './components/SortField';

export interface DialobAdminViewProps {
  config: DialobAdminConfig;
  showNotification?: (message: string, severity: 'success' | 'error') => void;
}

export const DialobAdminView: React.FC<DialobAdminViewProps> = ({ config, showNotification }) => {
  const [formConfigurations, setFormConfigurations] = useState<FormConfiguration[]>([]);
  const [selectedFormConfiguration, setSelectedFormConfiguration] = useState<FormConfiguration | undefined>();
  const [filters, setFilters] = useState<FormConfigurationFilters>(DEFAULT_CONFIGURATION_FILTERS);
  const [createModalOpen, setCreateModalOpen] = useState<boolean>(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const [fetchAgain, setFetchAgain] = useState<boolean>(false);
  const [sortConfig, setSortConfig] = useState<{ field: string | null; direction: 'asc' | 'desc' }>({
    field: "label",
    direction: 'asc',
  });
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const intl = useIntl();

  const handleCreateModalClose = () => {
    setSelectedFormConfiguration(undefined);
    setCreateModalOpen(false);
  }

  const handleDeleteModalClose = () => {
    setSelectedFormConfiguration(undefined);
    setDeleteModalOpen(false);
  }

  useEffect(() => {
    const fetchFormConfigurations = async () => {
      try {
        const response = await getAdminFormConfigurationList(config)
          .then((response: Response) => checkHttpResponse(response, config.setLoginRequired));

        const data = await response.json();

        const enrichedConfigurations = await Promise.all(
          data.map(async (formConfiguration: FormConfiguration) => {
            const tagsResponse = await getAdminFormConfigurationTags(config, formConfiguration.id);
            const tagsData: any = await tagsResponse.json();
            if (tagsData?.length > 0) {
              const mappedTags = tagsData?.map((tag: any) => ({
                latestTagDate: tag.created,
                latestTagName: tag.name,
              }));

              const sortedTags = [...mappedTags].sort((a, b) => new Date(b.latestTagDate).getTime() - new Date(a.latestTagDate).getTime());
              const latestTag = sortedTags[0];
              return {
                ...formConfiguration,
                latestTagName: latestTag.latestTagName,
                latestTagDate: latestTag.latestTagDate,
              };
            } else {
              return formConfiguration;
            }
          })
        );

        setFormConfigurations(enrichedConfigurations);
      } catch (error) {
        handleRejection(error, config.setTechnicalError);
      }
    };

    fetchFormConfigurations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fetchAgain]);

  const copyFormConfiguration = (formConfiguration: FormConfiguration) => {
    setSelectedFormConfiguration(formConfiguration);
    setCreateModalOpen(true);
  }

  const addFormConfiguration = () => {
    setCreateModalOpen(true);
  }

  const deleteFormConfiguration = (formConfiguration: FormConfiguration) => {
    setSelectedFormConfiguration(formConfiguration);
    setDeleteModalOpen(true);
  }

  const handleChangeInput = (e: any) => {
    setFilters(prevFilters => ({
      ...prevFilters,
      [e.target.name]: e.target.value
    }));
  }

  const handleDateChange = (name: string, date: Date | null) => {
    setFilters(prevFilters => ({
      ...prevFilters,
      [name]: date
    }));
  };

  const handleDateClear = (name: string) => {
    setFilters(prevFilters => ({
      ...prevFilters,
      [name]: null
    }));
  };

  const getDialobForm = async (formName: string) => {
    try {
      const response = await getAdminFormConfiguration(formName, config);
      await checkHttpResponse(response, config.setLoginRequired);
      const form = await response.json();
      return form;
    } catch (ex) {
      handleRejection(ex, config.setTechnicalError);
    }
  }

  const downloadAllFormConfigurations = useCallback(() => {
    const formNamesList = formConfigurations?.map((formConfiguration: FormConfiguration) => formConfiguration.id) || [];

    const fetchForms = async () => {
      try {
        const forms = await Promise.all(
          formNamesList.map(async (formName) => {
            const form = await getDialobForm(formName);
            return form;
          })
        );
        downloadAsJSON(forms);
      } catch (error) {
        console.error("Error fetching forms:", error);
      }
    };

    fetchForms();
  }, [formConfigurations]);

  const handleUploadClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const uploadDialogForm = (e: any) => {
    const file = e.target.files[0];

    const handleFileRead = async (event: ProgressEvent<FileReader>) => {
      const result = event.target?.result;
      if (typeof result === 'string') {
        try {
          const json = JSON.parse(result);
          if (!Array.isArray(json)) {
            const formNamesList = formConfigurations?.map((formConfiguration: FormConfiguration) => formConfiguration.id) || [];
            delete json._id;
            delete json._rev;

            const uploadPromise = formNamesList.includes(json.name)
              ? editAdminFormConfiguration(json, config)
              : addAdminFormConfiguration(json, config);

            try {
              const response = await uploadPromise;
              await checkHttpResponse(response, config.setLoginRequired);
              await response.json();
              if (showNotification) {
                showNotification(`Uploaded ${formNamesList.includes(json.name) ? 'an existing' : 'a new'} form successfully.`, 'success');
              }
              setFetchAgain((prevState) => !prevState);
            } catch (ex: any) {
              if (showNotification) {
                showNotification(`Error while uploading ${formNamesList.includes(json.name) ? 'an existing' : 'a new'} form: ${ex}`, 'error');
              }
              handleRejection(ex, config.setTechnicalError);
            }
          } else {
            if (showNotification) {
              showNotification(`JSON needs to contain an object, not an array.`, 'error');
            }
          }
        } catch (error: any) {
          if (showNotification) {
            showNotification(`Error parsing JSON: ${error.message}`, 'error');
          }
        }
      }
    };

    const handleFileError = () => {
      if (showNotification) {
        showNotification(`Error reading file.`, 'error');
      }
    };

    if (file) {
      const reader = new FileReader();
      reader.onload = handleFileRead;
      reader.onerror = handleFileError;
      reader.readAsText(file);
    }

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleSort = (field: string) => {
    setSortConfig((prevConfig) => {
      if (prevConfig.field === field) {
        return {
          field,
          direction: prevConfig.direction === 'asc' ? 'desc' : 'asc',
        };
      } else {
        return { field, direction: 'asc' };
      }
    });
  };

  const sortedFormConfigurations = useMemo(() => {
    if (!sortConfig.field) return formConfigurations;
    let aValue: any = undefined;
    let bValue: any = undefined;

    return [...formConfigurations].sort((a, b) => {
      if (sortConfig.field === 'latestTagDate' || sortConfig.field === 'latestTagName') {
        aValue = a[sortConfig.field as keyof FormConfiguration];
        bValue = b[sortConfig.field as keyof FormConfiguration];
      } else {
        aValue = a.metadata[sortConfig.field as keyof Metadata];
        bValue = b.metadata[sortConfig.field as keyof Metadata];
      }

      if (sortConfig.field === 'latestTagDate' || sortConfig.field === 'lastSaved') {
        const aDate = aValue ? new Date(aValue as string).getTime() : 0;
        const bDate = bValue ? new Date(bValue as string).getTime() : 0;
        return sortConfig.direction === 'asc' ? aDate - bDate : bDate - aDate;
      } else {
        const aStr = (aValue || '').toString().toLowerCase();
        const bStr = (bValue || '').toString().toLowerCase();
        return sortConfig.direction === 'asc'
          ? aStr.localeCompare(bStr)
          : bStr.localeCompare(aStr);
      }
    });
  }, [formConfigurations, sortConfig]);

  return (
    <Box pt={6}>
      {formConfigurations ? (
        <Box sx={{ padding: "0 50px" }}>
          <Box sx={{ display: "flex", justifyContent: "space-between" }}>
            <Typography sx={{ mb: 4 }} variant='h2'><FormattedMessage id={'adminUI.dialog.heading'} /></Typography>
            <Box>
              <Tooltip title={intl.formatMessage({ id: "upload" })} placement='top-end' arrow>
                <Button onClick={handleUploadClick} sx={{ width: '50px', height: '50px' }}>
                  <SvgIcon fontSize="small" >
                    <FileUploadIcon />
                  </SvgIcon>
                </Button>
              </Tooltip>
              <input
                ref={fileInputRef}
                type='file'
                accept='.json'
                hidden
                onChange={(e) => uploadDialogForm(e)}
              />
            </Box>
          </Box>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell width="3%" sx={{ textAlign: "center" }}>
                    <Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.add" })} placement='top-end' arrow>
                      <IconButton
                        onClick={function (e: any) {
                          e.preventDefault();
                          addFormConfiguration();
                        }}
                      >
                        <SvgIcon fontSize="medium"><AddIcon /></SvgIcon>
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                  <TableCell width="3%"></TableCell>
                  <TableCell width="20%">
                    <SortField
                      active={sortConfig.field === 'label'}
                      direction={sortConfig.field === 'label' ? sortConfig.direction : 'asc'}
                      name="label"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="20%">
                    <SortField
                      active={sortConfig.field === 'latestTagName'}
                      direction={sortConfig.field === 'latestTagName' ? sortConfig.direction : 'asc'}
                      name="latestTagName"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="15%" sx={{ minWidth: "220px" }}>
                    <SortField
                      active={sortConfig.field === 'latestTagDate'}
                      direction={sortConfig.field === 'latestTagDate' ? sortConfig.direction : 'asc'}
                      name="latestTagDate"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="15%" sx={{ minWidth: "220px" }}>
                    <SortField
                      active={sortConfig.field === 'lastSaved'}
                      direction={sortConfig.field === 'lastSaved' ? sortConfig.direction : 'asc'}
                      name="lastSaved"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="18%">
                    <FormattedMessage id={"adminUI.formConfiguration.labels"} />
                  </TableCell>
                  <TableCell width="3%" />
                  <TableCell width="3%" sx={{ textAlign: "center" }}>
                    <Tooltip title={intl.formatMessage({ id: "download.all" })} placement='top-end' arrow>
                      <IconButton
                        onClick={function (e: any) {
                          e.preventDefault();
                          downloadAllFormConfigurations();
                        }}
                      >
                        <SvgIcon fontSize="small"><DownloadIcon /></SvgIcon>
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                <TableRow>
                  <TableCell />
                  <TableCell />
                  <TableCell>
                    <OutlinedInput
                      sx={{ height: '40px' }}
                      name='label'
                      onChange={handleChangeInput}
                      value={filters.label}
                    />
                  </TableCell>
                  <TableCell>
                    <OutlinedInput
                      sx={{ height: '40px' }}
                      name='latestTagName'
                      onChange={handleChangeInput}
                      value={filters.latestTagName}
                    />
                  </TableCell>
                  <TableCell>
                    <CustomDatePicker
                      value={filters.latestTagDate}
                      onChange={(date) => handleDateChange('latestTagDate', date)}
                      handleDateClear={() => handleDateClear('latestTagDate')}
                    />
                  </TableCell>
                  <TableCell>
                    <CustomDatePicker
                      value={filters.lastSaved}
                      onChange={(date) => handleDateChange('lastSaved', date)}
                      handleDateClear={() => handleDateClear('lastSaved')}
                    />
                  </TableCell>
                  <TableCell>
                    <OutlinedInput
                      sx={{ height: '40px' }}
                      name='labels'
                      onChange={handleChangeInput}
                      value={filters.labels}
                    />
                  </TableCell>
                  <TableCell />
                  <TableCell />
                </TableRow>
                {sortedFormConfigurations.map((formConfiguration: FormConfiguration, index: number) =>
                  <TagTableRow
                    key={index}
                    filters={filters}
                    formConfiguration={formConfiguration}
                    deleteFormConfiguration={deleteFormConfiguration}
                    copyFormConfiguration={copyFormConfiguration}
                    getDialobForm={getDialobForm}
                    config={config}
                    setFetchAgain={setFetchAgain}
                  />
                )}
              </TableBody>
            </Table>
          </TableContainer>
          <CreateDialog
            createModalOpen={createModalOpen}
            handleCreateModalClose={handleCreateModalClose}
            setFetchAgain={setFetchAgain}
            formConfiguration={selectedFormConfiguration}
            config={config}
          />
          <DeleteDialog
            deleteModalOpen={deleteModalOpen}
            handleDeleteModalClose={handleDeleteModalClose}
            setFetchAgain={setFetchAgain}
            formConfiguration={selectedFormConfiguration}
            config={config}
          />
        </Box>
      ) : (
        <Spinner />
      )}
    </Box>
  );
}
