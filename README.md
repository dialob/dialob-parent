# Dialog Composer Reference Application

Reference implementation of an application for Dialob Composer

Dialob backend requirement **2.x**

Uses `@resys/dialob-composer` as implementation. ( https://git.resys.io/dialob/dialob-composer-v2 )

## CI Build

Builds automatically on push and deploys to S3, controlled by `Jenkinsfile`

https://ci.resys.io/job/dialob/job/dialob-composer-generic-app/

## Deployment 

CI Build deploys runtime artifacts to S3 bucket `https://s3.eu-central-1.amazonaws.com/cdn.resys.io/dialob-composer-generic-app/master` 

Backend condfiguratoin for S3 CDN UI:

```yaml
dialob:
  tenantuis:
    composer:
      tenants:
        xxx: -- or default:
          template: "https://s3.eu-central-1.amazonaws.com/cdn.resys.io/dialob-composer-generic-app/master/index.html"
```

## Build and Run

This is CRA application.

Build: `yarn build` \
Run: `yarn start` 

## Configuration

```javascript
const DIALOB_COMPOSER_CONFIG = {
  transport: {
    csrf: {
      headerName: window.COMPOSER_CONFIG.csrfHeader,
      token: window.COMPOSER_CONFIG.csrf
    },
    apiUrl: window.COMPOSER_CONFIG.backend_api_url,
    previewUrl: window.COMPOSER_CONFIG.filling_app_url,
    tenantId: window.COMPOSER_CONFIG.tenantId,
  },
  itemEditors: DEFAULT_ITEM_CONFIG,
  itemTypes: DEFAULT_ITEMTYPE_CONFIG,
  postAddItem: (dispatch, action, lastItem) => {},
  closeHandler : () => {}
};
```

* **transport** - Transport configuration, CSRF header, token etc.
* **apiUrl** - URL for Dialob backend service API
* **previewUrl** - (Optional) URL for Dialob Filling preview application. If omitted, "Preview" feature is disabled.  `/<sessionId>` is appended to the URL for preview.
* **itemEditors** - Configuration for item editors
* **itemTypes** - Configuration for item types
* **closeHandler** - JS function that is called when toolbar `X` button is clicked.
* **postAddItem** - (Optional) callback function that gets called after a new item gets added to a form. Arguments: `dispatch` - Redux dispatch for dispatching additional actions into composer state, `action`- The Redux action that was used for creating the item, `lastItem` - The item that was added (including ID). Use this, for example, to create addtitional form structure depending on the created item, communicate to other parts of application etc.

### Item type configuration

### Item editor configuration

