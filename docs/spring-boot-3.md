
### Changed properties

| Old name          | New name               |
|-------------------|------------------------|
| spring.redis.host | spring.data.redis.host |
| spring.redis.port | spring.data.redis.port |

Websocket URL changed

`/socket/{tenantId}/{sessionId}` -> `/socket?tenantId={tenantId}&sessionId={sessionId}`

Upgrade to Spring security 6 

https://github.com/spring-projects/spring-security/wiki/Spring-Security-6.0-Migration-Guide

Spring boot autoconfiguration change

https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#auto-configuration-files
