---
id: 06-options-and-settings
sidebar_position: 6
title: Options and settings
---

# Options and Settings

## Quick Summary

* Dialog options allow the modification of general information.
* Element options provide metadata and/or style classes to individual form elements.

---

## Overview

Settings provide various metadata to the form and the form elements. 

---

### Dialog options: Form metadata

View and modify general information such as form name, number and types of elements in the form, submit URL, form creation date and last save date, etc.


### Form element options

Provide additional metadata and/or style classes to the individual form elements: Group and Question.

---

# Dialog Options

## Quick Summary

* Access Dialog options by clicking on the "Options" button in the Composer upper menu
* Dialog options give you an overview of basic form data and several globally-applicable visibility and requirement options

---

## Overview  

The Dialog options give you 

* An overview of basic form data
* General visibility and requirement options that can be applied globally to all questions

Dialog options can be accessed by clicking the Options button on the top menu bar.

<img width="1466" alt="06-01" src="https://github.com/user-attachments/assets/857ad915-6d76-41ae-b9b6-0d93b5cb11a5" />
---

### Content

![06-02](https://github.com/user-attachments/assets/435fc4a3-c317-4a73-8639-e3c47f650b5d)

#### Form name

A String representing your form's name, not technical ID. This can be changed at any time.

#### Labels

For grouping and filtering purposes, you can create arbitrary labels for your forms.  This can be used to filter by label when you request a list of forms or to identify which forms belong to which application if you have several applications using the same Dialob backend.

#### Question visibility during filling

This drop-down gives you multiple global options to apply to your questions.

* **Show only active questions:** Only information about active elements is sent to filling side (default). For example, elements with visibility rules dependent on previous elements will not appear, as they are not active until their dependent questions are answered.

* **Show inactive pages:** Information about inactive pages is sent to filling side, which is useful for navigation features.  

* **Show all questions:** Information about all elements is sent to filling side, which is useful for debugging reasons.

* **All answers required by default:**  Sets a global "required" rule across all questions. A Dialog cannot be completed until all questions have been answered. To make an exception to this rule for individual questions, write a requirement rule that returns `false` for each excepted question.

---

#### Technical name 
The unique ID of this form. It is automatically set upon creating a new form, and it cannot be changed.It can be copied to clipboard by clicking on the icon.

#### Instance ID 
The instance ID. It can be copied to clipboard by clicking on the icon.

#### Created 
The date/time of when this form was created.

#### Last saved 
The date/time of the last save. The save action takes place upon every change, even if it is not immediately reflected in this

#### Composer version 
The version of the Composer UI currently used.

#### Backend version 
The version of the Composer backend currently used.

