
## Azure blob storage parameters

| settings                                                  | Description                                                                                          |
|-----------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| dialob.db.database-type                                   | Set this to `NONE`                                                                                   |
| dialob.formDatabase.database-type                         | `AZURE_BLOB_STORAGE`                                                                                 |
| dialob.formDatabase.azure-blob-storage.container-name     | Name of container in storage account. `azure-container`                                              |
| dialob.questionnaireDatabase.database-type                | Keep questionnaires in SQL database. `JDBC`                                                          |
| dialob.formDatabase.azure-blob-storage.prefix             | Form object's prefix. Object's name will be `<prefix>/<tenantId>/<formId><suffix>`. Default: `forms` |
| dialob.formDatabase.azure-blob-storage.suffix             | Form object's suffix. Default: null                                                                  |
| dialob.questionnaireDatabase.azure-blob-storage.prefix    | Questionnaire object's prefix. Default: `questionnaires`                                             |
| dialob.questionnaireDatabase.azure-blob-storage.suffix    | Questionnaire object's suffix. Default: ``                                                           |

