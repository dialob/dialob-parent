---
id: 04-input-and-output-types
sidebar_position: 4
title: Input and output types
---

# Text and Textbox types

## Quick Summary

* Text and Textbox both return text (string)
* The only difference between them is how they are rendered on the filling side

---

## Overview  

Both text and textbox return text (string) values. There is no length difference between the maximum text length for either of these. 

The only difference between them is how they appear on the filling side.

* **Text**: A single-line text field
* **Textbox**: A multi-line text field

---

On the Composer side, they both look the same:

<img width="1172" alt="04-01" src="https://github.com/user-attachments/assets/7089c78b-317e-46df-8d8a-a21fd725c7d3" />


But on the filling side, the rendering difference is visible:

<img width="1092" alt="text-textbox" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/9e21b39f-f127-4160-bf09-dca000221fdf" />

## Creating a new text response

1. Select "Add item" --> "Structure" --> "Group" 
2. Select "Add item" --> "Inputs" --> "Text" / "Text box"

---

# Number type

## Quick Summary

* Number responses may only be whole numbers
* Number return type is `Integer`

---

## Overview

Number responses are used in the case where whole numbers are required. Decimal points will be ignored on the filling side so as to provide "automatic validation" that only whole numbers are entered. See examples:

<img width="1087" alt="number-example" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/e9fdb04e-1ba2-40a8-999a-becfa7b03e09" />

---

## Creating a new Boolean response

Creating a new Number response works in the same way as other types:

1. Select "Add item" --> "Structure" --> "Group"
2. Select "Add item" --> "Inputs" --> "Integer"

---

## Validation example

### Ensuring a number falls within a specified range

In this example, we create a response where the user is required to enter a number between 1 and 10. If a response is not within that range, our validation message will appear. This example shows a situation where the input does not match the validation requirements.

**Validation expression**: `answer < 1 or answer > 10`
**Another version of a valid expression**: `number1 < 1 or number1 > 10`

**Validation message**: "Number must be between 1 and 10, and your number doesn't fall in this range!"

**Expected result**: The user is going to enter a response of 15. This will fall outside of the acceptable range and cause the validation message to appear. 

On the Composer side

<img width="894" alt="04-02" src="https://github.com/user-attachments/assets/435e421e-f8af-4bc0-a956-f7c603213af4" />

On the filling side

<img width="1116" alt="number-validation2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/08f38431-e0c4-4922-88f8-c97d0f4d8955" />

---

# Decimal type

## Quick Summary

* Decimal responses may be entered as whole numbers and as decimal values
* Decimal return type is a decimal number 

---

## Overview

Decimal responses are used in cases where more precision is needed than whole numbers can provide:

See example:

<img width="1086" alt="decimal-example" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/ae4195ec-9af5-46a4-b0df-a556bf5c9645" />

---

## Creating a new decimal response

Creating a new Number response works in the same way as other types:

1. Select "Add item" --> "Structure" --> "Group"
2. Select "Add item" --> "Inputs" --> "Decimal"

---

## Validation example

### Checking that a decimal value is greater than or less than zero

In this example, we are going to validate that a user enters a decimal value greater than zero. If the input is less than or equal to zero, we will trigger a validation message.

**Validation expression**: `answer <= 0`
Another version of a valid expression: `decimal1 <= 0`  

**Validation message**: "You cannot add 0 or less to your account!"

**Expected result**: The user is going to enter a response of -5. This will fall outside of the acceptable range and cause the validation message to appear. 

On the Composer side

<img width="893" alt="04-03" src="https://github.com/user-attachments/assets/6e2e71be-8a26-47a4-976f-53bff04f8462" />

On the filling side

<img width="1085" alt="decimal-validation2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/50a2c952-6933-4810-82c7-014868cba288" />

---

# Boolean type

## Quick Summary

* Boolean is used for yes/no or true/false questions.
* Return type will be a boolean value: `true` or `false`. 

---

## Overview

Boolean responses are used for true/false situations. When you create a Boolean response, a button will be rendered on the filling side with YES/NO selection options. See example:

