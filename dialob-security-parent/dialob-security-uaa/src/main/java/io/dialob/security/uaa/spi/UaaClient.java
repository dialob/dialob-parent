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
package io.dialob.security.uaa.spi;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.dialob.security.uaa.spi.model.UaaGroup;
import io.dialob.security.uaa.spi.model.UaaGroupList;
import io.dialob.security.uaa.spi.model.UaaUser;
import io.dialob.security.uaa.spi.model.UaaUserList;

@Headers("Content-Type: application/json")
public interface UaaClient {

  @RequestLine("GET /Users/{userId}")
  UaaUser getUser(@Param("userId") String userId);

  @RequestLine("GET /Groups/{groupId}")
  UaaGroup getGroup(@Param("groupId") String groupId);

  @RequestLine("GET /Groups?filter=displayName eq \"{displayName}\"")
  UaaGroupList findGroupsByDisplayName(@Param("displayName") String displayName);

  @RequestLine("GET /Groups?filter=displayName sw \"{displayNamePrefix}\"")
  UaaGroupList findGroupsByDisplayNamePrefix(@Param("displayNamePrefix") String displayNamePrefix);

  @RequestLine("GET /Users?filter=userName eq \"{userName}\"")
  UaaUserList findUsersByUserName(@Param("userName") String userName);

}
