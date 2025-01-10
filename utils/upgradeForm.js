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

    // Clean up notes with dangling rudiments
    if (item.type === 'note') {
      if (item.required) {
        console.error("Required set for note", item.id);
        delete item.required;
      }
      if (item.validations) {
        console.error("Validations set for note", item.id);
        delete item.validations;
      }      
    }

    // Convert null descriptions and translations
    if (item.description === null) {
      delete item.description;
    } else {
      for (let langKey in item.description) {
        if (item.description[langKey] === null) {
          delete item.description[langKey];
        }
      }
    }  

    // Fix broken descriptions
    if (item.description && item.description['language']) {
      delete item.description.language;
    }

    // Clean rest of null atributes
    for (const prop in item) {
      if (item[prop] === null) {
        delete item[prop];
      }
    }
  }

}

var convertValueSets = (valueSets) => {
  for (let valueSet of valueSets) {
    for (let entry of valueSet.entries) {
      // Remove empty language keys from entry labels
      delete entry.label[""];

      if (!entry.id) {
        console.error("Missing valueset entry ID for", valueSet.id, entry);
      }
    }

    // Delete valueset entries that do not have Id
    valueSet.entries = valueSet.entries.filter(e => !!e.id);
  }
}

// Convert main form items
convertBranch(json.data);
convertValueSets(json.valueSets);

console.log(JSON.stringify(json, null, 2));
