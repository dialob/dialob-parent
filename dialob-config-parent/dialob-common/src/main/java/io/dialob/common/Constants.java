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
package io.dialob.common;

import java.util.regex.Pattern;

public final class Constants {

  // Questionnaire cache (depends on redis)
  public static final String QUESTIONNAIRE_CACHE_NAME = "questionnaireCache";

  // Form model cache
  public static final String FORM_CACHE_NAME = "formCache";

  // Compiled Dialob forms cache
  public static final String PROGRAM_CACHE_NAME = "dialobProgramsCache";

  // Session state cache
  public static final String SESSION_CACHE_NAME = "sessionCache";

  // Cache session access permissions per user
  public static final String SESSION_ACCESS_CACHE_NAME = "sessionAccessCache";

  public static final String QUESTIONNAIRE_CACHE_MANAGER_BEAN = QUESTIONNAIRE_CACHE_NAME + "Manager";
  public static final String FORM_CACHE_MANAGER_BEAN = FORM_CACHE_NAME + "Manager";
  public static final String PROGRAM_CACHE_MANAGER_BEAN = PROGRAM_CACHE_NAME + "Manager";
  public static final String SESSION_CACHE_MANAGER_BEAN = SESSION_CACHE_NAME + "Manager";
  public static final String SESSION_ACCESS_CACHE_MANAGER_BEAN = SESSION_ACCESS_CACHE_NAME + "Manager";

  public static final String QUESTIONNAIRE = "questionnaire";
  public static final String GROUP = "group";
  public static final String ROWGROUP = "rowgroup";
  public static final String ROW = "row";
  public static final String NOTE = "note";
  public static final String PAGE = "page";
  public static final String SURVEYGROUP = "surveygroup";
  public static final String VARIABLE = "variable";
  public static final String CONTEXT = "context";
  public static final String TEXT = "text";
  public static final String LIST = "list";
  public static final String SURVEY = "survey";
  public static final String BOOLEAN = "boolean";
  public static final String DATE = "date";
  public static final String TIME = "time";
  public static final String NUMBER = "number";
  public static final String DECIMAL = "decimal";
  public static final String MULTICHOICE = "multichoice";

  public static final String ERROR_CODE_REQUIRED = "REQUIRED";

  public static final String VALID_ID_PATTERN = "^\\p{Alpha}[_\\p{Alnum}]*$";
  public static final Pattern VALID_ID_PATTERN_COMPILED = Pattern.compile(VALID_ID_PATTERN);

  /**
   * A regular expression pattern that defines a valid revision identifier.
   * This pattern matches strings containing 1 to 64 characters composed of
   * hexadecimal digits (0-9, a-f, A-F) and hyphens ('-').
   */
  public static final String VALID_REV_PATTERN = "[\\p{XDigit}-]{1,64}";
  public static final Pattern VALID_REV_PATTERN_COMPILED = Pattern.compile(VALID_REV_PATTERN);

  public static final String VALID_FORM_NAME_PATTERN = "[\\p{Alnum}-_]+";
  public static final Pattern VALID_FORM_NAME_PATTERN_COMPILED = Pattern.compile(VALID_FORM_NAME_PATTERN);

  public static final String VALID_FORM_ID_PATTERN = VALID_FORM_NAME_PATTERN;
  public static final Pattern VALID_FORM_ID_PATTERN_COMPILED = Pattern.compile(VALID_FORM_ID_PATTERN);

  public static final String VALID_FORM_TAG_PATTERN = "[\\p{Alnum}-_.]+";
  public static final Pattern VALID_FORM_TAG_PATTERN_COMPILED = Pattern.compile(VALID_FORM_TAG_PATTERN);

  public static final String QUESTIONNAIRE_ID_PATTERN = "[\\p{XDigit}-]{1,64}";
  public static final Pattern QUESTIONNAIRE_ID_PATTERN_COMPILED = Pattern.compile(QUESTIONNAIRE_ID_PATTERN);

  private Constants() { }

}
