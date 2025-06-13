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
