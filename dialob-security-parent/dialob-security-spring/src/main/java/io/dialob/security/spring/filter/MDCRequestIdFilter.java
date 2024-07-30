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
package io.dialob.security.spring.filter;

import io.dialob.security.tenant.LoggingContextKeys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;

public class MDCRequestIdFilter implements Filter {

  private String requestIdHeaderName;

  public MDCRequestIdFilter(String requestIdHeaderName) {
    this.requestIdHeaderName = requestIdHeaderName;
  }

  public MDCRequestIdFilter() {
    this("X-Request-Id");
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    final String newRequestIdHeaderName = filterConfig.getInitParameter("requestIdHeaderName");
    if (newRequestIdHeaderName != null) {
      this.requestIdHeaderName = newRequestIdHeaderName;
    }
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    try {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      String requestId = httpServletRequest.getHeader(this.requestIdHeaderName);
      if (requestId != null) {
        MDC.put(LoggingContextKeys.MDC_REQUEST_ID_KEY, requestId);
      }
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      MDC.remove(LoggingContextKeys.MDC_REQUEST_ID_KEY);
    }
  }

  @Override
  public void destroy() {
    MDC.remove(LoggingContextKeys.MDC_REQUEST_ID_KEY);
  }
}
