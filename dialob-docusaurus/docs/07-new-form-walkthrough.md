---
id: 07-new-form-walkthrough
sidebar_position: 7
title: New form walkthrough
---

# New form walkthrough 

Welcome to Dialob! In this guide, you will use Dialob Composer and learn how to create a simple dialog using structures, inputs, and response types. You will create a basic input validation, learn how to show/hide fields, and write a requirement rule for a question, all using Composer's built-in features and Dialob Expression Language, **DEL**.

After completing this new user guide, you will be familiar with Dialob's basic features and have a better idea how to organise your workflows while using the platform. Note that this basic guide does not include advanced features such as custom variables and complex validations, visibility, or requirement rules.

Let's get started in making our first dialog.  Below is a screenshot of the end-result we are going to build: A simple questionnaire to collect user personal data on foods and restaurant opinions.

<img width="1096" alt="complete-form" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/0eebb5fc-c442-4142-9be0-aab5e04198bf" />

To make this example, we will follow these steps:

1. Create a new form

2. Create a new page

3. Create a new group

4. Create a response (form question)

5. Create list and apply it to a group / question

6. Create a survey input

7. Write simple validation, visibility, and requirement rules

8. Preview your form

---

### Step 1: Create a new form 

All dialogs (forms) are built using Dialob Composer, the user interface that you see on the front-end side, and these dialogs are processed by Dialob Manager, which is the engine running in the background, processing all of your changes in near real-time. There is no need for a "save" button anywhere, as each change you make is automatically processed by Manager.

Let's begin our work with Composer by creating a new form.

<img width="947" alt="create-new-form" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/2207b6dc-d256-4607-be26-2c3b0fa6dbb1" />

---

### Step 2: Create a new page 

Pages form the base containers for all inputs in Dialob. Pages contains groups. Groups contain questions. To get started, click the `add` icon to create your first page.

<img width="1468" alt="07-01" src="https://github.com/user-attachments/assets/cdbae3d0-70f0-4039-89c0-2ac3004227de" />

---

### Step 3: Create a group 

Now that you have a page, you can create a group inside of it. Click the `Add item` button to add a "standard" group. The other forms of group -- _Survey group, Survey Group (Vertical), Multirow group_ -- will be discussed later.

<img width="1468" alt="07-02" src="https://github.com/user-attachments/assets/9e0c8086-8211-42c1-a12d-2a2ff20c04ec" />

---

### Step 4: Create a response (form question) 

Now that you have a group, you can give the group a _label_. The label is the name or title of the group which will appear on the filling side above all the questions contained within that group. The group label can be left empty if you don't want to have any visible text as a group label. In our case, we want this group to have a visible label, called "Personal Questions", since that is the nature of the question we will be asking the user.  

<img width="1468" alt="07-03" src="https://github.com/user-attachments/assets/592fee89-234e-465b-bddf-f90ff27cb535" />

Click on the group label field, which will open up the label editor.  Write your label in the text field and close the dialog. The changes will be automatically saved.

<img width="1468" alt="07-04" src="https://github.com/user-attachments/assets/d2f04b6b-8632-431a-8448-38fb7a020751" />

