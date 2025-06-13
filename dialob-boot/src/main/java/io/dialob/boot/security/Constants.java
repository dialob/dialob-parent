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
package io.dialob.boot.security;

import org.springframework.core.Ordered;

interface Constants {
  int API_CHAIN_ORDER = Ordered.HIGHEST_PRECEDENCE;
  int ACTUATOR_CHAIN_ORDER = 0;
  int WEBAPI_CHAIN_ORDER = 125;
  int COMPOSER_CHAIN_ORDER = 130;
  int QUESTIONNAIRE_CHAIN_ORDER = 140;
  int REVIEW_CHAIN_ORDER = 150;
  int ADMIN_CHAIN_ORDER = Ordered.LOWEST_PRECEDENCE;
}
