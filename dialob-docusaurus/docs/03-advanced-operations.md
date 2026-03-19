---
id: 03-advanced-operations
sidebar_position: 3
title: Advanced operations
---

# Lists

## Quick Summary

* Lists
  * are **key**-_value_ pairs (**id**-_list_Item_) 
  * are used to populate drop-down menus, choice selections, and create survey button options
  * can have visibility rules (local lists only)
  * used in combination with Choice or Multi-choice types must be inserted **at the response level**.
  * used in combination with Survey Group or Vertical Survey Group must be inserted **at the group level**.

---

## Overview

Lists are **key**-_value_ pairs (**id**-_list_Item_) that are used to populate drop-down menus, choice selections, and create survey button options.  There are two types of lists in Dialob:


**Global lists**: These lists exist independently from specific response fields, which means that they are not tied to any specific question and can easily be reused anywhere in the form. Any changes made to a global list will automatically be updated in all of the questions which use that list. Use a global list for drop-downs if the intent is to reuse the same list many times in different questions. For example, if a form requires the repeated reuse of a drop-down menu comprised of the same generic cars, a global list is the best choice.

**Local lists**: These lists are bound to the response in which they are created. In fact, you can only create them from within the question/response where they will be used. Changes to local lists will be reflected only within the question where they were created. Use a local list if the goal is simply a one-time use bound to a particular response field which will only apply to that particular question. For example, if a form requires one specific list of Italian sports cars and only one question will ask the user about these cars, a local list is the best choice.

---

### Creating a global list

1. Select "Lists" option from the top menu of the Composer screen.
2. Create a new list by selecting "Add new list".
3. Give the list a name.
4. Give the list items their keys and texts.