<img width="1089" alt="boolean-example" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/f8cd58b2-ad84-4fe7-b7c0-d555b2007ff8" />

---

## Creating a new Boolean response

Creating a new Boolean response works in the same way as other types:

1. Select "Add item" --> "Structure" --> "Group"
2. Select "Add item" --> "Inputs" --> "Boolean"


## Validation examples

### Example: Checking that user-entered information is correct

In situations where typos are easy to make, such as when entering addresses, it is useful to add a boolean check to prompt the user to confirm that the information they entered is correct.
For this example, we will create the following:

1. An address input type
2. A boolean input type with
  * A _visibility rule_ to trigger the input's appearance once the address input type has been edited 
  * A _requirement rule_ set to `true`, which will force the user to answer it before the form can be completed


**Validation message**: "Is the above information correct?"  
**Visibility rule**: `address1 is answered`  
**Requirement rule**: `true`  

On the Composer side: 

<img width="902" alt="04-04" src="https://github.com/user-attachments/assets/b8dd7d05-55c4-4719-861f-0a1ab625d818" />
<img width="894" alt="04-05" src="https://github.com/user-attachments/assets/9af1e312-2e97-480d-bc3f-f234c1b8c46e" />


***Note:** When rules are added to an item, there is an indicator present. In this example, a gavel icon is used to indicate that a requirement rule has been set. Clicking on the icon will show the rule that has been set.*

On the filling side:

![boolean1](https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/b67ae997-f30b-415c-804f-020b874be141)

---

# Date type

## Quick Summary

