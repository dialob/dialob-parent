import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import {
  Box, TableContainer, Typography, Table, TableRow, TableHead, TableBody,
  Tooltip, IconButton, SvgIcon, OutlinedInput, TableCell, Button
} from '@mui/material';
import { checkHttpResponse, checkSearchHttpResponse, handleRejection } from './middleware';
import { FormattedMessage, useIntl } from 'react-intl';
import type { FormConfiguration, FormConfigurationFilters, FormTag, Metadata, DialobAdminViewProps } from './types';
import { DEFAULT_CONFIGURATION_FILTERS, downloadAsJSON } from './util';
import { useAdminBackend } from './backend';
import { SortField, CustomDatePicker, Spinner, CreateDialog, DeleteDialog, TagTableRow } from './components';
import AddIcon from '@mui/icons-material/Add';
import DownloadIcon from '@mui/icons-material/Download';
import FileUploadIcon from '@mui/icons-material/FileUpload';

const CSV_PARSING_ERROR = "CSV_PARSING_ERROR";

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
  const intl = useIntl();
  const fileInputRefJson = useRef<HTMLInputElement | null>(null);
  const fileInputRefCsv = useRef<HTMLInputElement | null>(null);

  const {
    addAdminFormConfiguration, addAdminFormConfigurationFromCsv, editAdminFormConfiguration,
    getAdminFormAllTags, getAdminFormConfiguration, getAdminFormConfigurationList
  } = useAdminBackend(config);

  const handleJsonUploadClick = () => {
    if (fileInputRefJson.current) {
      fileInputRefJson.current.click();
    }
  };

  const handleCsvUploadClick = () => {
    if (fileInputRefCsv.current) {
      fileInputRefCsv.current.click();
    }
  };

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
        const response = await getAdminFormConfigurationList()
          .then((response: Response) => checkHttpResponse(response, config.setLoginRequired));

        const data = await response.json();
        const tagsResponse = await getAdminFormAllTags()
          .then((response: Response) => checkSearchHttpResponse(response, config.setLoginRequired));
        const allTags: FormTag[] | undefined = await tagsResponse.json();

        const enrichedConfigurations =
          data.map((formConfiguration: FormConfiguration) => {
            const latestTag = findLatestTag(allTags, formConfiguration.id);
            if (latestTag) {
              return {
                ...formConfiguration,
                latestTagName: latestTag.name,
                latestTagDate: latestTag.created,
              };
            } else {
              return formConfiguration;
            }
          }
          );

        setFormConfigurations(enrichedConfigurations);
      } catch (error) {
        handleRejection(error, config.setTechnicalError);
      }
    };

    fetchFormConfigurations();
  }, [fetchAgain]);

  const findLatestTag = (allTags: FormTag[] | undefined, formId: string): FormTag | undefined => {
    let latestTag: FormTag | undefined = undefined;
    allTags?.forEach((current) => {
      if (current.formName == formId) {
        if (!latestTag) {
          latestTag = current;
        }
        else if (current.created > latestTag.created) {
          latestTag = current;
        }
      }
    });
    return latestTag;
  }

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
      const response = await getAdminFormConfiguration(formName);
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

  const uploadJsonDialogForm = (e: any) => {
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
              ? editAdminFormConfiguration(json)
              : addAdminFormConfiguration(json);

            try {
              const response = await uploadPromise;
              await checkHttpResponse(response, config.setLoginRequired);
              await response.json();
              if (showNotification) {
                showNotification(`Uploaded ${formNamesList.includes(json.name) ? 'an existing' : 'a new'} form successfully.`, 'success');
              }
              setFetchAgain((prevState) => !prevState);
            } catch (ex) {
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

    if (fileInputRefJson.current) {
      fileInputRefJson.current.value = '';
    }
  };

  const uploadCsvDialogForm = (e: any) => {
    const file = e.target.files[0];

    const handleFileRead = async (event: ProgressEvent<FileReader>) => {
      const result = event.target?.result;
      let response;
      if (typeof result === 'string') {
        try {
          response = await addAdminFormConfigurationFromCsv(result);
          await checkHttpResponse(response, config.setLoginRequired);

          const responseData = await response.json();
          if (showNotification) {
            showNotification(`Uploaded CSV form successfully. ID: ${responseData?.id}`, 'success');
          }
          setFetchAgain((prev) => !prev);
        } catch (ex) {
          if (response) {
            const responseData = await response.json();
            const errorMessage = responseData?.error === CSV_PARSING_ERROR
              ? responseData?.reason
              : responseData?.message;

            if (errorMessage) {
              console.error(`Error while uploading a new form from CSV: ${errorMessage}`);
              if (showNotification) {
                showNotification(`Error while uploading a new form from CSV: ${errorMessage}`, 'error');
              }
            }
          } else {
            console.error(`Error while uploading a new form from CSV: ${ex}`);
            if (showNotification) {
              showNotification(`Error while uploading a new form from CSV: ${ex}`, 'error');
            }
          }
          handleRejection(ex, config.setTechnicalError);
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

    if (fileInputRefCsv.current) {
      fileInputRefCsv.current.value = '';
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
    <>
      {formConfigurations ? (
        <Box>
          <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 3 }}>
            <Typography variant='h2'><FormattedMessage id={'adminUI.dialog.heading'} /></Typography>
            <Box display="flex" gap={1}>
              <Button
                onClick={function (e) {
                  e.preventDefault();
                  addFormConfiguration();
                }}
              >
                <SvgIcon fontSize="medium"><AddIcon /></SvgIcon>
              </Button>
              <Button onClick={handleJsonUploadClick}>
                <SvgIcon fontSize="small" >
                  <FileUploadIcon />
                </SvgIcon>
                {intl.formatMessage({ id: "upload.json" })}
              </Button>
              <input
                ref={fileInputRefJson}
                type='file'
                accept='.json'
                hidden
                onChange={(e) => uploadJsonDialogForm(e)}
              />
              <Button onClick={handleCsvUploadClick}>
                <SvgIcon fontSize="small">
                  <FileUploadIcon />
                </SvgIcon>
                {intl.formatMessage({ id: "upload.csv" })}
              </Button>
              <input
                ref={fileInputRefCsv}
                type='file'
                accept='.csv'
                hidden
                onChange={(e) => uploadCsvDialogForm(e)}
              />
            </Box>
          </Box>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell width="22%">
                    <SortField
                      active={sortConfig.field === 'label'}
                      direction={sortConfig.field === 'label' ? sortConfig.direction : 'asc'}
                      name="label"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="22%">
                    <SortField
                      active={sortConfig.field === 'latestTagName'}
                      direction={sortConfig.field === 'latestTagName' ? sortConfig.direction : 'asc'}
                      name="latestTagName"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="17%" sx={{ minWidth: "220px" }}>
                    <SortField
                      active={sortConfig.field === 'latestTagDate'}
                      direction={sortConfig.field === 'latestTagDate' ? sortConfig.direction : 'asc'}
                      name="latestTagDate"
                      handleSort={handleSort}
                    />
                  </TableCell>
                  <TableCell width="17%" sx={{ minWidth: "220px" }}>
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
                  <TableCell width="4%" sx={{ textAlign: "right" }}>
                    <Tooltip title={intl.formatMessage({ id: "download.all" })} placement='top-end' arrow>
                      <IconButton
                        onClick={function (e) {
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
    </>
  );
}
