---
id: 05-dialob-expression-language-del
sidebar_position: 5
title: "Dialob Expression Language (DEL)"
---

# Basics of DEL

## Quick Summary

* DEL expressions resemble traditional grammar and have a subject (Request ID), verb (operator or function), and condition (elements or values to compare against or evaulate against).
* DEL reserved words operate similarly to verbs (is answered, is not answered, is valid, etc.) or connecting words (and, or).
* Functions are actions. Functions are reserved words followed by parenthesis. The thing you want to perform the action on goes into the parenthesis. Not all functions require inputs in the parenthesis. 
* A String type is the same as a "text" type, which includes letters, numbers, and punctuation.
* DEL expressions do not end in any punctuation such as full stops or semi-colons.
* DEL expressions are if/then statements, where the **if** part is written by the user, and the **then** part is handled and evaluated by Dialob Manager

---

## Overview: Introduction to Dialob Expression Language (DEL)

* **DEL: Dialob Expression Language** was designed for use with Dialob Composer
  * It is used to powerfully yet simply define the control logic of online forms. 
* DEL is continuously enhanced and improved with new features to cover more complex cases and extend its usability to new areas.

---

### Concepts of DEL in linguistic terms

Let's think about DEL in terms of simple grammar. All sentences have a subject and a verb.

A **subject** is what the sentence is about.  
A **verb** shows the action in the sentence. 

Let's see an example:

"A customer's age is at least 21."

In this sentence, _customer_ is the subject, and _is_ is the verb. In Dialob, you will be writing expressions both similar and more complex than this, which will comprise the core of your form logic.  So, let's "translate" this English sentence into DEL to see what it looks like. 

For one of our form questions, we need to validate that a customer is at least 21 years of age, because if he/she is younger than 21, we cannot sell certain products. In DEL, you can write this expression as follows: `customerAge >= 21`

In this example, `customerAge` is the Request ID (subject), `>=` is the logical operator (verb), and "21" is the value that we are to validate against.  We will revisit these terms shortly.

---

### Syntax of a DEL expression: Examples

A DEL expression is basically an **If/Then statement**.  The "If" part is written by the user, and the "Then" part represents the result of the expression. The "then" part can evaluate to `true` or `false` based on how the user writes the "If" part.  For example, here we have two questions. The second question should only appear if the user specifies in the previous question that his/her age is greater than 25. 

```javascript
question1:  How old are you?
Answer1: 25

question2: Are you married?
Visibility Rule: question1 > 25 
```

Here, `question1 < 25` is the "if" part of the DEL expression. The "then" part is implied.  **If** the user enters his age as 26, **then** the statement will evaluate to true, and **then** this Question 2 will be displayed. However, **If** the user enters his age as 16, **then** this statement will evaluate to false, and **then** Question 2 will not be displayed.


A DEL expression can have several basic parts:

