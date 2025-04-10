# Dialob Composer Custom Configuration Demo App

This is a demo app to show how to use custom configuration for Dialob Composer and integrate the library into a target application. Since the library is not published yet, it is included as a local package via yalc.

## Main steps

1. Import the library `@dialob/composer-material`
2. Create a function to render the Dialob Composer component with custom configuration

The function should import `DialobComposer` component from the library and render it into the target element. The function should be called with the target element and the configuration object as arguments.

Within the method, an instance of `BackendTypes.DialobComposerConfig` should be created by reading the configuration object and passing it to the `DialobComposer` component, along with the form id.

Customization is done by setting `itemEditors` (of type `DefaultTypes.ItemConfig`) and `itemTypes` (of type `DefaultTypes.ItemTypeConfig`) properties in `DialobComposerConfig` object. 

```typescript
const renderDialobComposer = (targetElement: HTMLElement, appConfig: BackendTypes.AppConfig) => {

  const FORM_ID = appConfig.formId;

  const baseUrl = window.location.origin;

  const DIALOB_COMPOSER_CONFIG: BackendTypes.DialobComposerConfig = {
    transport: {
      csrf: appConfig.csrfHeader ? {
        headerName: appConfig.csrfHeader,
        token: appConfig.csrf
      } : undefined,
      apiUrl: appConfig.backend_api_url.includes('://') ? appConfig.backend_api_url : baseUrl + appConfig.backend_api_url,
      previewUrl: appConfig.filling_app_url,
      tenantId: appConfig.tenantId || undefined,
      credentialMode: appConfig.credentialMode || undefined,
    },
    documentationUrl: 'https://github.com/dialob/dialob-parent/wiki/',
    itemEditors: ITEM_EDITORS,
    itemTypes: ITEMTYPE_CONFIG,
    backendVersion: appConfig.version,
    closeHandler: () => window.location.href = appConfig.adminAppUrl,
  };

  ReactDOM.createRoot(targetElement!).render(
    <React.StrictMode>
      <ThemeProvider theme={siteTheme}>
        <CssBaseline />
        <DialobComposer config={DIALOB_COMPOSER_CONFIG} formId={FORM_ID} />
      </ThemeProvider>
    </React.StrictMode>,
  )
};
```

3. Call the function with the target element and the configuration object.

In the demo app, the function is called in the `index.html` file with the target element and a custom configuration object.

```typescript
    <script th:if="${appConfig}" th:inline="javascript" defer>
      var appConfig = /*[[${appConfig}]]*/ {};
      
      var apiUrlOverride = "%VITE_DIALOB_API_URL%";
  
      if (appConfig.formId === undefined && !apiUrlOverride.startsWith("%")) {
        console.log("LocalMode", apiUrlOverride, appConfig);
        const urlParams = new URLSearchParams(window.location.search);
        const formId = urlParams.get("id");
        
        console.log("Local Form ID: ", formId);
        
        appConfig = {
          backend_api_url: apiUrlOverride,
          formId,
          filling_app_url: 'http://localhost:3001/#',
          credentialMode: 'omit',
          adminAppUrl: 'http://localhost:3003/#',
          version: '0.0.0-local'
        };
      }
      
      // Wait for deferred module load
      function checkRender() {
        if (window.renderDialobComposer === undefined) {
          console.log("Check...");
          window.setTimeout(checkRender, 1000);
        } else {
          window.renderDialobComposer(document.getElementById("root"), appConfig);
        }
      }
          
      checkRender();
    </script>
```


