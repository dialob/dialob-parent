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
package io.dialob.rule.parser.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveValueTypeTest {

  @Test
  void testIntegerSum() {
    var bil10 = PrimitiveValueType.INTEGER.parseFromString("10000000000");
    BinaryOperator<Object> operator = PrimitiveValueType.INTEGER.sumOp();
    Assertions.assertEquals(new BigInteger("20000000000"), operator.apply(bil10, bil10));
    Assertions.assertEquals(new BigInteger("10000000001"), operator.apply(bil10, BigInteger.valueOf(1)));
    Assertions.assertEquals(new BigInteger("10000000001"), operator.apply(BigInteger.valueOf(1), bil10));
    Assertions.assertEquals(BigInteger.valueOf(2), operator.apply(BigInteger.valueOf(1), BigInteger.valueOf(1)));
  }

  @Test
  void testIntegerMult() {
    var bil10 = PrimitiveValueType.INTEGER.parseFromString("10000000000");
    BinaryOperator<Object> operator = PrimitiveValueType.INTEGER.multOp();
    Assertions.assertEquals(new BigInteger("100000000000000000000"), operator.apply(bil10, bil10));
    Assertions.assertEquals(new BigInteger("20000000000"), operator.apply(bil10, BigInteger.valueOf(2)));
    Assertions.assertEquals(new BigInteger("20000000000"), operator.apply(BigInteger.valueOf(2), bil10));
    Assertions.assertEquals(BigInteger.valueOf(4), operator.apply(BigInteger.valueOf(2), BigInteger.valueOf(2)));
    Assertions.assertEquals(new BigInteger("4611686014132420609"), operator.apply(BigInteger.valueOf(Integer.MAX_VALUE), BigInteger.valueOf(Integer.MAX_VALUE)));
  }

  @Test
  void testIntegerNegate() {
    var bil10 = PrimitiveValueType.INTEGER.parseFromString("10000000000");
    Assertions.assertEquals(new BigInteger("-10000000000"), PrimitiveValueType.INTEGER.negate(bil10));
    Assertions.assertEquals(BigInteger.valueOf(-1), PrimitiveValueType.INTEGER.negate(BigInteger.valueOf(1)));
    Assertions.assertEquals(BigInteger.valueOf(0), PrimitiveValueType.INTEGER.negate(BigInteger.valueOf(0)));
    Assertions.assertNull(PrimitiveValueType.INTEGER.negate(null));
  }

}