* Date type creates a date picker on the filling side
* Date type returns a date in the format of “yyyy-mm-dd”. [See more on ISO Date](https://www.w3.org/QA/Tips/iso-date)

---

## Overview

Date type creates a date picker in the form of a calendar on the filling side:

<img width="1185" alt="date-picker" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/3c2c50a7-8cb7-4381-b3ef-5a37fbe81ffb" />

---

## Creating a new date response 


Creating a new Date response works in the same way as other types:

1. Select "Add item" --> "Structure" --> "Group"
2. Select "Add item" --> "Inputs" --> "Date"

---

## Validation examples

### Example A: A date is not in the past

A typical use case for Date type is verification that a user-selected date is **not** in the past. This operation can be accomplished with the `today()` function and the following DEL notation entered into the date response type validation field:

`responseId < today()`

In the following case, our response id is `date1`, and the expression is validating whether `date1` is **earlier** than today's date:

`date1 < today()`

<img width="904" alt="04-06" src="https://github.com/user-attachments/assets/bbba941c-c13d-463d-a9ae-4fc033d56734" />
<img width="894" alt="04-07" src="https://github.com/user-attachments/assets/fd3d6a1c-c42b-4095-ae8d-5cbeb7347999" />


This validation expression will trigger our validation message when a user selects a date in 1995:

<img width="1170" alt="date-in-past2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/1caaff39-ded4-4372-a87e-71fa500f22b1" />


### Example B: A date is not in the past, not in the future, and a time limit applies to the situation 

Another typical use case builds on the first example. In the context of an insurance claim, we want to ensure three things:

1. A user-entered date is not in the past
2. The claim data cannot be in the future
3. If the incident happened more than 30 days ago, the claim is no longer valid


**Validation message**: "Incident date cannot be in the future, and incident date cannot be more than 30 days in the past. Please check entered dates for these criteria."  
**Validation expression**: `incidentDate > today() or incidentDate < today() -30 days`

In plain language, this Validation expression says   

_"incidentDate cannot be later than today, and incidentDate cannot be earlier than 30 days before today"_

**Expected result**: The user is going to enter a date which is more than 30 days in the past. This will trigger the validation message. 

On the Composer side:

<img width="904" alt="04-08" src="https://github.com/user-attachments/assets/8459b398-5037-4eb0-8e53-dabed7d22526" />
<img width="892" alt="04-09" src="https://github.com/user-attachments/assets/b02c62f9-133b-4198-a3d1-e45b6658015e" />

On the filling side: 

<img width="1106" alt="date-validation2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/440b936c-a05a-4ee4-a462-0b2d64a67c71" />

---

# Choice type

## Quick Summary

* Choice type functions with an attached list
* It allows users to choose **only one** option from the list
* A Choice return type will be an ID of selected row in the list
* Creating a Choice and a Multi-choice type follows the same process

---

## Overview

Choice type utilises a global or local list to populate a single-choice selection menu.

---

### Creating a new choice response 


**To create a Choice type, follow the steps of creating a multi-choice input type:**

1. Create a group to contain your choice response: Select "Add item" --> "Structure" --> "Group"
2. Create the choice input item: Select "Add item" --> "Inputs" --> "Choice"
3. Create or apply global or local list which will form the individual choice items: Select the hamburger icon in the top right corner of the question window and select "Options". You will then be given the option to apply a global list or create a local list.

Below is an example of a choice type on the filling side. The choice list has four items, and their associated IDs are `opt1`, `opt2`, `opt3`, `opt4`.

<img width="1077" alt="choice1" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/f8a7fb7a-ae47-4bbf-819c-dccd03b06a18" />

---

## Syntax for Choice type logic writing

When using logic with Choice type, the syntax is as following:

When matching a single item: `responseId = "listItemId"`  
Evaluates true when the return value of question1 is opt1.

When matching multiple items: `responseId in ("listItemId1", "listItemId2", "listItemId3")`  
This evaluates true when the return value of question1 is one of the following: opt1, opt3 or opt4 possible *Choice* key values.  

When matching multiple items: `responseId not in ("listItemId1", "listItemId2", "listItemId3")`  
This evaluates true when the return value of question1 is NOT one of the following opt1, opt3 or opt4 possible *Choice* key values.

---

##  Validation example 

In this example, we want to evaluate against a single choice option. Depending on this choice, a corresponding note output will be displayed.

To do this, we create a group with a Choice type. The Choice type has a local list attached to it with three values. We have two note outputs. We write visibility rules for the note outputs and set them to be shown depending on which option the user selects from the Choice list.

**Note visibility expression**: `list2 = "countryside`  

**Expected behaviour**: When a client selects "Rural Countryside" from the Choice list, the Note output visibility rule will be triggered, and note content will be output.

On the Composer side:

<img width="905" alt="04-10" src="https://github.com/user-attachments/assets/aac44ceb-f8d6-4e7f-9c76-23e795ffb7dc" />
<img width="891" alt="04-11" src="https://github.com/user-attachments/assets/cabc8ada-f1c1-4f53-8dd7-4e825da2dec2" />

On the filling side:

<img width="1091" alt="choice-example2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/39f63bee-1cb6-4884-8929-7b87659d01a9" />

---

# Multi-choice type

## Quick Summary

* Multi-choice type functions with an attached list
* It allows users to choose **one or more** items from the list
* A multi-choice return type will be a set of an ID of selected rows from the list
* Creating a Choice and a Multi-choice type follows the same process

---

## Overview  

Multi-choice type utilises a global or local list to populate a multiple-choice selection menu.

Below is an example of a multi-choice type on the filling side: 

<img width="1150" alt="multi-choice-example" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/70a9416e-c9ab-475c-aa18-bb6be36c3752" />

---

## Creating a new multi-choice response


**To create a multi-choice response, follow the steps of creating a choice response:**

1. Create a group to contain your multi-choice response: Select "Add item" --> "Structure" --> "Group"
2. Create the multi-choice input item: Select "Add item" --> "Inputs" --> "Multi-choice"
3. Create or apply global or local list which will form the individual multi-choice items: Select the hamburger icon in the top right corner of the question window and select "Options" and navigate to "Choices" tab. You will then be given the option to apply a global list or create a local list.

---

## Visual guide to creating a multi-choice type

* **Create a group to hold your response and add a multi-choice input.**

<img width="904" alt="04-12" src="https://github.com/user-attachments/assets/5ea231d5-00c8-43d9-863a-a8eaa0dfbb7b" />

* **Write your group label and multi-choice label.**

<img width="903" alt="04-13" src="https://github.com/user-attachments/assets/c7c4d320-1884-4bff-9a65-66f78ba535ff" />

**Create a global list of input items.**

<img width="891" alt="04-14" src="https://github.com/user-attachments/assets/e5c6fb0c-5a1e-4183-ae94-d9c96d784e90" />

**Click the hamburger icon in the top right corner of the multichoice and select `Options` and navigate to "Choices" tab. Apply the created global list.**

<img width="893" alt="04-15" src="https://github.com/user-attachments/assets/1d41d13d-6359-4e75-85f9-99d7dec47e2b" />

**The filling side preview**

<img width="1126" alt="multi-choice-after" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/0049fbf8-a342-48d0-94bc-cc4cf6b97466" />

---

## Syntax for Multi-choice type logic writing 

To write rules to evaluate Multi-choice responses, use the ID of the question you wish to evaluate against, followed by the `in` keyword, and then the ID of the Multi-choice question.

Example: `"id1" in multichoice1`

More examples: 

When matching a single item: `"responseId1" in multichoiceId1`  
Evaluates true when `responseId1` is selected from `multichoiceId1`

When matching a single item: `"responseId1" not in multichoiceId1`  
Evaluates true when `responseId1` is **not** selected from `multichoiceId1`

When matching multiple items within the same multi-choice response: `"responseId1" not in multichoiceId1 or "responseId2" not in multichoiceId1`  
Evaluates true when `responseId1` and `responseId2` are **not** selected from `multichoiceId1`

---

## Validation example 

### Specifying the number of selections a user must make 

To validate/specify the number of choices within a given multi-choice request, use the `count( )` function and create validation logic to fit your needs.  

The example below shows a situation where the user is not permitted to choose nothing, and they are required to select at least two choices. 

On the Composer side:

<img width="892" alt="04-16" src="https://github.com/user-attachments/assets/5f4ba02f-60ff-4c45-bb51-0e681b73d3cf" />

On the filling side, **before** a choice is made:

<img width="1110" alt="multi-choice-validation2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/604d0c55-c90f-4b3d-9057-d3438e1a2e39" />

On the filling side, **after** the correct number of choices is made:

<img width="1110" alt="multi-choice-validation3" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/0d194379-0dab-4d96-be4e-164859c0bd9c" />

---

# Multi-row type

## Quick Summary

* Multi-row creates an inline row of any number of input fields
* The number of rows in a multi-row element directly corresponds to the number of input fields created.
* Multi-row-specific operations 
  * Boolean operations: `any of`, `all of`
  * Mathematical operations: `sum of`, `min of`, `max of`
  * Count the number of row items currently selected: `count(rowgroupId)`

---

## Overview

Multi-row creates an inline row of any number of input fields. The number of input fields created will determine how many rows the multi-row input will consist of.  

Below is an example of a multi-row input with three input fields.

**Composer side**

<img width="917" alt="04-18" src="https://github.com/user-attachments/assets/66e16331-bc41-4845-810f-dfd4078dd069" />

**Filling side**

Note the "Add new" button. This will add as many identical rows as needed.

<img width="1079" alt="multi-row-filling-example" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/46d9803c-457d-4625-9092-52c3df30137a" />

**Filling side after selecting "Add new" row multiple times.**

<img width="1075" alt="multi-row-filling-example2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/0e2d0ef8-ad9d-4db4-9bfc-1a4d3c7960b5" />

---

### Creating a multi-row response 


**To create a multi-row group:**

1. Create a multi-row group to contain your response: Select "Add item" --> "Structure" --> "Multi-row group"
2. Create the label(name) for the multi-row group, which will serve as the prompt for the user on the filling side.
3. Create the input fields: The input fields created here will be the visible fields on the filling side.

<img width="923" alt="04-17" src="https://github.com/user-attachments/assets/570771f7-fdb8-4cea-875d-3b7558f34edf" />

---

<img width="917" alt="04-18" src="https://github.com/user-attachments/assets/0616615d-5499-40e9-913c-f88ad56ba63e" />

---

## Multi-row-specific operations 

There are a number of DEL keywords that are specific to multi-row and perform various functions on multi-row elements:

Mathematical operations: Applicable only to `number` and `decimal` types

* `sum of`: Returns the sum of multi-row fields 
* `max of`: Returns the highest value of multi-row fields
* `min of`: Returns the lowest value of multi-row fields

Boolean operations: Applicable only to `boolean` types

* `any of`: Returns `true` if any boolean value in multi-row elements is selected `true`
* `all of`: Returns `true` only when **all** boolean values in multi-row elements are selected `true`

Counting the number of active row items:

* `count(rowgroupId)`: The count function keeps track of the number of active rows in a multi-row group. Every time a new row item is added via the "Add" button, count is incremented by 1.

---

## Using multi-row-specific operators


### Boolean operations 

This example demonstrates `any of` and `all of` in action.  To create this example, we need to do several things:

1. Create a multirow group
2. Create two multirow elements of type `Boolean`. The input IDs are `boolean1` and `boolean2`. 
3. Create two Expression Variables which will contain our `any of` and `all of` operations
4. Create two Note type outputs to provide additional information on the filling side

#### Steps 1 and 2: Create a multi-row group with two boolean inputs: boolean1 and boolean2

* id: `boolean1` field label text: Boolean1: I return true if any of my rows are true  
* id: `boolean2` field label text: Boolean2: I return true only when ALL of my rows are true  

<img width="1174" alt="04-19" src="https://github.com/user-attachments/assets/00a88297-7727-4dac-a5cf-3c671e25de89" />

#### Step 3: Create Expression Variables

* id: `bool1` expression: `any of boolean1`
* id: `bool2` expression: `all of boolean2`

<img width="894" alt="04-20" src="https://github.com/user-attachments/assets/aa579055-e251-45fe-858d-13923b1d653e" />

#### Step 4: Create Note outputs within which to call Expression Variables

Create two different note outputs

id: `note1` text: Boolean1: Are any of my rows true? 
id: `note2` text: Boolean2: Are all of my rows true? 

<img width="1176" alt="04-21" src="https://github.com/user-attachments/assets/1ebdb079-44de-40f4-a69a-1faa364b28cf" />

#### Test

After adding three rows, we select true/false and view our note output. 

<img width="1110" alt="multirow-bool-filling" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/423eb274-b8bc-4b7f-9f96-36c599895a60" />


### Mathematical operations

We can use `sum of`, `min of`, and `max of` with `number` and `decimal` types in the same way as we just did with Boolean types.

The screenshots below were produced following the same steps as we took to produce the Boolean multirow screenshots. 

Creating variables

<img width="894" alt="04-22" src="https://github.com/user-attachments/assets/065efeef-84ba-4dfa-9214-2edcfa1bae44" />

Creating inputs

<img width="1171" alt="04-23" src="https://github.com/user-attachments/assets/0b1eebb7-b586-4bd1-9ece-107db8e6550c" />

Creating notes

<img width="1170" alt="04-24" src="https://github.com/user-attachments/assets/2589bd35-3686-4bbc-982b-72192cef8ee4" />

Filling side test

<img width="1096" alt="multirow-math-filling" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/b944b0f7-052e-49ff-89ba-86eeb67827cb" />


---

## Count the number of row items currently selected using the `count(rowgroupId)` function

It is possible to write logic to handle operations which are based on the number of currently active multi-row items. For example, you may want to show an input field only when/if a user adds more than a certain number of multi-row items. In this situation, you can use the `count(rowgroupId)` function to keep track of the number of selected row items.  On the filling side, every time the user clicks the "Add" button within the multi-row group, `count()` will increment by 1.

To use `count()` in logic rules, you will need to supply it with the ID of the multi-row group that you want it to operate on: For example: `count(myMultiRow3)`.


The example below demonstrates the filling side with the following:

1. A note output that will display the number of selected row items
2. A text input with a visibility rule that will be triggered whenever a user has added more than three multi-row items.

**Screenshots**

Filling side before selecting any rows:

<img width="1456" alt="multirow-count" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/622703e1-0a55-4ea1-8007-bda5df99dd09" />

Filling side after selecting 4 rows and triggering visibility rules:

<img width="1465" alt="multirow-count1" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/52e0ef8b-a88b-432d-a458-74dfccbf48f3" />

Composer side:

<img width="1468" alt="04-25" src="https://github.com/user-attachments/assets/a7da399e-c625-47dd-a631-31c7ed79cb19" />

### Creating a similar example

See the screenshot above for numbered assistance in following these instructions.

1. Create a new rowgroup and give it as many input fields as needed
2. Create a note output. It can be inside or outside of the multi-row group. (In the above example, the note output is **outside** the row group).
3. Create an expression variable to use within the Note output. 
    * Example expression variable: ID: `count1`, Expression: `count(rowgroup1)`
4. Add your expression variable to your note along with a descriptive text. Remember that when using an expression variable within a field label, surround it with curly braces. 
    * Example note text: `You have selected {count1} multi-row items.`
5. Add a new group.
6. Add an input type in that group. 
7. Write a visibility rule for that input that will determine when it will be shown. The rule below will show this input field if there are more than 3 active multi-row items.
    * Example Visibility Rule: `count(rowgroup1) > 3`
8. Test with Preview


---

## Visibility-logic example 

### Showing an additional field within a multi-row type depending on a previous answer 

In this example, we want to show a particular field within a multi-row item only if the user selects a particular boolean value.

* We create a multi-row group with three input fields (two text fields and one boolean). 
* We write a visibility rule to trigger the visibility of a fourth field (`list1`, a choice menu), if the user answers "Yes" to `boolean1`. 

Our items are as follows: 

`text2`: First Name  
`text4`: Last Name  
`boolean1`: Do you wish to be added to our mailing list?  

...if the user answers "yes".... then show

`list1`: Please select the topic you are most interested in.  
Visibility rule: `boolean1 = true`  

On the Composer side

<img width="1172" alt="04-26" src="https://github.com/user-attachments/assets/c61094df-2d28-4a53-851c-0a28ca436cd6" />

On the filling side

<img width="1113" alt="rowgroup-visibility2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/b79247e6-91ff-4be5-a5a6-b7c560edaa7d" />

---

# Survey type

## Quick Summary

* A complete Survey response type is comprised of three parts: Survey group, survey options, and survey inputs
* A Survey return type is a key
* Survey buttons can be horizontally or vertically arranged
* Follow the (visual) guide below to create a Survey response type.

---

## Overview  

Survey type uses radio buttons to collect input from users. Below is an example:

<img width="1118" alt="survey-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/761136f4-b4aa-4f8a-b736-122d1c26f385" />


Survey types function a bit differently than other types.  For example, where a **Text** response type is located within a group which can contain any number of various response types, a **Survey** response type is the group itself. 

**A complete Survey response type is comprised of three parts:**

1. The **survey group**, which can be thought of the "question" itself.
2. The **survey options** which are created with a global or a local list. 
3. The **survey inputs**, which are text fields. 

In summary, one survey group is the equivalent of one survey "whole" encompassing these three elements. This example below illustrates the three cohesive elements of a Survey response type as they appear on the filling side.

<img width="1118" alt="survey-filling-side-b" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/03e96105-f9b4-4b31-8c85-8054b72f57d7" />

---

## Creating a new survey response

A Survey response type is created as a Survey Group or Survey Group (Vertical). 

To create a survey:

* Click "Add item"
* Select "Structure"
* Select "Survey Group" or "Survey Group (Vertical)"
* Give your survey question a name by writing in the Survey Group label field
* Create a global/local list and attach it to the Survey group via the Survey Group-level hamburger icon + "Options" hamburger.
* Create survey inputs by clicking "Add item" in the bottom left of the Survey Group, and select "Survey Item". Write your text accordingly.

Follow the guide below for screenshots of this process.

---

## Visual guide to creating a survey group
 
**Click the "Add item" button and select "Structure", then "Survey Group".**

<img width="1191" alt="04-27" src="https://github.com/user-attachments/assets/2acb96c1-6a96-4c80-9c62-4dc529fbdf55" />


**Next, survey values are needed to populate the Survey group. Create a list (Global or local) which will comprise your inputs.**

<img width="893" alt="04-28" src="https://github.com/user-attachments/assets/b6e37c3e-c3ba-49df-8a2d-81b184980909" />

**Next, apply your list of survey values to a survey group**  

Click the hamburger icon in the top-right corner of the survey group. Then, select the list you wish to apply.

<img width="895" alt="04-29" src="https://github.com/user-attachments/assets/6d707347-a451-4c34-81ba-43f6848d6fe3" />


**After that, create survey inputs by creating a survey input within the survey group**.

<img width="1158" alt="04-30" src="https://github.com/user-attachments/assets/517228f1-0f10-43aa-bd40-a021d0240f97" />


---

## Syntax for Survey type logic writing: Matching single and multiple items 

When matching a single item:  
`question1 = "opt1"`  
Evaluates true when the return value of question1 is opt1.

When matching multiple items:  
`question1 in ("opt1", "opt3", "opt4")`  
Evaluates true when the return value of question1 is one of the following: opt1, opt3 or opt5 possible *Survey* key values.

When matching multiple items:  
`question1 not in ("opt1", "opt3", "opt4")`  
Evaluates true when the return value of question1 is NOT one of the following: opt1, opt3 or opt4 possible *Survey* key values.

## Typical visibility-logic example 

For this situation, we create a customer satisfaction survey. If the user indicates via a survey item that a service was "poor", we want to trigger a text input to appear so that we can collect additional information on how to improve in that area of service.

We create a survey group and three survey inputs:

* Friendliness of staff
* Response time for service inquiries
* Quality of solution to my problem

We create three survey options via a local list. These options describe the quality of service:

* `opt1` :  Poor
* `opt2` :  Acceptable
* `opt3` :  Good

We add a visibility rule to a text field inside the survey group that will appear on the filling side if a user selects `opt1: poor` and to describe "Staff Friendliness".

**Text input**: "Please tell us how we can improve our staff friendliness"
**Visibility rule**: `survey1 = "opt1"`

On the Composer side:

<img width="1171" alt="04-31" src="https://github.com/user-attachments/assets/19b8da78-b6c1-4d05-980d-dc3d763720ab" />
<img width="894" alt="04-32" src="https://github.com/user-attachments/assets/b19f2212-02c0-4fa0-9a8e-5aa2d6d2c49f" />

On the filling side:

<img width="1107" alt="survey-example2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/cfdb819c-cc68-4324-b339-5cbb5956f608" />

---

# Time type

## Quick Summary

* Time type returns a time in the format of "hh:mm:ss".
* Time type appears as a time picker on the filling side.

---

## Overview  

Time type creates a time picker on the filling side:

<img width="1185" alt="time-picker" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/3febc6d1-4b6b-4503-8489-ce635b3de212" />

---

## Creating a new time response

1. Select "Add item" --> "Structure" --> "Group" 
2. Select "Add item" --> "Inputs" --> "Time"


## Validating Time type 

Logical operators can be used to determine the relation of different time values to each other.

Using the "less than" `<` operator essentially means "earlier than" in Time type.  The "greater than" `>` operator essentially means "later than".


For example:

To check if Time1 is earlier than Time2, use the "less than" operator: `>` 

`time1 > time2`  

On the Composer side:

<img width="1169" alt="04-33" src="https://github.com/user-attachments/assets/85f0e166-ef1e-4069-8bbb-cd2eeb4b45c8" />
<img width="896" alt="04-34" src="https://github.com/user-attachments/assets/3beb1df4-951d-4f51-a3d9-a7c026defafe" />

On the filling side:

![time2](https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/ce1df387-2e32-47fa-aeb8-603204a8803c)


