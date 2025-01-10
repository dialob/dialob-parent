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

public final class Permissions {

  private Permissions() {}

  public static final String QUESTIONNAIRES_GET = "questionnaires.get";
  public static final String QUESTIONNAIRES_PUT = "questionnaires.put";
  public static final String QUESTIONNAIRES_POST = "questionnaires.post";
  public static final String QUESTIONNAIRES_DELETE = "questionnaires.delete";
  public static final String FORMS_GET = "forms.get";
  public static final String FORMS_PUT = "forms.put";
  public static final String FORMS_DELETE = "forms.delete";
  public static final String FORMS_POST = "forms.post";
  public static final String AUDIT = "audit";
  public static final String ALL_TENANTS = "tenant.all";
  public static final String COMPOSER_VIEW = "composer.view";
  public static final String MANAGER_VIEW = "manager.view";

}