* [Request ID](https://github.com/digiexpress-io/digiexpress-parent/wiki/Dialob-composer:-03%E2%80%90Advanced-operations#request-ids-and-rules-for-writing-them) or reference to a request ID (not required to be used in `Required` fields)
* [Logical operator](https://endjin.com/blog/2013/07/learning-to-program-a-beginners-guide-part-eight-working-with-logic)
* [Reserved word](https://www.bouraspage.com/repository/algorithmic-thinking/what-are-reserved-words)
* [Function](https://www.makeuseof.com/what-is-a-function-programming/)

[List of Dialob Expression Language Operators](#del-operators)  
[Overview of Dialob Expression Language Functions and Reserved Words](#del-functions-and-reserved-words)  

In the following expression, we can see three of these elements:

`text1 = "happy"`  

`text1` is the Request ID  
`=` is the logical operator  
`"happy"` is the String value we wish to validate against  

**NOTE: "String" is a programming term which means "text". Text can be any character, including punctuation, letters, and numbers.**

`text1` can be thought of as the subject, `=` is the verb (is / is equal to), and `"happy"` is akin to the condition.

Let's look at some more examples:

`age > 25 and favouriteColour = "green"`  

In this expression, we have two Request IDs: `age` and `favouriteColour`. There are two logical operators: `>` and `and`.  There are also two predicate expressions: 25 and "green".

This sentence can be "translated" into English as the following: "**If** age is greater than 25 and favourite color is green, **then**..."  

---

### Response types and punctuation

In the examples above, you might have noticed that some elements were surrounded by quotation marks and others were not.  Here is the explanation:

**No quotation marks**: If you are referencing a number, there are no quotation marks around the number.

`numberOfCats >= 35`  

**Quotation marks**: If you are referencing a String (text), the text has to have quotation marks around it.

`catName = "Tiger"`

**Referencing a particular selection from a Choice list**: If you want to reference a particular choice that a user selected from a `Choice` drop down list, the ID of the choice must be surrounded with quotation marks and the requestID of the question to which the choice list is attached must be in the beginning of the expression.

`question1 = "opt1"` **Translation**: The selection from question1 must be opt1.

**Referencing more than one selection from a Choice list**: If you want to specify more than one predicate (answer), you still need quotation marks around the individual answers, but all of the answers must be grouped and surrounded by parenthesis.  

`coloursList = ("green", "orange")` **Translation**: The selection from coloursList must be green OR orange.  

DEL expressions do not end with any punctuation like full stops or semi-colons.

---

### Using reserved words

Reserved words function as "verbs" in DEL expressions. They will go after the "subject" of the sentence (Request ID).  

Here are some examples:

`text1 is answered and text1 is valid` **Translation**: If the question whose ID is text1 has been answered **and** the answer to text1 is valid, then...  

`survey4 not in "cat"` **Translation**: If the user does NOT select "cat" from the survey question whose ID is survey4, then...

The next example shows a validation rule for multiple selections from a mult-choice list:

`carsOwned in ("Opel", "Renault", "Audi")` **Translation**: If the question whose ID is carsOwned has been answered by the user, who selected either Opel, Renault, or Audi....  

---

### Using functions

Functions are statements that perform actions. A DEL function is a reserved word followed by parenthesis `( )`.  The word will trigger an action, and this action will be performed on whatever is inside the parenthesis. An example function is as follows:

`wash()`  

**Translation:** The `( )` show that this is a function (action). However, there is nothing inside the parenthesis for our wash function to actually wash. As it is written, this function will simply wash, but becasue we haven't defined what it will wash, it will just wash in a generic sense.  So, let's add something to wash.

`wash(laundry)`  

Now, our function will perform the action of wash on the object of laundry.  Functions in DEL work the same way. Here are some real examples:

`lengthOf(text1)`  **Translation**: This function will check the length of the question whose ID is text1.  

`lengthOf(text1) > 5` **Translation**: This function will check that the length of text1 is greater than 5 characters.

Not all functions need to have anything entered into the parenthesis. For example, the function `now()` will simply fetch the current system time.  

---

# DEL operators

## Quick Summary

* Use operators to write logic rules
* Basic DEL comparison operators: `=` , `!=` , `>`, `>=` , `<` , `<=`
* Basic DEL keyword-based basic operators: `and`, `or`, `in`, `not in`
* Advanced operators include the keyword phrases `is answered`, and `is valid`
  * These two operators produce different behaviours on the filling side

---

## Overview

Operators are symbols or words used to connect and evaluate two or more expressions to produce a result.  In Dialob, operators are used for writing the various logic rules (validation, visibility, and requirement).

DEL utilises several types of operators, including:

* Comparison operators
* Symbol-based operators
* Keyword-based operators

---

## Available operators

Below is a description of the basic DEL operators, which includes comparison operators, symbol-based logical operators, and keyword-based basic operators and their corresponding input types.

### Basic Comparison Operators

* **=**: **Equal to** is valid with all default Response types

* **!=**: **Not equal to** is valid with all default Response types

* **>**, **&lt;**: **Greater than** and **Less than** are valid with Integer, Decimal, Date and Time Response types

* **>=**, **&lt;=**: **Greater than or Equal to**, and **Less than or Equal to** are valid with Integer, Decimal, Date and Time Response types

### Basic Keyword-based operators

* **and**: To combine a set of comparisons for evaluation as one entity

* **or**: To evaluate a set of comparisons as separate entities

* **in**: Checks if a given set of unique key(s) is found in the list to which it is compared. Valid Response types are Choice and Multi-choice

* **not in**: Checks if a given set of unique key(s) are found in the list to which it is compared. Valid Response types are Choice and Multi-choice

---

### Advanced Keyword-based operators 

* **`is answered`**: Checks if a given request has a response.  Useful in cases where visibility of a particular question is based on whether a previous question has been answered on the filling side.
* **`is valid`**: A special comparison that checks if all Required and Validation conditions are passed for a given, answered request. `is valid ` can be used in a situation, for example, to define that a particular page, group, or question will not be displayed until all Required and Validation errors have been addressed and will only be displayed **while** the validation and required conditions **are being met.**  

## How are `is valid` and `is answered` different? 

The behaviour of these two operators can be seen in how questions are displayed on the filling side. 

`is answered` can be used in situations where a request field is to be displayed **only when a response to a previous question is entered, regardless of whether it passes validation rules**, i.e.: _it does not matter if the previous response's validation rule has been met._  The question field will remain visible as long as there is some data entered in the previous response field.
  * Example: `vatNumber is answered and europeanCountries = "germany"` will cause a response field to appear as soon as there is some data entered into VAT number field and "Germany" has been selected from a list.  If VAT number field is deleted by the form filler or "Germany" is deselected from the list, the response field will also disappear until data is entered again in VAT field and "Germany" is selected.

`is valid` can be used in situations where a request field will **only** be displayed **while a previous response is currently entered _and_ that response _is currently passing_ all validation rules.** If the response is edited and no longer matches validation rules **or** that response is deleted, the response field with `is valid` in its validation/visibility will disappear. It will only appear again once validation / required rules have been met.
  * `vatNumber is valid and list1 = "estonia"` will cause a response field to appear **only as long as** an entered VAT number is passing validation **and** "Estonia" is currently selected from a list. If either condition is altered, the field in which this validation/visibility rule is written will disappear until the conditions are met again.

---

# DEL functions and reserved words

## Quick Summary

* Dialob Expression Language (DEL) comes with built-in functions and keywords to help create form logic

---

## Overview  

* **DEL** functions are built via Java and Groovy and are automatically loaded into the dialog context when creating a new dialog.
* **DEL** function library is continuously being improved, and it is possible to extend it.

---

### Existing DEL functions: 

* **`today()`**: Gets the system date (where **Dialob Manager** is deployed) in ISO format "yyyy-mm-dd". Return type is *Date*.  

* **`now()`**  Gets the system time (where **Dialob Manager** is deployed) in ISO format "hh:mm:ss.sss". Return type is *Time*.

* **`lengthOf(*Text*)`**: Provides the length of *Text* type. White spaces before and after the response are counted. Return type is *Number*.

* **`isLyt(*Text*)`**: Checks if a given input is a valid FI company ID. Format is nnnnnnnn-n (for example, 12345678-3). Return type is *Boolean*.

* **`isHetu(*Text*)`**: Validates if given FIN personal ID (Hetu) is valid. The format of ID is ddmmyy-nnnl. Return type is *Boolean*.

* **`birthDateFromHetu()`**: Calculates a birthday from FIN personal ID (Hetu). Return type is *Date*.

* **`count(*Multi-choice type*)`**: Provides the number of selected items of **Multi-choice** type of request. Return type is *Number*.

* **`isIban()` , `isNotIban()`**: Used for validating IBAN numbers. Can contain spaces.

* **`format(*Text*)`**: Builds a string by interpolating item values into a template, using the same `{itemId}` placeholder syntax as note and question labels. The argument must be a string literal. Return type is *Text*. This is typically used in expression variables to concatenate strings together with answer values.

  The template supports the same placeholder formats as labels:
  * `{itemId}` inserts the value of the item (for choice items, the selected entry's label).
  * `{itemId:key}` inserts the raw stored key of a choice item rather than its label.
  * `{itemId:uppercase}` / `{itemId:lowercase}` change the case of the inserted value.
  * `{itemId:#,##0.00}` formats a number with a custom decimal pattern.

  Example expression variable value: `format('Hello {firstName}, your total is {total:#,##0} euros')`. The variable automatically recomputes whenever any referenced item value changes. Note that placeholders reference items by ID only; to interpolate a calculated value, define an expression variable for it and reference that variable's ID (for example `{sum}`, not `{q1 + q2}`).

**NOTE**: Although the list of inbuilt functions can be extended via Java or Groovy, users should consider using service requests instead as an alternative to functions. This especially true in cases where the scope of functions is growing larger and more complex.  

---

### DEL reserved words summary table

There are three basic categories of reserved words used when writing logic expressions in DEL: **Logic-building**, **time and date**, and **language specification**.

Language specification follows ISO-639-1 two-character abbreviations. [See list here](https://www.wikimass.com/html/language-code)

| Logic-building    |      Time and Date  |  Language       |
|-------------------|---------------------|-----------------|
|  `and`            |  `day`              | `language = ''` |
| `answer`          |  `days`             |                 |
| `answered`        |  `hour`             |                 |
|  `false`          |  `hours`            |                 |
| `in`              |  `minute`           |                 |
| `is`              |  `minutes`          |                 |
| `lengthOf`        |  `now()`            |                 |
| `matches`         |  `second`           |                 |
| `not`             |  `seconds`          |                 |
| `or`              |  `today()`          |                 |
| `true`            |  `week`             |                 |
| `valid`           |  `weeks`            |                 |
| `format`        |  `year`             |                 |
|                   |  `years`            |                 |

---

# Functions and reserved words: Example use cases

## Quick Summary

* DEL built-in functions and keywords are a powerful and simple way to build logic rules
* This page will give several examples of common use cases of these keywords in logic-building

---

## Overview: Keywords in use

**DEL** comes with a set of inbuilt functions and keywords that help in the creation of dialog logic. These functions are built via Java and Groovy and are automatically loaded into the dialog context when creating a new dialog.

The following examples feature various logic-building keywords across common use cases and should provide a basic working reference. The examples feature groups of words that are commonly used together.

* Example set 1: `answer`, `answered`, `and`, `is`, `valid`, `not`, `or`, `in`, `true`, `false`, `matches`
* Example set 2: `true`, `false`
* Example set 3: `matches`, `not matches`

---

## Example set 1: `answer`, `answered`, `and`, `is`, `valid`, `not`, `or`, `in`, `true`, `false`, `matches`

`answer`: Refers to the answer of the current question. `answer` can not be used to reference the answer from a different question. 

<img width="894" alt="05-01" src="https://github.com/user-attachments/assets/3823b629-36f9-4500-a952-d61e8cdfd6a6" />

In this case, `answer` is referring to the specific answer to `number1`. 

---

`answered`: Refers to the answer of a previous question.  

`and`: Used when joining two statements together. Both statements must evaluate to either `true` or `false`

`is`: Used in boolean logic calculations involving `answered` and `valid`

<img width="1186" alt="05-02" src="https://github.com/user-attachments/assets/41c66712-a609-4d0c-bd42-45727375b09c" />

---

`valid`: Used when showing/hiding/requiring/validating a field is dependent on a previous input being valid (passes validation rules). Used in combination with `is` and `not`

`not`: Used to negate an expression / part of an expression

<img width="1174" alt="05-03" src="https://github.com/user-attachments/assets/ff750020-ce2d-4528-926a-7cd70ddd8d56" />

`or`: Used when specifying one given statement in an expression and excluding the other(s). One of the statements must evaluate differently than the others. 

**Mini-example: Visibility using OR**

This rule below specifies `text1` to be visible only if the left or the right side of the visibility rule statement, separated by `or`, returns `true`.

text1: `Who is your current car insurance provider?`

Visibility rule: `age > 18 or age is not answered`

---
`in`: Used in combination with `Choice` or `Multi-Choice` type questions.  Evaluates to `true` if the answer(s) it refers to is/are selected from a list.

<img width="1157" alt="05-04" src="https://github.com/user-attachments/assets/674e9221-7765-4ece-9af0-e3bc7dae7a20" />

---

## Example set 2: `true`, `false`

`true` and `false` are used in evaluating boolean logic statements. They are preceeded by `=` or `!=` operator.

**Mini-example A: Visibility rule**

A following question will be displayed if the answer to isHappy is yes (true)

_Question 1_: `Are you happy? Select 'yes' or 'no.'`  
_Answer evaluation_: `isHappy = true`

_Question 2_: `You are happy. You may continue with the survey.`  
`Please explain what makes you happy.`

**Mini-example B: Validation rule**

A question will not pass validation if the answer is false  

_Question 1_: `You must be 25 or older to participate. Are you 25 or older?`  
_Answer evaluation_: `answer != true`  

_Question 2_: `How did you hear about this competition?`

---

## Example set 3: `matches`, `not matches`

`matches`: Used when matching an input against a formula to see whether they are the same. Used in Regular Expression validations/visibility rules.

The following example shows a Java regex which validates that an input matches the Estonian VAT number, which starts with "EE" and is followed by 9 digits.

<img width="893" alt="05-05" src="https://github.com/user-attachments/assets/dfb5bfa2-c8e7-4f6c-9d7c-d788a5bd59c2" />

**NOTE**: Why is the validation written as `answer not matches` if we are trying to use a regex to match a pattern?  

When using validations, the logic works opposite to visibility and required logic.  Validations can be thought of as "Answer CANNOT be" whereas visibility and requirement can be thought of as "Answer MUST be".

---

# Introduction to DEL logic rules

## Quick Summary

* Dialob has three types of logic rules:
  * Validation rules
  * Visibility rules
  * Requirement rules
* Different types of rules are written in different places in Composer

---

## Overview

DEL creates the logic to control the behavior of a dialog via three main control elements (rules): **Visibility**, **Validation**, and **Requirement**.  Combined with Request IDs, DEL logic rules will determine the complexity of the end-user filling experience.

There are three basic types of DEL rules:

* **Visibility logic**: Defines if a given form element (unique request, group of requests or set of groups) is available in a given dialog when compared against a set of user inputs.  
In other words, visibility logic can be used to decide whether to show or to hide a certain question or group of questions based on a previous answer: E.g. If a user specifies that his/her age is 15, then visibility rules can be set to hide questions about spousal income, and these will not be asked.

* **Required logic**: Defines if a response (user interaction) is required for a given Request. The request must be visible.  
Required logic can be used in situations where, for example, a question about total household income can be required to be answered if a user's previous answer indicates that he/she is married and both partners work.  

* **Validation logic**: Validates that the user response (user interaction) for a given Request is valid. The request must be visible.
The current state of Required and Validations is also "monitored" across all visible requests and the online rendering application (UI) is made "aware" if there are Required and / or Validation violations within a visible set of Requests.  
  * As a basic example, validation logic can be used to require a user to answer a question about his/her age with numbers only. If the user enters a short text or series of letters in the "age" field, the validation logic will catch this mistake and require the user to enter input in the specified correct format: Integer.  Validations are highly-customizable and can be written in a rich and complex manner.
  * Validation logic can also be written with Java Regular Expressions

**Important notes**:

* When logic rules perform their evaluations, the only outcome that counts is `true`. This means that if expressions do not validate to `true`, then the Visibility, Required, and Validation form control elements will not do anything, as they only operate based on evaluations of `true`. These rule elements will not exist in the context of the form.

---

### Where to write rules

Rules can be edited when opening the item edit dialog, by clicking on the hamburger icon in the corner of the item, and selecting "Options". Different tabs are used for different rules.

* **Visibility and Requirement Rules**: In the "Rules" tab.

* **Validation Rule**: In the "Validations" tab. 

These specific tabs can also be reaching by clicking the indicator icons for corresponding rules.

<img width="895" alt="05-06" src="https://github.com/user-attachments/assets/c48329b7-9254-4100-a440-cefb292f9f76" />

---

# Language Keywords

## Quick Summary

* Dialob Expression Language (DEL) comes with built-in keywords to specify languages for localisation purposes
* Languages can be specified by using this syntax: `language = 'fi'` (Language is Finnish), where `fi` is the two-letter language code

---

## Overview  

It is possible to show and/or hide different language fields and note outputs on the filling side based on the currently active language of a filling session. For example, you may want certain inputs to appear based on the form user's active language: If a client is using the English version of a form, but the client's active language is Finnish, it is possible to set outputs to appear, in Finnish. These Finnish outputs will only appear for Finnish-language users of this form and will be invisible to everyone else.

Using the ISO 639-1 standard, two-character language codes can be specified, which store the language that the current Dialob session is using. The `language` keyword can be used to write logic rules based on language. A list of two-character language codes [can be found here](https://www.wikimass.com/html/language-code).

---

## Example of the language keyword in use

In DEL, a language is designated with the keyword `language` followed by **equal to** `=` operator  or **not equal to** `!=` operator and completed with the two-character abbreviation.

Example:

`language = 'xx'`

Language specification is written into the visibility or required fields as an expression seen below:

`language = 'fi'` (Language is Finnish)  
`language != 'en'` (Language is not English)

---

# Time and Date: Keywords and functions

## Quick Summary

* Time and date types can be used to calculate durations, perform mathematical operations on time, and validate times in relation to each other
* Example use cases of Time and Date keywords and functions can be found on this page.
  * Calculating a time duration in hours, minutes, seconds
  * Returning the difference between different times in hours and minutes
  * Checking that one time is earlier or later than another time
  * Calculating a date period in years, months, days
  * Building logic to add or subtract years, months, or days
  * Validating that one date has occurred before or after another date

---

## Overview  

**Time and date keywords** can be used to write logic connected to the following:

* **Time durations** (time in hours, minutes, seconds)
* **Date periods** (time in months, days, years)


**Time and date functions** can be used to return the system time and date to write logic in connection with the current time and date.

The `today()` function returns the system date in MM/DD/YY format.  
The `now()` function returns the system time in 12-hour format (hh:mm AM/PM): For example 8:58 AM.

---

### Time and date keywords and functions overview table

| Time in Years, Months, Weeks, Days    |  Time in Hours, Minutes, Seconds  |
|---------------------------------------|-----------------------------------|
| `day`                                 |  `hour`                           |
| `days`                                |  `hours`                          |
| `week`                                |  `minute`                         |
| `weeks`                               |  `minutes`                        |
| `year`                                |  `second`                         |
| `years`                               |  `seconds`                        |
| `today()`                             |  `now()`                          |


---

### Time type reserved words

Time type reserved keywords work in the same way as Date type reserved words; accordingly, they can be used for the following functionalities:

* Calculating and returning a duration of a time period between times in hours, minutes, and seconds

* Building logic to perform mathematical operations on time such as adding time in hours or subtracting time in minutes

* Building logic to validate that one time is earlier or later than another time

Date type words are frequently used in combination with the `now()` function.

**Basic operations**:  

* [Time] **-** or **+** [Time] => [Duration]. Outcome is in the format "PT**hh**H**mm**M**ss**S" , "PT12H34M55S" where:
  * **P** marks that this is a period of time
  * **T** marks that the type of period is time
  * **hh** is the difference in hours
  * **mm** is the difference in minutes
  * **ss** is the difference in seconds

* [Time] **-** or **+** [Duration] => [time]. The outcome of time format is "hh:mm:ss"

For example, we will calculate the duration of a workday. The following will return the difference between two times, `time1` and `time2`, in Hours, Minutes, and Seconds in the PT**hh**H**mm**M**ss**S format:

**Expression variable ID**: `{durationOfWorkday}`  
**Expression variable value**: `time2 - time1`  
**Times used for comparison**: `time1 = 08:00` and `time2 = 17:15`  
**Note output text**: `Your workday is {durationOfWorkday} long.`  
**Return**: `Your workday is PT9H15M long.`

*In other words, this return value says that the time difference between time1 and time2 is a **Period** of **Time** of 9 **H**ours, 15 **M**inutes.*

**Time duration** can be used also to build logic with the following notation:  
`8 hours + 30 minutes + 22 seconds`

Using **Time** type examples: 

`question1 > "05:00"`  
Is true if *question1* is later than "05:00" (5 am).

`question1 + 2 hours + 30 minutes > "18:30"`  
Is true if *question1* is no earlier than "17:00".

**Example 3**: Checking one time against another time, validating that one is later than the other by a certain number of hours and minutes.

---

### Date type reserved words 

Date type reserved keywords can be used for the following functionalities:

* Calculating and returning a duration of a time period between dates in years, months, and/or days

* Building logic to perform mathematical operations on dates such as adding time in months or subtracting time in days

* Building logic to validate that one date is earlier or later than another date

Date type words are frequently used in combination with the `today()` function.

## Calculating date periods

**Basic operations**:  

* [Date] **-** or **+** [Date] => [Period]. The period format is "P**y**Y**m**M**d**D" where:

  * **P** marks that this type is a period of time
  * **y** is the difference in years  
  * **m** is the difference in months
  * **d** is the difference in days

* [Date] **-** or **+** [Period] => [Date]. Outcome is in the format "yyyy-mm-dd".

For example, the following will return the difference between two dates, `date1` and `date2`, in Years, Months, and Days in the P**y**Y**m**M**d**D format:

**Expression variable ID**: `{timeDifference}`  
**Expression variable value**: `date2 - date1`  
**Dates used for comparison**: `date1 = 05/11/2005` and `date2 = 10/24/2020`  
**Note output text**: `The time difference between date1 and date2 is {timeDifference}`  
**Return**: `The time difference between date1 and date2 is P15Y5M13D`

*In other words, this return value says that the time difference between these two dates is a **Period** of 15 **Y**ears, 5 **M**onths, and 13 **D**ays.*

### Building logic to add or subtract years, months, or days 

**Date period** can also be used to build logic with the following notation:  
 `1 years + 3 months + 14 days`

### Validating that one date has occurred before or after another date 

`question1 > "2005-01-01" `  
Is true if question1 is later than the 1st of January, 2005.

`question1 + 4 years < "2005-01-01" `  
Is true if *question1 + 4 years* is earlier than the 1st of January, 2005.  

For example, if the answer to `question1` is "2000-01-01", then this would evaluate to true, as `question1 + 4 years`  would evaluate to "2004-01-01", which is earlier than "2005-01-01".

`question1  - "2005-01-01" > 1 year + 2 months + 10 days`  
Is true if *question1* is later than "2006-03-12".

`question1  in ("2005-01-01",  "2006-01-01", 2007-01-01")`  
Is true if *question1* is one of following dates: "2005-01-01", "2006-01-01" or "2007-01-01".

**Example 1**: Validating that, as of today's date, a client's age is at least 18 years old.

1. Add a new date input to capture client's birthdate.
2. Create expression variable for output: `today() > date3 + 18 years`. This will trigger the validation message if the client's date of birth is 18 years earlier than today's date.
3. Add an output `note` and insert the expression variable along with some contextual information:

```Markdown
Customer birthdate is {date3}

Customer must be at least 18 years old to purchase this product.

Possibility to sell product to this customer is {isOver18}.
```

4. Preview behaviour on filling side

<img width="1172" alt="05-07" src="https://github.com/user-attachments/assets/ef8f04c7-949d-4db6-b109-a175ec9df240" />
<img width="893" alt="05-08" src="https://github.com/user-attachments/assets/c3a59bcb-144e-4216-87b6-d0b907c62913" />
<img width="893" alt="05-09" src="https://github.com/user-attachments/assets/3225b6c5-b406-4fe6-a227-a1139c893dd2" />


**Example 2**: Validating that an entered date is both in the past and one day ago (checking for yesterday's date)

1. Create two inputs of type `Date`. date1 is for today's date, date2 is for yesterday's date.
2. Write your validation rule and validation message in date2.

**Validation message**: "Yesterday's date must be in the past, and it can only be one day ago!"  
**Validation rule**: `date2 <= date1 or date2 - date1 != 1 day`  

3. Preview and test.

Composer side

<img width="1181" alt="05-10" src="https://github.com/user-attachments/assets/a96d5e54-87c7-493c-a282-f3a6b1e3445a" />
<img width="893" alt="05-11" src="https://github.com/user-attachments/assets/4f0b6d7e-faa2-4caf-845b-0db7eefc6137" />

Filling side preview

<img width="1151" alt="date-validation2-2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/1ecbd52e-6711-4273-879c-67bc64157e1d" />

---

# Validation Rules

## Quick Summary

* Validation rules ensure that filling side data matches stipulated requirements. 
* Validations can be thought of as “Answer CANNOT be” whereas visibility and requirement can be thought of as “Answer MUST be”.
* Validation rules should be accompanied by Validation Messages, which are short texts providing additional information to users on the filling side, either to give additional information on certain questions or to help them fix any errors that don't pass validation.
* To make a validation message appear to the user on the filling side, the DEL rule must evaluate to `true`

---

## Overview

Validation rules ensure that the data recorded on the filling side matches the requirements. Dialob matches the filling side data against the validation rule written on the Composer side, and if there is a discrepancy, a validation message will appear and alert the user.  

Validation Messages are short texts providing additional information to users on the filling side, either to give additional information on certain questions or to help them fix any errors that don't pass validation. By default, these messages are blank in Composer. If you do not write a validation message and the user input doesn't pass validation for that particular question, an empty red text field will appear on the filling side because no actual text was specified or written. For that reason, it is a good idea to write clear validation messages when you write a validation rule, as these two things go hand-in-hand. 

Validation messages appear on the filling side when they are triggered by a validation rule's evaluation: That is, when a validation rule evaluates to TRUE, the message will appear. 


See examples:

**Validation rule and message on the Composer side:**

<img width="895" alt="05-12" src="https://github.com/user-attachments/assets/b6f68eee-c3d3-422c-a6e3-53d18c725776" />

**Validation rule and message on the filling side with message:**

<img width="1090" alt="validation-filling-side1" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/1f3fc1f6-a276-4b01-a53c-e2173759008e" />

**Validation rule and message on the filling side without message:**

<img width="1102" alt="validation-filling-side2" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/d927839e-242f-44dc-977e-b43f7723a988" />

---

### Evaluation of Validation rules

To make a validation message appear to the user on the filling side, the DEL rule must evaluate to `true`.  Therefore, the logic works opposite to visibility and required logic. Validations can be thought of as “Answer CANNOT be” whereas visibility and requirement can be thought of as “Answer MUST be”.

**In other words, an evaluation of `TRUE` triggers the validation message and prevents the user from continuing to the next page of the form, whereas `FALSE` will not trigger the message and will allow the user to continue filling the form.**

---

# Visibility and Requirement Rules

## Quick Summary

* Visibility rules determine when and if a particular question is shown on the filling side.
* Requirement rules determine whether a question must be answered before proceeding to the next page or completing the form.
* Visibility and Requirement rules should be written in the "Rules" tab of the item edit dialog.
* Visibility rules can be written for local list items using when expanding the list item and typing the rule in the "Visibility" field. The highlighted eye icon indicates that a visibility rule is present.

<img width="890" alt="05-13" src="https://github.com/user-attachments/assets/5b59ec38-89c6-4d2b-8a15-d32a52eb3091" />

---

## Overview

**Visibility rules** determine when and if a particular question is shown on the filling side. What determines this is usually based on the answer to a previous question.  For example, if a user selects "other" from a drop-down list, a subsequent text field can be made to appear for collecting additional information.

**Requirement rules** determine whether a question must be answered before proceeding to the next page or completing the form.  Requirement rules have a default value of `false`, which means that the question is not required to be answered.  Simply writing `true` in the Requirement field is enough to make it universally required without dependency on any other question.

---

### Evaluation of Visibility and Requirement rules

When considering how to write visibility and requirement rules to be triggered at the appropriate times, think of their evaluation in terms of “Answer MUST be”.  See these examples below:

1. To trigger `text2` visibility, the answer to `boolean1` MUST BE true.

<img width="1174" alt="05-14" src="https://github.com/user-attachments/assets/6f7d289d-c378-42ac-8675-ceb4a7a82cb5" />

2. To make `text2` visible AND required, the answer to `boolean1` MUST BE true.

<img width="894" alt="05-15" src="https://github.com/user-attachments/assets/2140199a-f223-4a28-af5a-c256205ed560" />

In this case, text2 will only be a required field if the response to boolean1 in the previous field is `true`. 

---

# Regular Expressions in logic

## Quick Summary

* Regex format in a logic rule is as follows:
  * `answer not matches "regex"`
  * `answer matches "regex"` 
* Dialob supports Java Regex in any variable, validation, or visibility rule.
* Regex can evaluate only the structure of Strings, Numbers, Dates, etc., but they cannot ensure that this data is factually valid.
* Validation logic must evaluate to true in order to make validation message appear on the filling side. 
  * Write helpful validation messages to assist users in correcting their input if it doesn't match the Regex-stipulated format

---

## Overview

### Java regular expressions and Dialob 

Dialob supports the use of Java regular expressions (regex) in any variable and any validation or visibility rule. Using regex, form creators can write simple or complex rules to validate user inputs against any array of requirements so as to ensure accurate recording of entered data by the end-user on the filling side.  

**NOTE**: It is important to remember that regular expressions can validate the *structure* of Strings, Numbers, Dates, etc., but they cannot ensure that this data is factually valid. For example, a regex can ensure that a user's ID number is of the correct format for a given country but it cannot check that this ID number is active or real.

For more information on regular expressions: [Wikipedia](https://en.wikipedia.org/wiki/Regular_expression)  
For Oracle Java regular expression patterns: [Oracle Documentation](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html)

---

### Notes before you start using Regex 

* **Preface Regex with keywords**: In the `Validation Rule` or `Visibility` field, write the following Dialob keywords _first_ : `answer matches` or `answer not matches`. Your Regex will then come _after_ these.

* **Ensure rule evaluates to true**: If a response field should be shown based on a previous answer, the rule must be written to evaluate to `true`. Keep in mind that validation messages will only appear on the filling side when a validation rule returns `true`. Therefore, if a form creator wishes the validation message to appear when the user enters incorrect information, validations must be written in a way so as to return true (i.e. `true` produces the validation message, `false` does not).  

* **Remember to create a validation message**: The validation message field appears above the validation rule field. The validation message is helpful for the end user on the filling side, as it can be used to give additional information to the user to assist in completing a response accurately (if a user entered data in the wrong format, for example, the validation message can alert them to this).  For example, on the filling side, if a response requires that a user enter their VAT number, the validation message can be set to appear if the VAT number is entered in an incorrect format. The message will inform the user that the data entered is incorrect and provide a model for them to follow to ensure that the response fulfils the validation rule for VAT number format.  


### How to create a helpful validation message and ensure it appears when you need it

**Question:** `Are you older than 25? Only those 25 and older may participate.`  

**Validation Message:** `"Sorry, you must be 25 or older!"`  

**Validation:** `answer > 25`

_Validation message is triggered._

If the user enters that his age is 50, the validation will evaluate to true, and the validation message will appear, which is clearly not what we want. We want the validation message to appear only if the user's age is under 25, so we need to write the validation in a way that it will evaluate to `true`, given this situation.

To make the validation message appear if a user enters an age less than 25, we need to write the validation in this way:

`answer < 25`  

In this way, if a user enters his age is 18, and because 18 is less than 25, the validation will evaluate to true, and the message will appear, alerting the user that his age is under the threshold.

### Important notes to remember about writing validations with regex 

* `answer not matches` will produce a validation which will return false when the regex matches the input. A return of false will cause the validation message **_not to appear_**.

* `answer matches` will produce a validation which will return true when the regex matches the input. A return of true will cause the validation message to appear.  

* `answer` refers to the user response which is only in scope of the current selected question.  
  * When using regex, it will be very commonplace to need to refer to an `answer` which is out of scope of the regex itself. To access and work with a different variable, simply use that variable's ID in place of `answer`. For example, instead of `answer not matches "XXX"`, write `text2 not matches "XXX"`.
* Regex must be surrounded by quotation marks " " as in this example: `answer not matches "xxxxxx"`

---

## How to input Regex into Composer with example use cases 

* [Estonian VAT number validation](#estonian-vat-number-validation)

* [Finnish ID number validation](#finnish-identification-number-validation)

* [Phone number validation](#phone-number-validation)

* [Email address validation](#email-address-validation)

---

### Estonian VAT number validation

**_Example Use Case:_** A form requires that a user input an Estonian VAT number.  

The following regex will check that

* The VAT number entered on the filling side is a correctly formatted Estonian VAT number, which takes the format EE123456789 (EE followed by 9 digits).

`answer not matches "^(EE)?[0-9]{9}"`

#### Composer side

<img width="893" alt="05-05" src="https://github.com/user-attachments/assets/f682c390-50ca-4a3c-9178-56af4e5262a2" />

#### Filling side

<img width="1080" alt="estonia-vat-validation-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/ff763627-ad02-471e-a1ac-41f883d383a0" />

**NOTE on writing visibility rules**: If a response field should be shown based on a previous answer, the visibility rule must be written to evaluate to `true`.  

---

### Finnish identification number validation 

**_Example Use Case_** A form requires a user to input a national identification / social security number because the user specified that they are a Finnish citizen.  

The following regex will validate  

* The user input a properly formatted Finnish social security number

`answer not matches "(\d{2})(\d{2})(\d{2})([+-A])\d{3}[0-9A-Z]"`  

Next, Composer will validate that the user-entered social security number is of a valid format and match it against the user-specified citizenship (Finnish) which was selected in the previous question.

#### Composer side  

<img width="895" alt="05-16" src="https://github.com/user-attachments/assets/9ab6f1ad-b56a-4edb-9269-b9b802b55629" />

#### Filling side

<img width="1084" alt="national-id-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/150ec9c7-0529-4599-b72a-2286a3859a36" />

---  

### Phone number validation 

**Validating an international number**: The following regex will check that

**_Example Use Case_:** A form requires that a user input an international phone number. 

* The phone number follows [E.123 standards](https://en.wikipedia.org/wiki/E.123) for international telephone number notation
  * The number is preceeded by a + sign to denote country code
  * The country code is followed by telephone number
  * Spaces should separate the country code, area code, and local number

`answer not matches "^\+(?:[0-9] ?){6,14}[0-9]$"`

**Validating a United States number**: The following regex will check that

* The phone number is a valid United States format
* The number format matches any of these possibilities:
  * 1234567890
  * 123-456-7890
  * 123.456.7890
  * 123 456 7890
  * (123) 456 7890

`answer not matches "^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$"`

**Validating that a country code matches a particular country**: The following regex will check that

* The user entered a three-digit country code

`answer not matches "\d{3}"`

Next, Composer will check that the country code provided matches the country of residence that the user previously specified. For this example, Dialob has been configured to match the country code of Estonia (372).

#### Composer side 

<img width="1177" alt="05-17" src="https://github.com/user-attachments/assets/01cf064f-0979-4036-873a-36619db09f8a" />
<img width="894" alt="05-18" src="https://github.com/user-attachments/assets/c71ebf95-daf7-4791-94dc-7c061d0be1d9" />
<img width="892" alt="05-19" src="https://github.com/user-attachments/assets/4a3ef8fc-e7f8-4ef6-bca1-b896c0616410" />

#### Filling side  

<img width="1098" alt="country-code-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/aca7f4af-a104-41a3-b8b9-f2b75c1e55f1" />

---

### Email address validation 

**_Example Use Case:_** A form requires that a user input his/her email address, and the format of the email must be correct to ensure that it is of a valid format before attempting to send an email there.  

The following regex will check that  

* The domain name includes at least one dot  
* The part of the domain name after the last dot consists of only letters  
* The domain must consist of two levels (i.e. secondLevel.com or secondLevel.thirdLevel.com)  
* The top-level domain must consist of two to six letters (A good example is country-specific domain names: .uk, .ee, .us)  
* Generic top-level domains have between three (.com) and six (.happy) letters  

``answer not matches "^(?i)^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[A-Z0-9-]+\.)+[A-Z]{2,6}$"``

**Composer side**

<img width="893" alt="05-20" src="https://github.com/user-attachments/assets/44c7644b-7e19-4b04-96ac-552dc8d88b00" />

**Filling side**

<img width="1057" alt="email-validation-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/35560500-6e64-44ed-8e92-80ccef0b17ec" />
