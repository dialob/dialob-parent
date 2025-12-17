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
package io.dialob.session.boot;

import org.assertj.core.groups.Tuple;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TestUtils {
  public static final Comparator<Tuple> ORDER_AGNOSTIC_LIST_COMPARATOR = (t1, t2) -> {
    for (int i = 0; i < t1.toList().size(); i++) {
      Object o1 = t1.toList().get(i);
      Object o2 = t2.toList().get(i);
      if (o1 instanceof List && o2 instanceof List) {
        List<?> list1 = (List<?>) o1;
        List<?> list2 = (List<?>) o2;
        if (list1.size() != list2.size() || !new HashSet<>(list1).equals(new HashSet<>(list2))) {
          return -1;
        }
      } else if (!Objects.equals(o1, o2)) {
        return -1;
      }
    }
    return 0;
  };
}
