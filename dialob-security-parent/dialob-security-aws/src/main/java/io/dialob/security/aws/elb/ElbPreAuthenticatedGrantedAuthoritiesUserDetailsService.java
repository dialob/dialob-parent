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
package io.dialob.security.aws.elb;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collection;

public class ElbPreAuthenticatedGrantedAuthoritiesUserDetailsService implements
  AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	/**
	 * Get a UserDetails object based on the user name contained in the given token, and
	 * the GrantedAuthorities as returned by the GrantedAuthoritiesContainer
	 * implementation as returned by the token.getDetails() method.
	 */
	public final UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)  {
		Assert.notNull(token.getDetails(), "token.getDetails() cannot be null");
		Assert.isInstanceOf(GrantedAuthoritiesContainer.class, token.getDetails());
		Collection<? extends GrantedAuthority> authorities = ((GrantedAuthoritiesContainer) token
				.getDetails()).getGrantedAuthorities();
		return createUserDetails(token, authorities);
	}

	/**
	 * Creates the final <tt>UserDetails</tt> object. Can be overridden to customize the
	 * contents.
	 *
	 * @param token the authentication request token
	 * @param authorities the pre-authenticated authorities.
	 */
	protected UserDetails createUserDetails(Authentication token,
                                          Collection<? extends GrantedAuthority> authorities) {
	  String subId = (String) token.getPrincipal();
		return new User(subId, "N/A", true, true, true, true, authorities);
	}
}
