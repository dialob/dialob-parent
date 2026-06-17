---
id: 02-basic-operations
sidebar_position: 2
title: Basic operations
---

# Pages

## Quick Summary

* Pages are the main containers for all Dialob elements
* All forms need to have at least one page
* Page logic can contain only visibility rules

---

## Overview  

Pages are the basic containers for all Dialob elements.

Below is a simple diagram of Pages and their child elements:

* Page
  * Group
    * Question (Response)
      * Logic Rules
      
Note that page logic can contain only visibility rules.

---

## Creating a page

Before you can begin creating your form's content, you need to create a page to hold it. All forms need at least one page, but you can have as many pages as you require.

When you first create a new form, you will need to add your first page by clicking the 'add' button in the top right corner.

<img width="1920" alt="new-page" src="https://github.com/user-attachments/assets/2ddbb06a-c508-40cd-81b7-2a4dda001b7f" />



Now, you can begin to populate your empty page with items.


<img width="1920" alt="new-page2" src="https://github.com/user-attachments/assets/88b0c706-29e4-4e24-886d-e33cf449ab3e" />


Note that page logic can contain only visibility rules.

---

# Groups

## Quick Summary

* Groups exist within pages
* Group logic can contain only visibility rules.
* Groups can be nested
* Inputs can exist outside groups
* Groups are created with the 'Add Item / Structure' buttons
* Groups are deleted via the hamburger icon in the top right corner of the group

---

## Overview

Groups exist within pages. They serve to organise different types of questions into logical sets and have slightly different functionalities depending on their type.

There are four types of groups:

* Group (general)
* Survey Group
* Survey Group (vertical)
* Multi-row Group

All groups can contain other groups (nested groups), inputs, and outputs (note type). Inputs or outputs can be created both inside and outside of groups.

Only visibility logic rules can be applied to groups

---

## Group Types

### Group (general)

This is the most basic type of group. It can be used to nest other groups or simply to logically organise inputs and outputs.

### Survey Group

A survey group is used to create survey-style questions.  It contains **Survey Items** and a **List**.  In this type of group, survey radio buttons are arranged horozontally across the screen on the filling side.

### Survey Group (vertical)

A vertical survey group is used to create survey-style questions.  A vertical survey group contains **Survey Items** and a **List**.  In this type of group, survey radio buttons are arranged vertically across the screen on the filling side.

### Multi-row Group

This type of group is used when multiple inputs are required, for example, when collecting a client's first, middle, and last names as separate inputs. On the filling side, a multi-row group will be rendered as a row containing a group of input fields. The number of fields rendered within the group is determined by the number of input fields created within that group on the Composer side.

A multi-row group can contain any combination of different inputs.  

---

## Creating a new group

To create a group, click 'Add item / Structure' buttons at the bottom left corner of the page or an existing group. Then, select the type of group to create.

<img width="1920" alt="create-group" src="https://github.com/user-attachments/assets/1ac8f0ed-0190-4341-8222-14f71db45598" />


---

## Deleting a group

To delete a group, click the hamburger icon in the top right corner of the group, and select 'Delete'.

<img width="1920" alt="delete-group" src="https://github.com/user-attachments/assets/f3498496-2925-43db-9f8e-58afa35236bc" />


---

# Adding new items

## Quick Summary

* There are two ways to add new items: Via the hamburger icon + "Insert New" in the top right of an element, or "Add Item" button on the bottom left.
* Adding an item via the hamburger icon creates the item outside of a group.
* Adding an item via the 'Add Item' button creates the item within the active group.

---

## Overview

Items consist of structures (groups), inputs, and outputs.  

There are two ways to add a new item:

1. **To create a new item WITHIN a group**: Use the "Add Item" button in the bottom left corner of a group. 
2. **To create a new item OUTSIDE of a group**: Use the hamburger icon in the top right corner of a page or group and select "Insert new"

---

## Creating a new item WITHIN a group: Use the 'Add Item' button

Using the 'Add Item' button at the bottom left corner of a group will create a new item within that particular group. See example below:

<img width="1920" alt="add-item" src="https://github.com/user-attachments/assets/2be17ee7-2f2b-4c63-92fa-4ffa9ae86591" />

Select text input type and write a label by clicking on the label field, which will open the item editing dialog, where you can enter localized text.

<img width="1920" alt="add-item2" src="https://github.com/user-attachments/assets/21112ff8-0826-47c3-9a5e-d2a66dddd131" />


On the filling side, we see that our text input with the label "I am inside group 1" appears within group1.

<img width="1248" alt="add-item3" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/08c686fb-b32f-4f69-97c2-8a5098d8f5d5" />


---

## Creating a new item OUTSIDE of a group: Use the hamburger icon + 'Insert New'

Creating a new item with the hamburger icon + "Insert New" from **within a group** will create a new item **below and outside** of the group. See example below:

First, we add an input of type `text`.

<img width="1920" alt="insert-new" src="https://github.com/user-attachments/assets/6c617926-d8bb-410b-a15f-7ae7eea5a197" />


We add a label to `text1`.

<img width="1920" alt="insert-new" src="https://github.com/user-attachments/assets/51e825fe-b348-445c-91f9-0a324144bb40" />


