# Dialob API Service

API Service component for Dialob ( https://github.com/dialob/dialob-parent )

## HTTP Port

**8081**

## Configuration options

- SPRING_DATA_REDIS_HOST=redis
- SPRING_PROFILES_ACTIVE=ui,jdbc
- SPRING_SESSION_STORE_TYPE=redis
- SPRING_SESSION_REDIS_NAMESPACE=dialobServiceSession
- SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql/dialob
- SPRING_DATASOURCE_USERNAME=dialob
- SPRING_DATASOURCE_PASSWORD=dialob123
- ADMIN_VERSIONING=true
- DIALOB_SESSION_POSTSUBMITHANDLER_ENABLED=true
- DIALOB_SECURITY_ENABLED=false
- SERVER_SERVLET_CONTEXTPATH=/dialob
- LOGGING_LEVEL_IO_DIALOB=DEBUG
- DIALOB_TENANT_MODE=FIXED
- DIALOB_TENANT_FIXED_ID=''

---

https://dialob.io
