---
id: 01-introduction
sidebar_position: 1
title: Introduction
---

# User Documentation

[Learn about the Dialob Platform and Architecture](01-introduction#about-the-platform)  
[Read about Dialob features](01-introduction#dialob-features)  


---

## About the platform

**Dialob** is a platform for creating and testing responsive dialogs and publishing them online. In the context of the Dialob platform, a dialog is both a form and a process. Creating with Dialob is a dynamic process wherein the word “dialog” is a representation of the constant communication between user inputs on the front-end and Dialob’s back-end processes, resulting in a constant “dialogue” between the system and the user throughout the creation process.  In this way, Dialob constantly updates and saves user inputs on the back-end side and reflects those changes in near real time. The product and end result of the dialog process is a highly customizable form with validations, versioning, translation, and security built in, all tailored to a specific end-user requirement.

The Dialob platform comes with a comprehensive set of tools targeted at non-technical users to enable the creation, testing, and deployment of responsive forms.  

The image below represents the Dialob structural hierarchy:

![picture_1](https://github.com/digiexpress-io/digiexpress-parent/assets/47987958/e814df46-ac0a-4454-b989-49a17b8f7a4b)

* **Dialob Composer**: This is the main environment for creating, changing and testing on-line dialogs. It is powered by Dialob Expression Language (DEL), a powerful, custom-made logic-building language which is easy to learn and use even for non-technical users.  Dialob Composer is available as a cloud service, so there is no need to download anything before use. New users need only create a new account to get started.

* **Dialob Manager**: This is the runtime backend service that executes dialogs developed via Dialob Composer. Dialob Manager is an open-source-distributed package, available either as a Docker-image or via source code. Dialob Manager comes with:

 * **REST API**: Simple API for online UI front end to interact with Dialob manager. See reference implementation.

 * **Dialog repository**: Repository where dialogs and associated visibility rules are stored so that they can be fetched by the engine at run time.

 * **Dialog session repository**: Repository that tracks all the changes, dialog requests, and responses, of each unique dialog session. The gathered data provides a full audit trace of each dialog session and thus provides the baseline for further optimization of the online dialogs.

**Dialob platform** is continuously improving it's functionalities evaluating new technologies like AI support as potential additions to Dialob for the purpose of improving its online dialog creation capabilities.


---

## Dialob features

From the first moment, you can start taking advantage of Dialob's wide range of features. The Dialob team can add new features upon request.

* **Logic-building**: Use rich yet simple logic-building expressions with _Dialob Expression Language (DEL)_ to control the flow of dialogs and, in turn, create a highly configurable end-user filling side.

* **Complex validation and visibility rules with built-in functions, DEL, or Java regular expressions**: Using DEL, intricate and detailed forms with interconnected visibility and validation logic can be created, resulting in a fully-customizable and highly-specific user experiences. Dialob Composer can validate or hide/show any response field based on user inputs. Custom functions can also be created when needed.

* **Constant communication between front and backend = Saving on the fly, data security**: Because Dialob Composer (frontend) is in constant contact with Dialob Manager (backend), changes are saved as they happen. There is no fear of losing work. This provides the additional benefit of security: Data cannot be manipulated on the front end, because the backend does all the heavy lifting.  

* **Lists**: Lists can be created to populate multi-choice and choice questions and can be reused/edited as often as needed.  
  * **CSV support**: Upload and download valueset entries from Comma Separated Value files, which is useful for utilising lists created outside of Composer.

* **Custom Variables and Functions**: Using "Context Variables" and "Expression Variables", form creators can create their own custom variables and functions to suit their needs.  

  * A context variable is a user-created variable that can be used either to store the result of a function evaluation, to prefill form data on the filling side, or as a static value (age = 5).  

  * An expression variable is a user-created function that can be used to process inputs from dialog questions, and the product can be stored in a context variable. For example, an expression variable can be written to find the sum of two fields, (question1 and question2) which can then be stored in and later retrieved from a context variable (`question1 + question2 = {sum}`)

* **Pre-filling data**: Dialogs can be configured to populate the filling side with pre-selected data (questionnaires, context variables) where desired. This can be done in the integration layer of Dialob where Dialogs are instantiated.

* **Full visual customisation options**:
  * **Look and feel customisability**: Dialogs can easily be adapted, themed, and styled to fit your needs.
  * **Markdown editing**: Text outputs such as group descriptions, question-level additional information (help text), and note outputs can be styled with simple [Markdown](https://www.markdownguide.org/)

* **Live testing**: In Preview mode, you see the form in its current state and test the functionality in real time from the perspective of the filling side.  

* **Downloading as JSON**: Dialogs can be downloaded as JSON files.

* **Tagging**: Dialogs can be tagged to create a history of changes. The Dialob lifecycle is managed either in a linear or a branched manner. 

* **Localisation**: Dialog questions and list items can be individually translated to / from English, Finnish, Swedish, or Estonian languages. In addition, custom languages can also be created.

* **AI Translation**: Dialob Composer can integrate with an external AI translation service to automatically translate form content between languages. The AI translation feature can translate item labels, descriptions, validation messages, and choice lists while preserving markdown and expression language syntax. Human validation of AI translations is supported through visual indicators.
