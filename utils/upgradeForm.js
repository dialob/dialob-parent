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
/*
Upgrades forms from Dialob 2.x to 3.x
  * Correct input types for suveys
  * Remove null description values

Usage:
  node upgradeForm.js <form.json> > <output.json>

  form.json   - form to convert
  output.json - result is output to stdout, redirect to file as needed

*/

let json = JSON.parse(require('fs').readFileSync(process.argv[2]));

var convertBranch = (branch) => {
  let questionnaire = null;
  for (let itemId in branch) {
    let item = branch[itemId];

    // Old version surveygroup conversion
    if (item.type === 'group' && item.className && item.className.indexOf('survey') > -1) {
      item.type = 'surveygroup';
      item.className.splice(item.className.indexOf('survey'), 1);
    }

    // Old version surveyitem conversion
    if (item.type === 'text' && item.className.indexOf('survey') > -1) {
      item.type = 'survey';
      item.className.splice(item.className.indexOf('survey'), 1);
    }

    // Convert null descriptions
    if (item.description === null) {
      delete item.description;
    } else {
      for (let langKey in item.description) {
        if (item.description[langKey] === null) {
          delete item.description[langKey];
        }
      }
    }

  }

}

// Convert main form items
convertBranch(json.data);

console.log(JSON.stringify(json, null, 2));