On the filling side, we see that the `text1` with the label "I am outside of group 1" appears outside of Group1 but still inside Page1.

<img width="1209" alt="insert-new3" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/935c059a-be9a-46af-8388-1d90b04556f0" />

---

# Input Types

## Quick Summary

* A Response is akin to a question: We create a Response on the Composer side, and a user completes that Response on the filling side.
* An Input type is the particular kind of value that can be returned by a response. 
  * Example: An input such as a text field or text box will return a text value (string response type). An input of type "number" has a `number` response type.
* Dialob supports a variety of different input/response types that comprehensively cover most data collection needs.
* Additional types can be added to suit your needs.

---

## Overview

Data is comprised of multiple types, such as numbers, texts, true/false information, time, etc.  To collect this information, we use input/response types. 

When you design a form, you decide what type of information to collect, from names to birthdates to decimal values. This is where input/response types come in. As you build your form and create your questions, you specify, using types, what kind of data you are collecting and accordingly, the form of that data that will be returned by Dialob.

As an example, if a question requires that a user enter a first name, the type capable of capturing and returning text is called a "string".  To collect decimal values, you will use the corresponding "decimal" type.

---

### Supported response types

* **Survey item**: Return type will be a key

* **Text**: Return type will be a string

* **Text box**: Return type will be a long string

* **Address**: Enables autocomplete of addresses (disabled by default)

* **Decimal**: Return type will be a decimal number

* **Number**: Return type will be an Integer number (whole number, positive, negative, or zero, no decimals)

* **Boolean**: Return type will be a Boolean value (true / false)

* **Date**: Return type will be a date in the format of "yyyy-mm-dd". [See more on ISO dates](https://en.wikipedia.org/wiki/ISO_8601).

* **Time**: Return type will be a time in the format of "hh:mm:ss"

* **Choice**: Return type will be an ID of selected row in the list

* **Multi-Choice**: Return type will be a set of an ID of selected rows from the list

---

# Output Types

## Quick Summary

* **Note** type is the only output type currently supported in Dialob
  * It is used to provide additional information on the filling side
  * It has no return value

---

## Overview

Note is an output type which is used to provide additional / general information about a question, on the filling side, to assist users in answering questions.

There is no return value to note type.

Note text can be styled with Markdown. [Markdown Syntax Guide](https://www.markdownguide.org/)

---

## Example of Note type

<img width="1120" alt="note-example1" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/a93a7e0b-660e-4142-a51b-4965bcbd8b6f" />

---

# Editing items

## Quick Summary

* To edit an item, click on the hamburger menu and select "Options", this will open the item editing dialog, which is the central place for any edits.
* The item editing dialog is where you can edit the localized labels, descriptions, rules, and other item-specific settings.

---

## Overview

The dialog is organized into tabs, each of which contains a different set of settings for the item. The tabs are as follows:
 - **Label**: This is where you can edit the localized labels for the item. The labels are written in Markdown, which allows for simple text formatting. You can preview the formatted text by clicking the "Preview" button.
  - **Description**: This is where you can add a description to the item. The description is also written in Markdown.
  - **Rules**: This is where you can add visibility and/or requirements rules to the item. Visibility rules determine when the item is shown to the user and requirement rules determine when the item is required to be filled out. The rules are written in Dialob Expression Language (DEL). Default value for the item can also be set here.
  - **Validations**: This is where you can add validation rules to items. Validation rules determine whether the user's input is valid. The rules are written in DEL, and can have a custom validation message (also localized as labels and descriptions). Only available for input types.
  - **Choices**: This is where you can add a local list or link a global list to the item. The list is used to populate the item with choices for the user to select from. Choices are localized, and can be reordered by dragging and dropping them. Only available for choice and survey group types.
  - **Properties**: This is where you can set the properties of the item, that are used to customize the filling side behaviour.

The header of the dialog shows the item ID, which can be modified by clicking the "Edit" icon. The header also shows the type of the item and, where applicable, a dropdown menu to switch between different types.


<img width="1400" alt="item-edit-dialog" src="https://github.com/user-attachments/assets/f2357789-31aa-4dac-9b33-6f266964021e" />


---

# Reordering items


## Quick Summary

* The tree view on the left side of the Composer window shows you the current order of present form items
* To rearrange items, drag and drop them within the tree

---

## Overview  

Tree view shows you the current hierarchy of items as they appear both on the Composer side and on the filling side.  

To rearrange your form items, simply click on them in the tree view, and then drag and drop them in the desired location.

---

## Example of the tree view

<img width="1468" alt="tree-view" src="https://github.com/user-attachments/assets/625d4d77-76f0-460d-b861-823206cc9914" />

---

# Testing fill side

## Quick Summary

* Forms can be live-tested at any time with the "Preview" feature
* Preview is located in the top right corner of the Composer window
* Preview mode is not accessible if a form has errors

---

## Overview  

You can test your form live, at any time in the creation process.

To activate Preview Mode, simply click "Preview" in the upper right corner of the Composer window.

**NOTE**: Any existing errors will need to be resolved before you can utilise Preview Mode.

---

<img width="1468" alt="preview" src="https://github.com/user-attachments/assets/da25125b-eb7a-4ac5-b0c7-f26a7b2a40dc" />

