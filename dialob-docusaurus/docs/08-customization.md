---
id: 08-customization
sidebar_position: 8
title: Customization
---

Dialob offers many way for customizing both filling-side behavior, as well as composer-side setup.


### Filling-side customization

Adjusting certain properties for an item in the Composer results in customizing filling-side behavior. Properties can be edited in the item edit dialog, under the "Properties" tab.

<img width="896" alt="properties" src="https://github.com/user-attachments/assets/0eecd793-897f-4341-b16c-a5a928a2cd78" />

TODO:: provide documentation for specific properties and what each does

---

### Composer-side customization

While setting up a client-specific Dialob application, modifications can be made to adjust which types of items (structures, inputs, outputs) will be available and, if needed, which types can be added to the base list. Additionally, the theme and the color-scheme of the Composer can be adjusted to match the client needs. 

Read more: [Developer guidelines for customization](https://github.com/dialob/dialob-parent/tree/dev/frontend/dialob-composer-material-custom-app#dialob-composer-custom-configuration-demo-app)

---

### theWrench Flow Function Integration

In environments that use both Dialob and theWrench, there is a way to add a custom function to the list of Dialob functions, which can be used to call theWrench flows directly from Dialob. This allows for seamless integration between the two platforms, enabling users to leverage the capabilities of theWrench within their Dialob forms.

To integrate theWrench flow function into Dialob, you can follow these steps:

1. In the backend of the application that uses both Dialob and theWrench, create a configuration class that defines a Bean for the custom function which takes Dialob's `FunctionRegistry` as a parameter. This Bean will be responsible for registering the custom function with the FunctionRegistry. Make sure this configuration class is included in the component scan of your Spring application so that it is picked up and the Bean is created.

    ```java
    import io.dialob.rule.parser.function.FunctionRegistry;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration(proxyBeanMethods = false)
    public class DialobFunctionAutoConfiguration {
      @Bean
      public FlowFunction flowFunction(FunctionRegistry functionRegistry) {
        return new FlowFunction(functionRegistry);
      }
    }
    ```

2. Implement the custom function. This class will contain the logic to call theWrench flow and handle the response. Make sure to implement `ApplicationContextAware` to access the Spring application context, which allows you to retrieve necessary beans such as `HdesClient` and `ProgramEnvir` that are needed for executing the flow. 

    In case they are not avaiable within the same application context, you need to expose theWrench flow executing functionality as an endpoint and call it inside the custom function. This approach might need some additional work to set up the communication between the two applications, such as configuring REST clients and handling authentication if necessary.

    Also note that the function should be annotated with `@PostConstruct` to ensure that it is registered with the FunctionRegistry after the bean is initialized, and the name of the function should be defined in the `configureDefaultFunctions` method. 

    In this example, we have a function named "strFlow" and it takes three parameters: the name of the flow to call, a map of parameters to pass to the flow, and the key for the result to retrieve from the flow's output. It returns the result as a string.

    ```java
    public class FlowFunction implements ApplicationContextAware {
      private final FunctionRegistry functionRegistry;
      private static ApplicationContext appCtx;

      public FlowFunction(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
      }

      @PostConstruct
      public void configureDefaultFunctions() {
        functionRegistry.configureFunction("strFlow", "strFlow", FlowFunction.class, false);
      }

      static void setAppCtx(ApplicationContext appCtx) {
        FlowFunction.appCtx = appCtx;
      }

      @Override
      public void setApplicationContext(ApplicationContext applicationContext) {
        setAppCtx(applicationContext);
      }

      public static String strFlow(String flowName, Map<String, Serializable> parameters, String resultKey) {
        HdesClient hdesClient = appCtx.getBean(HdesClient.class);
        ProgramEnvir programEnvir = appCtx.getBean(ProgramEnvir.class);

        try {
          FlowProgram.FlowResult result = hdesClient
            .executor(programEnvir)
            .inputMap(parameters)
            .flow(flowName)
            .andGetBody();
          Map<String, Serializable> returns = result.getReturns();

          LOGGER.debug("Flow output = {}", returns.get(resultKey));
          return Integer.toString((int) returns.get(resultKey));
        } catch (Exception e) {
          LOGGER.error("Flow exception", e);
          throw(e);
        }
      }
    }
    ```


3. Once the custom function is registered, you can use it in your Dialob forms just like any other function. For example, you can call the `strFlow` function in an expression variable to execute a flow and retrieve its result. In this case, we will use the `strFlow` function to call a flow named `DialobFlowTest` with the required parameters and output key, storing this in a variable named `message`.

    <img width="1215" alt="01" src="https://github.com/user-attachments/assets/ace98844-dad2-4c39-a598-39f0c92ffb90" />


4. The input and output parameters need to be in sync with the flow definition. In this example, we are passing a parameter named `name` whose value comes from the variable `inputName`, and we are expecting to retrieve a result with the key `result`. To verify this, you can check the flow definition in theWrench to ensure that it is set up to receive the `name` parameter and return a value with the key `result`.

    <img width="372" alt="02" src="https://github.com/user-attachments/assets/f64c64d0-6a21-4ad4-8e73-5c51f48ad809" />

    <img width="653" alt="03" src="https://github.com/user-attachments/assets/aaebfb2d-78e3-4db7-ac0f-bf85a47eb2f1" />


5. This variable can then be used in your form to display the result or to make decisions based on the flow's output. Here's an example of how you might use this in a form. A text input called `inputName` is used to capture the user's input, and the `message` variable is used to display the result from the flow in a note item.

    <img width="1175" alt="04" src="https://github.com/user-attachments/assets/a9f1ec9f-cc37-49f5-b4f1-6d2fb22f21a5" />


6. You can use visibility rules to control when the result is displayed based on the flow's output. For example, you might want to only show the note item if the flow returns a certain value, or only after certain questions have been answered. Here we define a visibility rule for the note item that checks if the `inputName` question is answered and the `message` variable is not empty, ensuring that the note is only shown after the flow has been executed and a result has been returned.

    <img width="912" alt="05" src="https://github.com/user-attachments/assets/2549ba37-018d-4c1d-9a1f-aeff89724801" />


7. Finally, make sure to test the integration to ensure that the function is working correctly and that the flow is being called as expected. This is an example on the filling side.

    <img width="929" alt="06" src="https://github.com/user-attachments/assets/994d7948-4aa9-4781-8779-e02bc985bfeb" />

This integration allows you to extend the functionality of your Dialob forms by leveraging the capabilities of theWrench flows, enabling more complex logic and interactions within your forms.

---

### AI Translation Integration

To integrate the AI translation service (LLM translator), the Dialob backend needs to have the configuration property `composer.translationServiceUrl` defined and pointing to the URL of the exact endpoint for translation, with `/api/translate` suffix. If the translation service URL is not configured, all AI-related features will be hidden from the Composer UI.

Read more: [Using the AI translation feature](03-advanced-operations#ai-translation)
