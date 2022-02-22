
CSV query API
=============

CSV Export for completed sessions.

CSV Export is using existing `/api/questionnaires` endpoint, but following HTTP request header must be present `Accept: text/csv`

Possible URL parameters:

1. Request explicit set of sessions. These sessions must all have same form revision.

    `GET /api/questionnaires?questionnaire=xxx,yyy,zzz` or `GET /api/questionnaires?questionnare=xxx&questionnaire=yyy&questionnaire=zzz` 

1. Request sessions by criteria. This applies when `questionnaire` parameter is not set.
    * `GET /api/questionnaires?formName=xxx&formTag=yyyy`
           
           or
    * `GET /api/questionnaires?formId=xxxx`
    
        Additional filters, any combination of these can be defined
    
    * `GET /api/questionnaires?from=YYYY-MM-DDThh:mm:ss`
    * `GET /api/questionnaires?to=YYYY-MM-DDThh:mm:ss`
    
        Filter by last answer timestamp. Completion timestamp will be used when it gets recorded in metadata.
        
1. General parameters
    * `language=en` - (Optional) use given language to get form labels and translate booleans, falls back to English.
