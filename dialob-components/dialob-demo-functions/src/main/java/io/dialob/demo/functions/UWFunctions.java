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
package io.dialob.demo.functions;

import com.google.common.collect.HashBasedTable;
import io.dialob.rule.parser.function.FunctionRegistry;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

public class UWFunctions {

  public static final HashBasedTable<String, String, BigDecimal> UWCOSTTABLE;
  static {
    UWCOSTTABLE = HashBasedTable.create();
    UWCOSTTABLE.put("F01", "I1", BigDecimal.valueOf(1000.0));
    UWCOSTTABLE.put("F02", "I1", BigDecimal.valueOf(1010.0));
    UWCOSTTABLE.put("F03", "I1", BigDecimal.valueOf(1020.0));
    UWCOSTTABLE.put("F04", "I1", BigDecimal.valueOf(1030.0));
    UWCOSTTABLE.put("F05", "I1", BigDecimal.valueOf(1040.0));
    UWCOSTTABLE.put("F06", "I1", BigDecimal.valueOf(1050.0));
    UWCOSTTABLE.put("F07", "I1", BigDecimal.valueOf(1060.0));
    UWCOSTTABLE.put("F08", "I1", BigDecimal.valueOf(1070.0));
    UWCOSTTABLE.put("F09", "I1", BigDecimal.valueOf(1080.0));
    UWCOSTTABLE.put("F10", "I1", BigDecimal.valueOf(1090.0));
    UWCOSTTABLE.put("F11", "I1", BigDecimal.valueOf(1100.0));
    UWCOSTTABLE.put("F12", "I1", BigDecimal.valueOf(1110.0));
    UWCOSTTABLE.put("F13", "I1", BigDecimal.valueOf(1120.0));

    UWCOSTTABLE.put("F01", "I2", BigDecimal.valueOf(2000.0));
    UWCOSTTABLE.put("F02", "I2", BigDecimal.valueOf(2010.0));
    UWCOSTTABLE.put("F03", "I2", BigDecimal.valueOf(2020.0));
    UWCOSTTABLE.put("F04", "I2", BigDecimal.valueOf(2030.0));
    UWCOSTTABLE.put("F05", "I2", BigDecimal.valueOf(2040.0));
    UWCOSTTABLE.put("F06", "I2", BigDecimal.valueOf(2050.0));
    UWCOSTTABLE.put("F07", "I2", BigDecimal.valueOf(2060.0));
    UWCOSTTABLE.put("F08", "I2", BigDecimal.valueOf(2070.0));
    UWCOSTTABLE.put("F09", "I2", BigDecimal.valueOf(2080.0));
    UWCOSTTABLE.put("F10", "I2", BigDecimal.valueOf(2090.0));
    UWCOSTTABLE.put("F11", "I2", BigDecimal.valueOf(2100.0));
    UWCOSTTABLE.put("F12", "I2", BigDecimal.valueOf(2110.0));
    UWCOSTTABLE.put("F13", "I2", BigDecimal.valueOf(2120.0));

    UWCOSTTABLE.put("F01", "I3", BigDecimal.valueOf(3000.0));
    UWCOSTTABLE.put("F02", "I3", BigDecimal.valueOf(3010.0));
    UWCOSTTABLE.put("F03", "I3", BigDecimal.valueOf(3020.0));
    UWCOSTTABLE.put("F04", "I3", BigDecimal.valueOf(3030.0));
    UWCOSTTABLE.put("F05", "I3", BigDecimal.valueOf(3040.0));
    UWCOSTTABLE.put("F06", "I3", BigDecimal.valueOf(3050.0));
    UWCOSTTABLE.put("F07", "I3", BigDecimal.valueOf(3060.0));
    UWCOSTTABLE.put("F08", "I3", BigDecimal.valueOf(3070.0));
    UWCOSTTABLE.put("F09", "I3", BigDecimal.valueOf(3080.0));
    UWCOSTTABLE.put("F10", "I3", BigDecimal.valueOf(3090.0));
    UWCOSTTABLE.put("F11", "I3", BigDecimal.valueOf(3100.0));
    UWCOSTTABLE.put("F12", "I3", BigDecimal.valueOf(3110.0));
    UWCOSTTABLE.put("F13", "I3", BigDecimal.valueOf(3120.0));

    UWCOSTTABLE.put("F01", "I4", BigDecimal.valueOf(4000.0));
    UWCOSTTABLE.put("F02", "I4", BigDecimal.valueOf(4010.0));
    UWCOSTTABLE.put("F03", "I4", BigDecimal.valueOf(4020.0));
    UWCOSTTABLE.put("F04", "I4", BigDecimal.valueOf(4030.0));
    UWCOSTTABLE.put("F05", "I4", BigDecimal.valueOf(4040.0));
    UWCOSTTABLE.put("F06", "I4", BigDecimal.valueOf(4050.0));
    UWCOSTTABLE.put("F07", "I4", BigDecimal.valueOf(4060.0));
    UWCOSTTABLE.put("F08", "I4", BigDecimal.valueOf(4070.0));
    UWCOSTTABLE.put("F09", "I4", BigDecimal.valueOf(4080.0));
    UWCOSTTABLE.put("F10", "I4", BigDecimal.valueOf(4090.0));
    UWCOSTTABLE.put("F11", "I4", BigDecimal.valueOf(4100.0));
    UWCOSTTABLE.put("F12", "I4", BigDecimal.valueOf(4110.0));
    UWCOSTTABLE.put("F13", "I4", BigDecimal.valueOf(4120.0));
  }

  private final FunctionRegistry functionRegistry;

  public UWFunctions(FunctionRegistry functionRegistry) {
    this.functionRegistry = functionRegistry;
  }

  @PostConstruct
  public void configureDefaultFunctions() {
    functionRegistry.configureFunction("uwCost", "cost", UWFunctions.class, true);
  }

  public static BigDecimal cost(String turnover, String indemnity) {
    return UWCOSTTABLE.get(turnover, indemnity);
  }

}