Note that labels are written in Markdown, and clicking the "Preview" button will show you how the label will appear on the filling side.  Markdown is a simple way to format text, and you can find more information on how to use it [here](https://www.markdownguide.org/basic-syntax/).

Next, let's create our first response (question). Click the `Add item` button in the bottom left corner of the Group window. Add a question of type Text.

<img width="1468" alt="07-05" src="https://github.com/user-attachments/assets/87f1e713-a9ee-4fa2-8fd2-2877672de82e" />

You can see below that the tree view on the left side of the screen is automatically updated with groups and questions as you go. Our group label (title) has been updated with the name we have given it. `text1` is the default ID of the question we have just created and will be updated if we choose to rename this ID later on.  

<img width="1468" alt="07-06" src="https://github.com/user-attachments/assets/1a471ca1-5b41-4c9d-aaa3-7f9532dd622e" />

Now, let's create our first question. Since this group is intended to collect personal data, it is logical that our first question should be a Text input to collect a user's full legal name.  In the `Text field label`, write your question: "Please enter your full legal name".

<img width="1468" alt="07-07" src="https://github.com/user-attachments/assets/f3a4f073-87b2-40c0-bb49-0564c9795fa6" />

Let's preview this on the filling side. Click the `Preview` button in the top right of the Composer screen.  Note how the Group Label (title) is displayed above the question.

<img width="1468" alt="07-08" src="https://github.com/user-attachments/assets/663d38d6-1b77-466b-82b9-bbcb1dda9af3" />

Now, we have a text response field to collect someone's full legal name, but it would make more sense to collect each name individually, so as to create a clear differentiation between a first, middle, and last name. We will create a `Multi-Row` response type to collect these names individually.  

`Multi-Row` response types need to be created in a separate, `Multi-Row` group, which we will create now.

<img width="1468" alt="07-09" src="https://github.com/user-attachments/assets/e84c4d7c-86e2-434a-baf3-28a8bd6e2905" />

`Multi-Row` creates inline fields, and the number of inputs you add to the group will determine the number of input fields. For this demonstration, we will create three text fields for the collection of a user's first, middle, and last name.

Be sure to add a group label here, as the group label will serve as the question prompt to tell the user what to do.  In this case, our group label will be the following: "Please enter your full legal name".

Now, you should have something that looks like this:

<img width="1468" alt="07-10" src="https://github.com/user-attachments/assets/26eb7b7a-2ebd-4e8a-8562-5c0400bcc804" />

On the filling side, the user will see this:

<img width="1464" alt="multi-row-group-three-inputs-filling" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/315f2041-f943-411f-b2d6-984194f4df0a" />

Since we now have a `Multi-Row` group to collect a user's legal name, we do not need our first text input anymore, so it can be deleted. To delete, click on the hamburger icon in the top right corner of the desired question. In the picture below, we are deleting the entire group, since it holds only the one question that we don't want anyway. Groups and questions can be deleted from the hamburger icon. However, **keep in mind that there is no undo button!  Once a group or question has been deleted, the only way to get it back is to recreate it!**

<img width="1468" alt="07-11" src="https://github.com/user-attachments/assets/37916a03-3647-4e1d-a128-c1377502b055" />

Our dialog now has one question. Next, we will add our second, third, and fourth questions: A single-choice dropdown, a multiple-choice dropdown, and a survey question, respectively.

---

### Step 5: Create a list and apply it to a group / question 

A single `Choice` input gives the user the option of selecting only **one** option from a list, whereas the `Multi-choice` input allows the user to select **one or more than one** input. 

_Note: For more advanced use cases, if you want to specify that the user must select a certain number of inputs from a `Multi-choice` question, this can be determined by writing a validation rule which includes your specification._

`Choice` and `Multi-choice` can be created within a "standard" group.

Let's create a group to house our two new questions:  `Add-item` --> `Structure` --> `Group`

Be sure to select the `Add-item` button which is **outside** of any existing group. If you select the `Add-item` button within a group, you will create a nested group within that group, which, in this case, is not needed.

Give the group a label of "Favourite Foods" and create a `Choice` input type.

<img width="1468" alt="07-12" src="https://github.com/user-attachments/assets/a6742193-fcc6-4e80-b369-26f4b6cb1318" />

Notice that, within the group, the `Choice` input type will produce a `List` ID. This is telling us that, to create the drop-down menu for the user to select from, we need to create a list.

To create a list, go to the upper menu bar and select `Lists`.

<img width="1468" alt="07-13" src="https://github.com/user-attachments/assets/1b3770b1-3fdf-4585-ac4c-613686d61d52" />

Select "Add new list".

<img width="1468" alt="07-14" src="https://github.com/user-attachments/assets/46472493-10b7-41f9-8037-d9dcd791a7cc" />

In this example, we are creating a Global List. Global Lists can be applied to any number of questions across the dialog.  Any changes made to a global list will be reflected across all questions which use that list.  This is a good thing to use if you wish to reuse the same list over and over.

<img width="1468" alt="07-15" src="https://github.com/user-attachments/assets/f93bf684-9895-456e-bc60-e50a4160e701" />

To add a new list item, click the `add` button (plus sign) and enter a key and text.  **NOTE**: When creating lists, the key (ID) is not auto-generated, and it must be set manually by the form creator. Any changes to the ID will be auto-updated globally, however.

Let's fill in our list and name it Favourite Foods. Each list item can be expanded to edit its properties. 

<img width="1468" alt="07-16" src="https://github.com/user-attachments/assets/c92884af-1d89-447b-844c-e3077060f676" />

You should see something similar to the following:

<img width="1468" alt="07-17" src="https://github.com/user-attachments/assets/85408d51-e56e-4bfe-b856-1c5f89bf437d" />

Now that we have a list, we need to apply it to our `Choice` question and give our `Choice` question some text to prompt the user on the filling side.  Click the hamburger icon in the top right corner of the question and select `Options`.

<img width="1468" alt="07-18" src="https://github.com/user-attachments/assets/e2c4e2d5-4a44-420d-b9d8-1c6e6424707a" />

Navigate to the `Choices` tab. Now, select the global list you just created.  If you had wanted to create a one-time list which is bound to this question in specific, you could create a local list here.

<img width="1468" alt="07-19" src="https://github.com/user-attachments/assets/2722c423-32d8-4416-8339-2f74c3358545" />

After selecting the list, you can see that the list is now applied to the question and preview its content. There are buttons to navigate to the list itself, to edit the list, or you can convert to a local list if you wish to customize the list for this question only.

<img width="1468" alt="07-20" src="https://github.com/user-attachments/assets/798df798-f9f1-493a-8006-fb50fc3053ae" />

Let's preview this on the filling side:

<img width="1143" alt="choice-list-filling" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/3482e1c9-98d3-40fd-b9f0-a730ad40a12b" />

Now that we have seen how to create a global list, next, we will create a `Multi-choice` input type and connect it with a local list, which follows basically the same process.

<img width="1468" alt="07-21" src="https://github.com/user-attachments/assets/4ad1077e-8896-4562-9edc-403efbb8ed99" />

Create the local list of food preferences. Remember that this list is bound to this specific question only and cannot be reused in a different question.

Your food preferences list should look something like this:

<img width="1468" alt="07-22" src="https://github.com/user-attachments/assets/0ad1701e-09b0-4614-aa4b-e98b1bb8b9ad" />

Note that local lists can have specific visibility rules that toggle when a certain list item will be shown to the user.

The final result should look like this on the Composer side:

<img width="1468" alt="07-23" src="https://github.com/user-attachments/assets/44b672b3-e9b8-4771-8e3c-b1c1130b9d54" />

Let's preview the filling side now.

<img width="1116" alt="preview-filling-2lists" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/979cf5f2-d090-4de4-bc36-2b1343902297" />

---

### Step 6: Create a Survey input 

Like `Multi-Row` input types, questions involving survey buttons require being placed in their own specific group: `Survey Group` or `Survey Group Vertical`.  In this section, we will create our fourth question: An opinion survey on restaurants.

The first thing you need to do is create a `Survey Group`.  

`Add item` --> `Structure` --> `Survey Group`. This will create a single group with as many survey options as needed. The Group Label will be the singular prompt for the user, unifying all of the survey options under one question text.

<img width="1468" alt="07-24" src="https://github.com/user-attachments/assets/eca3b9d6-e3cd-4d6c-8a0a-a98fc5822c8f" />

Next, add the categories for the survey options by selecting `Survey item`.

<img width="1468" alt="07-25" src="https://github.com/user-attachments/assets/508dc939-7faf-4d49-9611-44550f29c92b" />

Note that the number of inputs directly corresponds to the number of survey categories the user will be able to choose from.

<img width="1468" alt="07-26" src="https://github.com/user-attachments/assets/ce28ca39-d042-4a61-9ea6-df448e4b997b" />

Now that we have the categories, it is time to add the survey buttons themselves. This is done by creating a list. For this question, we will create a local list.  Select the hamburger icon in the top right corner of the **SURVEY GROUP**, then select `Options`, navigate to the `Choices` tab, and `Create local list`.

<img width="1468" alt="07-27" src="https://github.com/user-attachments/assets/3791b650-dd0d-48cc-a447-9df1abd20d0f" />

This is what we see on the filling side preview:

<img width="1117" alt="survey-group-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/58319bba-4b6f-41c7-9c27-c33ddf27443a" />

---

### Step 7: Write simple validation, visibility, and requirement rules 

To create a more dynamic and appropriate user experience, the dialog should be crafted in such a way that it responds to user inputs efficiently and logically. For example, it would not make sense to ask a user about a favourite restaurant if he/she had previously specified that he/she doesn't eat at restaurants. Likewise, if a question asks the user for an email address, and the email address is to be used in the future for sending follow-up emails, it would be reasonable to expect that this field is required to be filled and that the input conforms to valid email address specifications. Validation, visibility, and requirement rules allow you to accomplish these things with ease.  These rules are written in Dialob Expression Language (DEL), a "programming" language so simple that even those who have never touched programming can use it.  It resembles a combination of algebra and the most basic of English grammatical structures.

Before we begin writing rules, however, it is important to understand that there is are two places to write rules: In the `Rules` tab and in the `Validations` tab, both located in the item editing dialog, accessed through the `Options` button from the hamburger icon in the top right corner of the question.

**_Visibility_ and _Requirement_ rules are edited within the Rules tab. You can also set a default value (answer) to the question here.** 

**_Validation_ rules along with localized validation messages are edited in the Validations tab. You can add multiple validation rules.**

Let's begin by adding requirement rules to our Multi-row text inputs. This is done by accessing the Rules tab in the  editing dialog and simply by writing `true` in the `Required` field. If a question has `Required` set to `true`, the dialog session cannot be completed until the question is answered (Complete button will be greyed out).

<img width="1468" alt="07-28" src="https://github.com/user-attachments/assets/12b9ee57-5949-46f9-bb6d-7b07ed8dcae8" />

If a  requirement rule is present, there will be an indicator for that.

<img width="1468" alt="07-29" src="https://github.com/user-attachments/assets/8fcbeb1e-9cd0-4a31-97e1-2c2180ac369b" />

Note that all questions default to `false`, which means that, unless otherwise specified, there is no requirement that questions be answered before completion of the dialog is permitted.

Next, after collecting the user's name, it might be nice to collect additional information if the user selects "other" from the favourite food `Choice` drop-down list. However, we did not add the option to select "other" before when we created the original global list. We will edit the global list to add that option, and then we will make a text box response field and set it to be visible if the user selects "other".

First, go up to the top menu bar and select `Lists`. Then, select your global list, `Favourite Foods`.  Adding another option is as simple as clicking the `Add` icon and setting your key-value pair.

<img width="1468" alt="07-30" src="https://github.com/user-attachments/assets/c0c40dca-47ee-4147-9077-63b7c5409c94" />

Return to `group1`: Favourite Foods. Click the `Add item` button within the group to add a new input. Select the `Text Box` input type. 

<img width="1468" alt="07-31" src="https://github.com/user-attachments/assets/0383445d-85ad-45cc-a5d5-111672ec1fdd" />

The text box item will appear at the bottom of the group. If you wish to reorder it and place it after the first `Choice` question, drag-and-drop it into place from the tree view on the left.

<img width="1470" alt="07-32" src="https://github.com/user-attachments/assets/31314a3e-7225-4a08-9753-26bd9414b58a" />

At this point, we will begin writing a visibility rule. We want to show `textBox1` **only if** a user selects "other" from the drop-down list in the preceeding question.  Using DEL, we will write the following into the `Visibility Rule` field:

`list1 = "other"`

Breaking it down, this rule is saying that, if the value "other" from the global list is selected in the question whose ID is `list1`, the expression will be `true`, and if this expression is `true`, `textBox1` is shown.  We can also make this a `Required` field.

<img width="1468" alt="07-33" src="https://github.com/user-attachments/assets/ce5fa937-5c73-4fbe-92c0-e0eb497668fe" />

On the filling side, we see this:

<img width="1115" alt="text-box-visibility-filling" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/08421c99-ce2c-47ec-975b-a9864f7f788b" />

It would be nice if our `textBox1` required the user to submit an input of a certain length so as to ensure a more detailed response. We can write a validation rule to specify the minimum / maximum length of a response.  We will also want to write a validation message, which will appear on the filling side if the user enters data that we don't want to accept, which is specified by our validation rule. The validation message will alert the user that the input is not valid and prompt him/her to re-enter it correctly.

Validations work in this way: Dialob will evaluate the user's inputs against the rules you write, and if your validation rule evaluates to `true`, the validation message will be displayed. The message will continue to be displayed until the rule evaluates to `false`. Please keep this in mind when writing validation rules.

We can navigate to the Validations tab in the editing dialog for `textBox1` and create a new validation rule.

<img width="1468" alt="07-34" src="https://github.com/user-attachments/assets/8bb9d4c9-9283-49cc-be43-c21b1704a3d5" />

Using the `lengthOf( )` function, we tell Dialob to check that the input of the item in parenthesis (in this case, `textBox1`) is less than 25 characters. If the user provides an input of fewer than 25 characters, the validation will evaluate to `true` and the validation message will be shown, prompting the user to continue entering characters, until the user enters more than 25 characters. At that time, the validation will evaluate to `false` and the message will stop being displayed. The form will also be completable, as a form cannot be completed with any outstanding invalid inputs.

<img width="1468" alt="07-35" src="https://github.com/user-attachments/assets/1c8ae579-8566-4514-86ea-c273ce585744" />

On the filling side:

<img width="1115" alt="text-box-filling-side" src="https://github.com/digiexpress-io/digiexpress-parent/assets/88784555/a8d5c476-851b-43e4-8880-240c35c0d35e" />

---

### Step 8: Preview your Dialog 

Congratulations! You have now gone through the basic steps in creating a dialog using Dialob Composer. Preview your form to see your end product!

This guide has been a simple beginning to help you become aware of Dialob's features and how the platform works in the dialog creation process. Of course, there are many more functionalities to explore and advanced options available to leverage the power of Dialob in the creation of highly complex, flexible dialogs. Please see the other sections of the documentation for an additional, more in-depth look.