* **Name** is the name of the list itself and is used by the form creator on the Composer side to identify the list.
* **Key** is the request ID and must be manually created by the form creator (at this time, list keys are not auto-generated). Keys must follow the [Request ID rules](#request-ids-and-rules-for-writing-them) for creation of IDs.
* **Text** is the actual words / sentences / numbers that the form filler will see when selecting an item from the list. Text is localized, meaning that you can create lists in multiple languages.

<img width="1468" alt="03-01" src="https://github.com/user-attachments/assets/8d353fa5-c994-4859-92fd-f0b187151254" />


---

### Creating a local list

Local lists are created **at the response level** or **at the group level**.

* `Choice` and `Multi-choice` local lists are created at the response level.

* `Survey Group` and  `Survey Group (Vertical)` local lists are created at the group level.

Click the hamburger icon in the top-right corner of the response editor. Then select "Options", which will open a dialog that is the central place for any edits. Navigate to the "Choices" tab and select "Create local list".

<img width="1468" alt="03-02" src="https://github.com/user-attachments/assets/85dd2dac-9f98-4d30-9180-9b7f52994857" />

---

### Editing lists

Lists are shown as expandable rows, and when expanded the list items can be edited - namely the key, the localized text, and in case of local lists the visibility rules. If a visibility rule is present the eye icon will be blue, otherwise gray. Value sets for lists can also be downloaded as csv, edited, and then uploaded back to the list - this can be useful for long lists or when working with multiple languages.

<img width="1400" alt="03-03" src="https://github.com/user-attachments/assets/cfa864b1-d451-4fed-b45e-2f932d5df083" />

For global lists, the name of the list is also editable. The header of the global list editing dialog also shows the users of the list, and the items using the list can be navigated to from the dropdown.

<img width="895" alt="03-04" src="https://github.com/user-attachments/assets/3c7a731e-e9aa-4f35-97e7-b8717874c9af" />

---

### Using a list in a Choice or Multi-Choice response field 

Lists used in combination with `Choice` or `Multi-choice` types must be inserted **at the response level**.  
Lists used in combination with `Survey Group` or `Vertical Survey Group` must be inserted **at the group level**.


The following screenshots demonstrate how to use a list in a Choice or Multi-Choice response type:
 
1. To use a list in a `Choice` or `Multi-choice` response, insert the list **at the response level**. Create a group, then create a response of type `Choice` or `Multi-choice` within that group.

<img width="1468" alt="03-05" src="https://github.com/user-attachments/assets/c7818fb6-8095-4bb1-8d8d-0d1060578e73" />

2. Click the hamburger icon in the top-right corner of the response editor. Then select "Options" and navigate to the "Choices" tab. Select the desired list from a list of global lists, or create a local list.

<img width="1468" alt="03-06" src="https://github.com/user-attachments/assets/8bda44aa-69f8-46a5-9423-6dcad5d94ab4" />

3. Preview the filling side.

<img width="1139" alt="list7" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/33a0eb5c-8526-460a-bf05-96af049f8200" />

**NOTE**: If there is a list attached to an item, there will be an indicator icon, also differentiating between global and local lists. Also note that local lists can be converted to global lists and vice versa.

<img width="1152" alt="03-07" src="https://github.com/user-attachments/assets/ceb4ff3f-4120-41fb-b11f-4cc97ad6e339" />

---

### Using a list in a Survey Group or Survey Group (Vertical) response field 

Creating a survey question is done at the **group level** through the creation of a "Survey Group" or "Survey Group (Vertical)".  

A complete survey question is comprised of three basic parts:

* A survey group (general container for the survey question)
* `Survey item` input type to provide the survey questions within the survey group
* A global or local list to provide the survey responses from which users can select

The following example follows these steps to create a survey about user opinions on car brands.  

1. Create a survey group by selecting "Add item/Structure/Survey Group".

<img width="1468" alt="03-08" src="https://github.com/user-attachments/assets/a5838c5b-f4db-47df-a40c-25ed02915a0d" />

2. Inside the survey group, create Survey Items. For this example, the Survey Items are the car brands we will ask opinions on.

<img width="1468" alt="03-09" src="https://github.com/user-attachments/assets/724346bf-bc43-4424-8794-79901793563b" />

3. Create the list to populate the Survey Responses. The list will comprise the range of opinion choices for the user to select.

<img width="1468" alt="03-10" src="https://github.com/user-attachments/assets/e4398d81-a040-46f2-9518-e4d833a5a5c6" />

4. Insert the list into the Survey Group by selecting the hamburger icon at the top-right of the **group**.

<img width="1468" alt="03-11" src="https://github.com/user-attachments/assets/b21f2e44-9c08-40d5-a055-5c8c01224b0e" />

5. Preview the filling side.

<img width="1115" alt="survey6" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/d72cee8b-41c6-4ead-94a2-91e9fdabb04d" />

---

# Lifecycle Management

## Quick Summary

* Dialob supports two kinds of tagging: Linear and Branching
* Dialob forms created via branching will not appear in Version History
* Multiple users editing the same dialog session simultaneously will result in unpredictable Dialob behaviour

---

## Overview

Managing the Dialob Lifecycle is simple with tagging. Creating a tag can be thought of as assigning a name to represent the state of a dialog at a certain point in time. This tag can then be referred back to at any time, and previously-created dialogs can be recalled and used whenever needed. Dialogs can be continually refined and managed in this way, with the tagging history providing a list of each tag, its creation date, and the ability to revisit/reactive previous tags. 

When making a new tag, Dialob Manager will save the dialog in its current state, which will then become immutable. Then, Dialob will create an exact copy of this dialog, which it will tag as the "latest version". Only the "latest version" can be edited. In this way, new versions can be built on top of old versions, using the older tags as a "starting off point" for the "latest versions".  

Dialob currently supports two forms of versioning: **Linear** and **Branching**. 

---

### Examples of Tagging: Linear and Branching

**Branches** are created by copying the original version to create a duplicate to be edited. The branching functionality is not currently available in the "Version" menu, and branched versions will not appear in Version history.

**Linear tagging**: 

Possible use case: Create a singular, evolving form with a core set of elements which won't change much over time

* Create v1.0 with 10 core questions.
* Create v2.0, which is based on v1.0 **PLUS** additional 20 questions.
  * v2.0 now has 30 questions: 10 from v1.0 and 20 new, unique to v2.0
* Create v3.0 from v2.0.  Modify existing questions.

_Result: There are three different tags in this scenario which represent the same form at different periods in time, a sort of evolution. This scenario can save time if you need to reuse many of the same questions without making too many modifications._

**Branching**: 

Possible use case: Create multiple different forms based on the same core elements/original form, and modifying each core to suit different needs

* Create v1.0 with 10 core questions.
* Create v2.0 with modified v1.0 questions, tailored to a similar but different user base.
* Create v3.0 with modified v1.0 questions, tailored to a similar but different user base.
* Repeat for each version of v1.0 required.

_Result: Using one core set of questions, multiple different forms can be created, each sharing similarities of the original core._

In summary, creating version tags via the "Version" menu creates dialogs that are tagged in a straight line as a continuous process, which means, in practice, that the newer version is an evolution based on the version that came immediately before it.

Branching, on the other hand, produces "lateral" versions based on the same original copy. It works simply by copying the original version and creating a duplicate.  

---

### How to create a tag 

**Linear tags** are created via the "Version" menu in the Composer window.

1. Navigate to the "Version" menu. To the right of "Version", the current dialog's version tag in will be displayed. If the active dialog is the latest version, "Latest Version" will be displayed. If the active dialog of is a previous version, that version's tag will be displayed.

2. To create a new tag, navigate to "Version" / "Create version tag" and enter desired tag name. Enter a tag name and an optional description.

<img width="593" alt="03-12" src="https://github.com/user-attachments/assets/fd6baa5a-bc84-4255-87e4-f680f5e2ec00" />

3. When navigating back to "Version" / "Manage versions", a list of all tags and the dates at which they were created is displayed. Select "Activate" for a previous version.


---

### Versioning dialog

The versioning dialog is accessible via the "Version" menu, by clicking on "Manage version".  Here, you can see all the tags you have created, along with the date they were created. Hovering over the date will show the creator of the tag, where applicable. 

This dialog also allows you to preview the dialog in the state it was in when the tag was created, by clicking on "Activate". You can switch back to the latest version by clicking on "Activate" next to "Latest version".

Tags can be downloaded as JSON files using the download icon, or can be copied as new dialogs by clicking on the copy icon.

<img width="895" alt="03-13" src="https://github.com/user-attachments/assets/2a6d54d2-760c-48fc-a19e-837896494e8e" />

---

### Multiple users accessing the same dialog session at the same time 

In the Dialob lifecycle, it is possible for multiple versions of the same dialog to co-exist at the same time.  For example, a situation may occur when one user is working with an older version on the filling side, while, at the same time, another user decides to create a new version.  Because session IDs are associated with the user's login, the user with the older version will proceed with the older version while the user with the newer version will proceed with the newer version.  Dialob's default behaviour is to allow this, but it can be modified if needed.  

Also, when in the production environment, one user can be editing a dialog on the Composer side while another user can see the changes as they happen on the filling side.  However, multiple users editing the same dialog session at the same time is not supported and will result in unpredictable Dialob behaviour.

---

# Comma Separated Values

## Quick Summary

* CSV files are a quick and easy way to create list valuesets with localisations
* Uploaded CSV files must follow the required format for Composer to apply them
* Supported form languages must be created via Translations **before** uploading a multi-language CSV file, otherwise those language values will be ignored by Composer

---

## Overview

Uploading CSV files is a quick and easy way to create list valuesets with localisations, using your CSV editor of choice.  The uploaded file can then populate an empty list created within Composer, which you can apply to individual questions just like any Composer-created list.  
 
You can also download any list valuesets as a CSV file.

**NOTE**: If you plan to use CSV files for multi-language valueset creation, first check out [Important notes on list-building with localisations in CSV](#important-notes-and-troubleshooting-multi-language-list-building-with-csv)

---

### What is a valueset?

A valueset is the key-value pair for a list item (unique id + localized item text/description). See this example from a list entitled "animals":

<img width="747" alt="03-14" src="https://github.com/user-attachments/assets/9d6bf730-e0ce-49de-90cc-04d21f15865a" />

### Localisation and valuesets

Valuesets created via CSV files can be localised for a particular language, simply by adding additional columns with headers designating the language of that item using two-letter language codes. This localisation is explained in greater detail in the next section: _Required CSV format_.

---

### Required CSV format and notes 

A CSV file must follow this format:

* The first row is the header row.
  * The first column of the header row must be `ID`.
  * The rest of the header columns must be two-letter language IDs for labels. [See two-letter language codes](https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes)
* The first column of the content rows is item ID.
* The rest of the content columns are the entries in the language corresponding to the two-letter language code in the column header.

The example below follows these rules. It contains three columns:

* Column A is the item ID
* Column B is the item value in English. The header includes the two-letter language code "en" for English.
* Column C is the item value in Estonian. The header includes the two-letter language code "et" for Estonian. 

<img width="350" alt="csv-format-example" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/94707651-8fdd-4d3c-838d-3d874bb15285" />

#### Notes on CSV creation and uploading

* Empty rows are ignored
* The order of entries in the file is retained
* All **defined** languages on a form are imported. This means that, if your target form doesn't support the language specified in the CSV file, the values will remain hidden.  In practice, for example, if you specify Estonian language valuesets like we did above, but your form doesn't have Estonian language translations enabled, the values in the "et" column will not appear in your Lists, nor will they be accessible until you define the Estonian language and reupload your CSV.  Defining a language for your form is as simple as going to the [Translations](#localisation) feature in the top menu bar and activating your desired language.

_To avoid this issue, be sure to activate your desired languages via the Translations modal **before** uploading your CSV file._

* All entry IDs are imported exactly how you wrote them in your CSV file. In the case of conflicts or other problems, error messages will be displayed, and you will need to resolve these as normal.
* Valueset entries are downloaded as a CSV file in the same format as described above.
* The following import modes are supported:

  * **Replace all**: Replaces all valueset entries with the values from the CSV file.
  * **Append**: Appends values from the CSV file to the end of the existing valueset entries. This will cause ID conflicts if new CSV values have IDs which are the same as existing valueset entries' IDs.
  * **Update**: Updates existing entries by ID and adds new entries from file.  

---

### Simple outline of steps to list-building with CSV 

Below is a simple outline for creating a list using CSV files from Google Sheets. A more detailed walkthrough for this process follows the outline.

#### Steps to create and upload the CSV file

1. Create a csv file following the required format
2. Download file in .csv format (only .csv format is supported)
3. Create a new list in Composer and give it a name
4. Upload .csv file to that list via the upload button

#### Steps to apply the CSV file to your Dialob form

5. Use Composer to create the question to which you wish to append your list
6. If using multiple languages, open Translations and add your required language
6. Navigate to Translations/Fields, and translate the question to which you will append your list
7. Test the filling side by selecting an active language in the top right of the Composer window, followed by the Preview button.  

---

### Step-by-step walkthrough of single-language list-building with CSV 

**1. Create a CSV file and download it in .csv format _only_**

For this walkthrough, we will use a CSV file containing key-value pairs for animals. The CSV file has one language localisation: English (en).  

Create your CSV file according to the guidelines for required CSV format above, and download it to your local machine.

<img width="253" alt="csv-format-example2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/055c8fb6-15b3-407f-9224-a600af25ebc9" />

**2. Create a new list in Composer to hold your CSV values**

Navigate to "Lists" in the top menu, and create a new list. Give the list a name, but do not fill in any list items.

<img width="890" alt="03-15" src="https://github.com/user-attachments/assets/cdfc152e-75b9-49a3-8a48-b34393c7c191" />

**3. Upload your CSV file via the upload button in the Global Lists modal**

Click the "Upload CSV" icon.

<img width="890" alt="03-16" src="https://github.com/user-attachments/assets/c5c91fb7-a030-44c3-8003-7239d2eddf94" />

Then, select your desired CSV file, followed by upload mode. For this example, we are going to select "Replace" because we are dealing with a completely new list with no pre-existing values to edit or append to.

<img width="892" alt="03-17" src="https://github.com/user-attachments/assets/a69f3c30-fe55-4e97-8019-65bfaafc037c" />

Once you have uploaded your CSV file, you will see that the list will automatically be populated with your valuesets, which appear in English because the form's active language is English and the two-letter language codes used in the second column of the table determined this content to be of language "en".

<img width="893" alt="03-18" src="https://github.com/user-attachments/assets/2d939770-60ca-45a4-af9f-a433bf7186cf" />

**4. Test your new list**

Append your list to a choice or multi-choice input type, write your question, and preview your form.

<img width="1142" alt="animals-csv-preview" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/de67d2b0-0f00-402e-b1f5-fe09b35669a9" />

---

### Important notes and troubleshooting multi-language list-building with CSV

* To ensure that all of your CSV's languages are recognised and added to your form upon upload, supported form languages **must be created** via the [Translations](#localisation) feature **BEFORE** you upload your CSV file!  
* This means the following: Composer will ignore the values of a CSV file associated with a language that has not been defined in Composer prior to CSV upload.  
* If you forget to activate a desired language prior to CSV upload, or you just wish to add support for other languages after the initial upload, activate the desired language via the Translation function and **reupload your list.**
* When looking at your list in the Lists window, you will see only the values associated with the currently active language in Composer. Changing the active language will change the values in the list to the specified language.  

---

# Downloading Dialog metadata

## Quick Summary

* Forms can be downloaded in JSON format

---

## Overview  

You can download a JSON file at any time which describes your current Dialog.  

Simply click on the download icon in the top right of the Composer window.

---

<img width="614" alt="03-19" src="https://github.com/user-attachments/assets/357ee907-ce64-4ed7-9663-86bd909418c6" />

---

<span id="localisation"></span>

# Localisation

## Quick Summary

* Create multi-language forms with the Translation feature
* Support for all ISO 639-1 languages
* If you start a filling session with incomplete translations, the untranslated items will not appear!
* There are two modes to use when adding a new language: 
  * [Create Empty](#create-empty)
  * [Copy from Active](#copy-from-active)
* Test your translations on the filling side by changing the active language either via the Translations dialog ("Manage languages" tab) OR via the language dropdown in the Composer upper menu
* Check for missing translations using the "Missing translations" tab in the Translations dialog

---

## Overview

Dialob enables you to create multi-language forms via a simple Translation feature.  There are three main tabs within the Translations dialog:

1. **Manage translation files**: This allows to donwload all current localized strings in a JSON file. The file can be used to translate the strings and then upload them back to Dialob. This is useful for sharing translations with external translators or for translating the strings in a more convenient way than in the Composer.

2. **Manage languages**: This tab shows all current languages and allows you to manage them, as well as add new languages. Adding a new language via the "Add language" dropdown will create an empty language, while using the "Copy as new" button will copy the translations to the new language. This tab also allows switching the currently active language via the switch in each language row.

3. **Missing translations**: This tab shows all missing translations for each language, grouped by the type of translation (item label, item description, validation message, valueset label). Clicking on a missing translation will take you to the item that needs to be translated.

<img width="893" alt="03-20" src="https://github.com/user-attachments/assets/55fbc327-d70e-4ed1-bc65-f407e12cd764" />

---

## Activating a new language 

When you add a supported new language, any translations you have written for fields and items will appear in that language on the filling side as long as they have been defined.  Filling side buttons will automatically be translated into the active language.

From the screenshot below, you can see that English is the only supported language on our form. Also, the blue highlighted switch indicates that English is the currently active language. 

To add a new language, select it from the "Add  language" dropdown. All ISO 639-1 languages are supported.

<img width="893" alt="03-21" src="https://github.com/user-attachments/assets/94918671-a00d-4495-b967-21e90ae6f5d6" />

Take note of the two creation method options: **Copy from Active** and **Create Empty**.  

<img width="942" alt="03-22" src="https://github.com/user-attachments/assets/3d8a4697-e6e6-4392-ae86-48ea3bd75c05" />

### Copy from Active 

This feature will create the new language and populate its values with a copy of the values in the currently active language. In practice, this means that if your currently active language is English, and you create a new language with the "Copy from Active" option, you will see your existing values copied to the new language's values, but they will still be in English. The copied values will need to be manually deleted and translated accordingly.

NOTE: When creating new translations via "Copy from Active", you can be assured that there will never be any blank fields or items on the filling side. If you forget to translate certain items into the active language of the filling side, they will appear in the language from which they were copied, thus ensuring that every field has at least some text in it.

### Create Empty 

This feature will activate the new language without copying the values from the currently active language, essentially leaving you with a "blank slate" with which to start writing your translations, no deleting needed.  

NOTE: If you forget to translate some fields and then start a filling session with incomplete translations, the untranslated items will not appear, causing empty fields on the flling side.  You can easily see if there are any remaining untranslated items by checking the "Missing translations" tab in the Translations dialog. Clicking on a missing translation will take you to the item that needs to be translated.

---

## How to start translating items 

Upon activating a new language, you can begin to translate your items. In this example below, we activated the Estonian language and selected the "Create empty" option. We then navigated to the "Manage translation files" tab and downloaded the CSV.

This is the downloaded file. It shows the item IDs (that are in the form of type:ID:field), page IDs, parent IDs and types, a human readable description of the item, and the current translations. Each language has its own column, and the header is marked by the ISO 639-1 language code.

<img width="1098" alt="03-23" src="https://github.com/user-attachments/assets/d2cad0aa-bbeb-441c-a66a-1effe5d0f265" />

We can translate the items, save the file, and then upload it back to the Translations dialog. In this case, we will intentionally remove the page translation.

<img width="906" alt="03-24" src="https://github.com/user-attachments/assets/26ba9845-1b26-48b4-a70b-42255fa0799b" />

After uploading the file, a check is perfored to see if there are missing translations and the user is alerted, but can still perform the upload.

<img width="895" alt="03-25" src="https://github.com/user-attachments/assets/70266ced-0d04-4c76-aee9-9f90a5b63aae" />

After confirming the upload, the translations are updated and the missing translations tab can be checked to see if there are any remaining untranslated items. As expected, the page translation is missing, and clicking on it will take us to edit the page label.

<img width="895" alt="03-26" src="https://github.com/user-attachments/assets/9a2e1a13-5e1a-46dc-afe6-4b820d80e7c3" />

---

## Testing 

You can test that your translations appear correctly on the filling side first by changing the active language either via the Translation dialog "Manage languages" tab OR via the language dropdown in the Composer upper menu.

<img width="897" alt="03-27" src="https://github.com/user-attachments/assets/d5d6fd5b-4e6d-4010-bd19-f2d6d8767f7b" />

<img width="582" alt="03-28" src="https://github.com/user-attachments/assets/6b2d29b6-cab5-4977-ac95-b9fe6c91b5b8" />

In this example, we select "Estonian" from the dropdown and then select "Preview", and on the filling side, everything is correct:

<img width="1114" alt="est-translation" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/11a8e3a7-49c7-4c42-845f-3c490b721ea6" />

---

## AI Translation

### Quick Summary

* AI translation service integration automates the translation of form content between languages
* The feature requires backend configuration and an external translation service
* AI translations are marked with visual indicators showing source language and timestamp
* Translations can be triggered individually or in bulk across missing entries
* Human validation is supported through indicator flags that can be removed after review
* The feature preserves markdown formatting and expression language syntax

---

### Overview

Dialob Composer can integrate with external AI translation services to automatically translate form content from one language to another. This feature significantly speeds up the process of creating multilingual forms by providing automated translations that can be reviewed and validated by users.

The AI translation service translates:
* Item labels (questions, groups, pages)
* Item descriptions
* Validation messages
* Choice list entries (valuesets)

The translation process preserves special formatting such as markdown syntax and Dialob Expression Language (DEL) expressions, ensuring that the functional aspects of your forms remain intact across languages.

---

### Configuration

The AI translation feature requires configuration in the Dialob backend. The backend must have the `composer.translationServiceUrl` property defined to point to the translation service endpoint.

**Important**: If the translation service URL is not configured, all AI-related features will be hidden from the Composer UI.

---

### Translation IDs

When collecting entries for translation, the system constructs translation IDs that follow the same format used for CSV upload/download:

| ID Format | Example | Description |
|-----------|---------|-------------|
| `i:{itemId}:l` | `i:group1:l` | Item label |
| `i:{itemId}:d` | `i:group1:d` | Item description |
| `i:{itemId}:v:{ruleIndex}` | `i:group1:v:0` | Validation message |
| `vs:{valueSetId}:{entryIndex}:{entryId}` | `vs:vs1:0:choice1` | Value set entry |

This ensures consistency across all translation-related features in Dialob.

---

### Using AI Translation

The AI translation feature appears in several places within the Composer interface:

#### 1. Item Options Dialog

When editing an item's options (labels, descriptions), a translate button appears next to fields that have no translation in the target language but have content in the currently active language. The button shows a tooltip indicating the source and target languages.

Clicking the translate button sends the text to the AI translation service and applies the result automatically.

<img width="896" alt="image" src="https://github.com/user-attachments/assets/06f6ad70-65af-440e-a224-55ed7421c0c0" />

#### 2. Choice Editor

In the choice editor for lists, a translate button appears in the first column header. This button allows you to translate all missing choice entries for the currently selected language at once.

<img width="1402" alt="image" src="https://github.com/user-attachments/assets/ad16a393-b5d7-44c6-a798-a9b036eb780e" />

Clicking the button opens a confirmation dialog showing:
* The source language (currently active language)
* The target language
* The number of entries to be translated

After confirmation, all untranslated choice entries are sent for translation and applied to the list.

<img width="595" alt="image" src="https://github.com/user-attachments/assets/c412c112-5903-499f-8ea4-10a2d3cb117c" />

#### 3. Translations Dialog

The Translations dialog provides the most comprehensive AI translation features:

**Missing Translations Tab:**
* Shows all missing translations across the form
* Individual translate buttons appear next to each missing entry
* A "Translate all missing" button translates all missing entries across all languages
* The bulk translation shows a progress dialog for each language being processed
* After bulk translation, a review dialog displays all new translations for confirmation before applying

<img width="909" alt="image" src="https://github.com/user-attachments/assets/a313508f-7b8f-4d86-9287-48b80b04f0cc" />

<img width="508" alt="image" src="https://github.com/user-attachments/assets/8046f0b4-0119-4b18-a6b5-e0a3bdf0c1fc" />

<img width="1414" alt="image" src="https://github.com/user-attachments/assets/87816134-084a-4ae9-814e-a9daf6d0e271" />

**AI Translations Tab:**
* A dedicated tab showing all AI-translated content in one place
* Displays each translated entry with its source language and translation timestamp
* Allows easy review and validation of all AI translations
* Provides quick navigation to translated items

<img width="911" alt="image" src="https://github.com/user-attachments/assets/1943b854-ac9a-4505-9392-0fdcc5ead33e" />

---

### AI Translation Indicators

When content is translated using AI, it is marked with a visual indicator (flag icon) that shows:
* The translated text
* The source language it was translated from
* The timestamp of when the translation occurred

These indicators appear throughout the Composer wherever AI-translated content exists, making it easy to identify which entries have been automatically translated and may benefit from human review.

**Validating Translations:**

To mark a translation as human-validated:
1. Click on the AI indicator flag, OR
2. Make any change to the translated text

Either action removes the AI indicator and removes the entry from the AI translations metadata list, signifying that a human has reviewed and validated the translation.

---

### Translation Workflow

**Individual Item Translation:**
1. Navigate to the item you want to translate
2. Ensure the source language is set as the active language
3. Switch to the target language using the language dropdown
4. Click the translate button next to the empty field
5. The AI translation is applied automatically
6. Review the translation and click the AI indicator to mark as validated

**Bulk Translation:**
1. Open the Translations dialog from the top menu
2. Navigate to the "Missing translations" tab
3. Click "Translate all missing"
4. A progress dialog shows translation progress for each language
5. Review the translations in the preview dialog
6. Click "Apply" to confirm or "Cancel" to discard
7. Review AI translations in the "AI Translations" tab as needed

---

### Important Notes

* Empty string labels are intentionally not translated, as they are sometimes used for structural purposes
* The source language for translations is always the currently active language in the Composer
* Translation IDs sent in the request are returned in the response and used to apply translations to the correct items
* Multiple languages can be translated in sequence during bulk operations
* All translations preserve markdown formatting and DEL expressions
* AI-translated content should be reviewed by users familiar with the target language for accuracy and context

---

# Unique identifiers

## Quick Summary

* An item's (Request) ID is a unique variable name that ensures each Dialob item (question, list item, field, etc.) has a unique identifier across a Dialog session
* Requests consist of two parts: Request Type (string, boolean, number, etc.) and meta data (e.g. the question the user will be responding to on the filling side)
* Group, response (question), and Note IDs can be changed by clicking on their current ID in the top left corner of the item window
* Page IDs can be changed by clicking on the hamburger icon next to their names

---

## Overview 

All Dialob elements have globally unique identifiers:

* Dialog ID
* Page ID
* Group ID
* Input ID
* Output ID

The Dialog ID can be found in the Dialog Options/Information window, accessible from the upper menu.

The IDs for all other items can be seen in the top left corner of the items themselves.
---

## What is a Request? 

The main variable used in DEL is a special meta variable called **Request**. A Request is the meta variable used to gather (form user) input defined as **Response**.

A Request consists of two parts:

* **Request type**: Defines how user input is gathered. For example, a Text type will gather responses by the user in the form of text and return a String, whereas Integer type will gather numbers and return an Integer.  In other words, the request type is the type of data that a question will return; for example, a number, a paragraph, or an address.

* **Request meta data**: Contains a hint/rubric/question describing what it wants the form user to do. Examples of a request include prompts such as "What is your name?" Take a picture” or “Select from the list”.  This can be thought of as the question prompt for the user to answer, also referred to as the item label. 

---

## Request IDs and rules for writing them 

Request IDs (unique variable names) are automatically generated by Dialob Composer to speed up the development process and to ensure that each Request has a unique ID by default. The user can change them as desired, and user-defined Request IDs uniqueness is automatically checked in near real time. Dialob Composer’s built-in ID notation check is based on the following naming conventions:

* The request ID must start with letter [a-z,A-Z]
* It can be followed by a number [0-9]
* It cannot be any reserved name (logical operators, comparison operators etc.)
* It must be unique. This is automatically checked by Dialob.
* Request IDs are case-sensitive. E.g. _firstname_ is not the same as _Firstname_.
* All of the changes to existing Request IDs are automatically updated by Dialob Composer. This ensures that the previously-created control logic with DEL remains correct and up-to-date.

---

## How to change an ID 

Changing an ID will update all references to this ID automatically.

### Page ID

The Page ID can be changed by clicking the hamburger icon to the right of the Page name and selecting "Change ID" from the dropdown.


### Group ID, Question ID, Note ID

These can all be changed simply by clicking on their current IDs in the top left corner of the items and creating a new ID.


If a new ID conflicts with an existing one, the ID editing field will turn red, and a new ID will not be set until the naming conflict is resolved.

---

# Custom variables and expressions

## Quick Summary

* Context Variables are static/immutable variables, available across the entire dialog session
* Expression Variables are user-defined functions, available across the entire dialog session
* Custom variables are only available for use when their values are set. If the values are not set, they will not be present or usable in the session

---

## Overview: Custom Variables and Expressions

**DEL** supports custom variables and custom expressions, called **Context Variables** and **Expression Variables** respectively, which can be used as part of dialog *visibility*, *required* or *validation* logic. These special variables/expressions are especially helpful for several situations where custom functionality is needed:

* If you need to pre-fill default data into a response field
* When a filling session is to be initialised with pre-defined data, often pulled from an outside resource
* Writing custom mathematical and logical functions

Context variables and expression variables can be of the following types: `Text`, `Number`, `Boolean`, `Date`, or `Time` with the exclusion of `Note` type.

Context variables and expression variables can be a combination of existing variables or function calls.  

---

## Context variables 

**In terms of a dialog session, a context variable is a static variable: it is immutable in the dialog session and is available across the entire session.**  

* A context variable has an ID, a type, description and a default value.  
* A context variable can be called by using its ID.
  * Using a variable within a note: ID must be surrounded by curly braces. For example: ` {contextId}`.
  * Using a variable within a response field: ID must be surrounded by curly braces. For example: ` {contextId}`.
  * Using a variable within a logic rule/expression: Write variable name without curly braces. For example: `contextId`
* A context variable's value is set outside of the context of the given dialog session and is not tied to any specific request ID. It works like a "hidden field", and typically, it is used to preload a given dialog session with data, such as preloading CRM data of an identified user, which is known before launching the session.  
* A context variable  can also be any existing response ID in the current session. For example, if you have a Text type response with ID `myName`, you can output the value of `myName` in a `Note` type. Just surround the response ID with curley braces `{ }` from inside the `Note`. So, as a response ID: `myName` but as a context variable being called: `{myName}`.  

Below are some examples to demonstrate what context variables can look like.

<img width="895" alt="03-29" src="https://github.com/user-attachments/assets/fd85684e-aaf8-4653-8277-f24213940c67" />

---

## Expression variables 
**An expression variable is a user-defined function which is not coupled to any specific request ID. It can be used, for example, to find the sum of two request IDs of type `Number` or to validate a text input in multiple different responses.** 

* An expression variable has an ID, description and an expression (function).
* An expression variable can be called with its ID surrounded by curley braces. For example: `{expressionId}`.
* An expression variable can be any of the response IDs in the current Dialob session, a logical expression, or a mathematical operation.
  * **Response ID in the current session**: Examples include default IDs such as `text1`, `date3`, `boolean2` or user-created IDs such as `isNewCustomer`, `postalCode2`, etc..
  * **Logical expression**: Example: `{date3 > date2}`, which can be read as "**If** date 3 is later (greater) than date 2, **then** return true".
  * **Mathematical operation**: Example: `{1 + 5 + 2 * (6 / 3)}`, which can be read as "6 divided by 3, 2 multiplied by 2, and then 1 plus 5.

Below are some examples to demonstrate what expression variables can look like.

<img width="892" alt="03-30" src="https://github.com/user-attachments/assets/cd91ac9e-04b5-4d35-b879-8e0ee1841e83" />

**NOTE**: **Requests, expression variables or context variables** are *ONLY* available when their values are set. This means that if the value is not set, then they will not be not present in the given dialog context.  In other words, in **DEL**, there is no such thing in as a NULL as value for a variable.

The next part outlines the use of context and expression variables.

---

# Using custom variables and expressions

## Quick Summary

* Custom variables are created in the "Variables" menu item at the top of the Composer screen
* Custom variables have many use cases, including
  * Pre-filling data on a question 
  * Initialising a form filling session with prefilled data
  * Creation of custom functions
  * Use in logic rules and expressions
* It is recommended to leave custom variables' "Published" value unselected as per default, unless special implementation is required
* A custom variable built using *question input data* becomes active only after all of its questions have been asked. This means that it must "wait" to receive input data before it can start working

---

## Overview: Creating custom variables and expressions


Context variables and expression variables can be created in the "Variables" menu at the top of the Composer window. Selecting the "Variables" menu option will present you with the variables window. From here, you can choose to create context or expression variables via the tabs.

<img width="1468" alt="03-31" src="https://github.com/user-attachments/assets/8dbaf25a-89f3-4fcd-9a63-2d4a80ea593d" />

---

**NOTE**: When you create a variable, it is important to take note of the "Published" feature.

**Context and expression variables both have the "Published" option.  Publishing a variable will make its value available on the filling side UI.**

It is recommended that, unless required by a specific implementation, the "Published" setting remain unselected.

**"Published" is unselected by default.**

---

### General example of creating and injecting a custom variable into a dialog

For this example, we create a custom variable called `{age}` which will be of type `Number` and will have a default value of 15. This variable will be used in a Boolean response field and will prompt the user on the filling side with the following:

`Is your age 15?`
`YES/NO`

We begin by creating our context variable.

<img width="889" alt="03-32" src="https://github.com/user-attachments/assets/ca04ab06-653c-47ef-a979-48ec88e5cb29" />

Now that we have a variable created, we want to put it to use by inserting it into a response field. To do this, the we take the variable ID, surround it with curly braces`{ }`, and insert it in the field where we want to use it. In this case, it is in the "Label" field of ageQuestion1. 

So, the variable's ID is `age`, but when used in a response field, we must refer to it as `{age}`.  

In this next step, we create a boolean response field and place our context variable inside, remembering to surround it with curly braces: `{age}`. This boolean response field will ask the user if his/her age is equivalent to the default value set by the context variable, which we set to 15.  We then preview the variable on the filling side:

<img width="1467" alt="03-33" src="https://github.com/user-attachments/assets/feeab245-5ba0-4661-a9cc-c1ebac2ad4f0" />

Before you can actually preview the form, it is important to remember that, whenever context variables are exist in a dialog, whether or not they are being used at the time, you will need to declare or check their default values before the filling side preview mode can be shown. This means that, after you click Preview but before you see your form, you will be presented with the Context Variable Preview window as seen below. This window will show you your context variables and any default values you have given them.

In the example below, you can see here that a value of 15, which we set for our `{age}` context variable, shows up as the default value. This value will appear on the filling side unless we type in a different value.

<img width="892" alt="03-34" src="https://github.com/user-attachments/assets/5d03ba12-a47a-44bd-85a0-222112779f79" />

Finally, on the filling side, this is the output:

<img width="1099" alt="is-your-age3" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/11f34334-9f0c-480d-9c5b-928a44f7bcf6" />

---

## How to call and use custom variables

* To use a variable within a note: ID must be surrounded by curly braces. For example: ` {addAges}`
* To use a variable within a response field: ID must be surrounded by curly braces. For example: ` {addAges}`
* To use a variable within a logic rule/expression: Write variable name without curly braces. For example: `addAges`

---

### Using expression variables to create and execute mathematical functions 

In this example, we create several expression variables which will perform mathematical and logical operations on data that the user inputs on the filling side. 

#### Step 1: Create form inputs

Before we create our expression variables, let's first create the inputs that the expression variables will perform operations on. We will create two inputs of type Integer: `number1` and `number2`.

<img width="1186" alt="03-35" src="https://github.com/user-attachments/assets/451ecd9a-70e7-41c9-a023-f218998b19c1" />

Previewing the filling side gives us this:

<img width="1122" alt="fun-with-numbers2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/004836f6-38b2-435e-985c-3b1cb1a83a4d" />

#### Step 2: Write expression variable functions

Next, we will create several expressions to perform operations on whatever integer input we get from the filling side.  Keep in mind that **creating an expression variable is almost the same as creating a context variable**:  

* In the ID field, write the desired name of the custom function. This is how you will call this function later.
* To edit the Expression field, clicking the Edit icon button, which will expand the code editing field below.
* In code editor, write the ID(s) of the responses to process with the function as well as the mathematical / logical operators that will be used. 
* Remember to leave "Published" unchecked unless your specific implementation requires it be checked.

The expressions we will create are as follows:  

| ID                | Expression           |
|-------------------|----------------------|
| add               | number1 + number2    |
| subtract          | number1 - number2    |
| multiply          | number1 * number2    |
| comboOperation    | (number1 + number2) * (number1 + number2) |
| isGreater         | number1 > number2    |
| isDifferentNumber | number1 != number2   |

And in Composer, they look like this:

![03-36](https://github.com/user-attachments/assets/3f9f510a-5eda-46dd-af16-d666d99f8634)

**Calling an expression variable** works in the same way as calling a context variable. For this example, we are going to call the expression variables in a note output, where we will be able to view the outcome of their mathematical operations after the user has input integers into the filling side.  

#### Step 3: Create a note output into which you can insert your expression variables and view the outcome of your operations

Create a new output type: `note` in your group and enter the following:

```markdown

The sum after adding is: {add}.  

The remainder after subtracting is: {subtract}.  

The product after multiplying is: {multiply}.  

The super fun comboOperation result is: {comboOperation}.  

Is number1 greater than number2?  {isGreater}.

Is number1 a different number than number2?  {isDifferentNumber}.  

```

Depending on how you laid out your group/input structure, on the Composer side, you should see something like this:

<img width="1467" alt="03-37" src="https://github.com/user-attachments/assets/7ed20973-2986-40df-a1a7-6e51699189ab" />


#### Step 4: Preview and test

Finally, hit the Preview button and test your form to see if everything is working correctly.

<img width="1115" alt="fun-with-numbers6" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/defbd1ee-9dad-4a05-9f37-21db6c82fd92" />

**NOTE** A custom variable built using *question input data* becomes active only after all of its questions have been asked!
This means that, for example, the expression variables created above in the previous cannot work until number1 and number2 have been answered on the filling side.
