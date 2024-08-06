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
package io.dialob.security.spring.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

@Aspect
@Slf4j
public class OAuth2AuthenticationAspect {

  @Around("within(org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient+) && execution(* getTokenResponse(..))")
  public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
    //Advice
    try {
      Object returnValue = joinPoint.proceed();
      if (returnValue instanceof OAuth2AccessTokenResponse) {
        OAuth2AccessTokenResponse token = (OAuth2AccessTokenResponse) returnValue;
        LOGGER.debug("Assigned token scopes: {}", token.getAccessToken().getScopes());
      }
      return returnValue;
    } catch (Throwable t) {
      LOGGER.debug("Could not get token.", t);
      throw t;
    }
  }

  @Around("within(org.springframework.security.authentication.AuthenticationProvider+) && execution(* authenticate(..))")
  public Object aroundAuthenticate(ProceedingJoinPoint joinPoint) throws Throwable {

    Object arg = joinPoint.getArgs()[0];
    LOGGER.debug("Try authentication: {}", arg);
    if (arg instanceof OAuth2LoginAuthenticationToken) {
      OAuth2LoginAuthenticationToken token = (OAuth2LoginAuthenticationToken) arg;
      LOGGER.debug("response code: {}", token.getAuthorizationExchange().getAuthorizationResponse().getCode());
      LOGGER.debug("request state : {}", token.getAuthorizationExchange().getAuthorizationRequest().getState());
      LOGGER.debug("response state: {}", token.getAuthorizationExchange().getAuthorizationResponse().getState());
      LOGGER.debug("request uri : {}", token.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
      LOGGER.debug("response uri: {}", token.getAuthorizationExchange().getAuthorizationResponse().getRedirectUri());
    }
    try {
      Object returnValue = joinPoint.proceed();
      LOGGER.debug("Authentication result: {}", returnValue);
      return returnValue;
    } catch (Throwable t) {
      LOGGER.error("Authentication failure", t);
      throw t;
    }

  }

}
