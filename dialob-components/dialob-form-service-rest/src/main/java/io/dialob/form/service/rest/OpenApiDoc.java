/*
 * Copyright © 2015 - 2025 ReSys (info@dialob.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dialob.form.service.rest;


public class OpenApiDoc {

  public static class GENERAL {

    public static final String FORM_ID = "Internal identifier for a form";
    public static final String FORM_NAME = "Unique, manually-created identifier for a form";
    public static final String TAG_NAME = "Name of the tag";
    public static final String REV = "Form revision number";
  }

  public static class TAG {

    public static final String TAG_SUMMARY = "List all tags within the tenant.";
    public static final String TAG_OP = "In order to find specific tags within the tenant, you can perform queries for the following properties within the tag: form ID, form name, and tag name.";
    public static final String SNAPSHOT = "If snapshot is set to true, a copy of the current form will be created as a snapshot version with a different form ID, to which the new tag will point";
    public static final String NEW_TAG = "Name to be assigned to new tag";
    public static final String TAG_OBJ = "Form tag object";
    public static final String TAG_LATEST_SUMMARY = "Update a form tag to 'LATEST' tag";
    public static final String TAG_LATEST_OP = "Use one of a form's existing tags to create a 'LATEST' tag. A LATEST TAG is mutable, but the all the previous tags are immutable. To do this, you can use the form ID and tag properties.";
    public static final String GET_TAGS_SUMMARY = "Return a list of tags associated with a given form ID";
    public static final String GET_TAGS_OP = "The form ID is used to identify a specific form and return an array of tag values for that form";

    public static final String GET_TAG_NAME_SUMMARY = "Return a tag by form ID and tag name";
    public static final String GET_TAG_NAME_OP = "Return a form tag using two properties: form ID and tag name";
    public static final String POST_FORM_TAG_SUMMARY = "Create a new form tag for a form by form ID";
    public static final String POST_FORM_TAG_OP = "This endpoint will create a new tag for the latest revision of a form.";

  }

  public static class FORM_ID {

    public static final String GET_FORMID_SUMMARY = "Return a form by form ID";
    public static final String GET_FORMID_OP = "In order to find specific forms within the tenant, you can perform queries based on two properties: form ID, and the form revision number: rev";
  }

  public static class POST_FORM {
    public static final String POST_FORM_SUMMARY = "Create a new form";
    public static final String POST_FORM_OP = "You can create a new form within your tenant with a form object in .JSON format.";
  }

  public static class PUT_FORM {
    public static final String PUT_FORM_SUMMARY = "Update an existing form by form ID";
    public static final String PUT_FORM_OP = "With this endpoint, you can edit a particular form. You have the option to choose whether or not you want to create a new revision number for the form. You can also force an update which ignores the form revision number check. ";
    public static final String DRY_RUN = "dryRun is useful for testing purposes. If dryRun is set to true, a new revision number will not be created upon opening a form for editing";
    public static final String FORCED = "A forced update will ignore form revision number check. The form ID and request body must match.";
    public static final String OLD_ID = "The name of the old identifier";
    public static final String NEW_ID = "The new name to assign to the identifer";
  }

  public static class DELETE_FORM {
    public static final String DELETE_SUMMARY = "Delete a form by form ID";
    public static final String DELETE_OP = "The form ID is used to identify and delete a form from the tenant's form database.";

  }


}
