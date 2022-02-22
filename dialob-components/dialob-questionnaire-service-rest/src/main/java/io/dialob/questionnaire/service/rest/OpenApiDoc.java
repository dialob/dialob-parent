/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.questionnaire.service.rest;

public class OpenApiDoc {
	
	  public static class QUESTIONNAIRE {
		  public static final String QUEST_SUMMARY = "A questionnaire is a unique filling session stored on the server side. A questionnaire contains the session ID and the data defined by the form instance.";
		  public static final String GET_QUEST_OP = "With this endpoint, you can retrieve a list of questionnaires, filtering by five properties: formID, formName, formTag, owner, and status";
		  public static final String QUEST_OWNER = "User ID of the document owner, identifying to which user this filling session belongs.";
		  public static final String QUEST_STATUS = "Current status of questionnaire";
		  public static final String QUEST_ID = "The internal identifier of a questionnaire";
		  public static final String QUEST_OBJ = "Questionnaire object to replace existing state";
		  public static final String ITEM_ID = "The internal identifier of an item";
		  public static final String ROW_ID = "The unique identifier of a row";
		  public static final String PAGES = "Pages object for use in updating a questionnaire active page";
		  public static final String VALUESET_ID = "The internal identifier of a value set";

		  public static final String ANSWER_ID = "The unique identifier of an answer";
		  public static final String ANSWER_OBJ = "New answer value for a single question";
		  public static final String NEW_ANSWERS_OBJ = "New answers for a questionnaire";
		  
		  public static final String DELETE_ANS_SUMMARY = "Delete a single answer";
		  public static final String DELETE_ANS_OP = "Delete a single answer on a questionnaire by questionnaire ID and answer ID";
		  
		  public static final String GET_QUESTID_SUMMARY = "Retrieve a specific questionnaire";
		  public static final String GET_QUESTID_OP = "You can use the questionnaire ID to retrieve a specific questionnaire.";
		  
		  public static final String GET_ITEMS_SUMMARY = "Return all questionnaire items";
		  public static final String GET_ITEMS_OP = "With this endpoint, you can return an object which contains all of a questionnaire's items.";
		  public static final String GET_ITEM_SUMMARY = "Return a single questionnaire item";
		  public static final String GET_ITEM_OP = "With this endpoint, you can return an object containing a single questionnaire item and its contents.";
		  
		  public static final String GET_ROWS_SUMMARY = "Return array indexes and values of rowgroup items";
		  public static final String GET_ROWS_OP = "This action returns an object containing the array indexes and values of row items within a particular rowgroup from a single questionnaire.";
		  
		  public static final String GET_VALUESETS_SUMMARY = "Return all lists and their key-value pairs";
		  public static final String GET_VALUESETS_OP = "Return an object detailing a particular questionnaire's list(s) and key-value pairs for each list item";
		  public static final String GET_VALUESET_SUMMARY = "Return key-value pairs of a particular list";
		  public static final String GET_VALUESET_OP = "Return an object detailing the key-value pairs of a particular list on a questionnaire";

		  public static final String GET_PAGES_SUMMARY = "Return a pages object for a questionnaire";
		  public static final String GET_PAGES_OP = "Return an object outlining a questionnaire's pages, including page items, the active item (active page), and the available items (all available pages) by questionnaire ID.";
		  public static final String GET_STATUS_SUMMARY = "Return questionnaire status";
		  public static final String GET_STATUS_OP = "Return the status of a questionnaire: NEW, OPEN, COMPLETED";
		  public static final String GET_ANSWERS_SUMMARY = "Return the answers from a questionnaire";
		  public static final String GET_ANSWERS_OP = "This endpoint enables you to retrive a user's answers for a questionnaire in their original format";
		  
		  public static final String DELETE_ROW_ITEM_SUMMARY = "Delete a row item";
		  public static final String DELETE_ROW_ITEM_OP = "Delete a particlar row item by item ID and row ID.";
		  public static final String DELETE_QUEST_SUMMARY = "Delete a specific questionnaire";
		  public static final String DELETE_QUEST_OP = "You can use the questionnaire ID to delete a specific questionnaire.";
		  
		  public static final String PUT_QUEST_SUMMARY = "Update an existing questionnaire object";
		  public static final String PUT_QUEST_OP = "You can update an existing questionnaire object by questionnaire ID";
		  public static final String PUT_ANSWER_SUMMARY = "Update a single answer";
		  public static final String PUT_ANSWER_OP = "Update a single answer on a questionnaire by questionnaire ID and answer ID";
		  public static final String PUT_PAGES_SUMMARY = "Update a questionnaire active page";
		  public static final String PUT_STATUS_SUMMARY = "Update a questionnaire status";
		  public static final String PUT_NEW_STATUS = "The new status for a questionnaire";

		  public static final String POST_QUEST_SUMMARY = "Create a new questionnaire object";
		  public static final String POST_ROW_SUMMARY = "Create a new row item";
		  public static final String POST_ROW_OP = "Using this action, you can create a new row item within a rowgroup type";
		  public static final String POST_ANSWER_SUMMARY = "Create a new answer for a questionnaire";
		  
		  public static final String ERRORS_SUMMARY = "Return errors for a questionnaire";
		  public static final String ERRORS_OP = "Return an array of error values by questionnaire ID";
	  }
	  
	}


