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
package io.dialob.form.service;

import io.dialob.api.form.*;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.form.service.api.validation.FormItemCopier;
import io.dialob.rule.parser.api.RuleExpressionCompiler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toList;

public class DialobFormItemCopier implements FormItemCopier {

  private final RuleExpressionCompiler compiler;

  private final FormIdRenamer renamerService;

  public DialobFormItemCopier(RuleExpressionCompiler compiler, FormIdRenamer renamerService) {
    this.compiler = compiler;
    this.renamerService = renamerService;
  }

  private String findNextID(Map<String, FormItem> formData, String id) {
    int suffix = 0;
    String nextID;
    do {
      nextID = id + ++suffix;
    } while (formData.keySet().contains(nextID));
    return nextID;
  }

  private String findNextValuesetId(Form form, String id) {
    int suffix = 0;
    String nextID;
    do {
      nextID = id + ++suffix;
    } while (findValueSet(form, nextID) != null); // TODO: Optimize
    return nextID;
  }

  private FormValueSet findValueSet(Form form, String valueSetId) {
    return form.getValueSets().stream().filter(vs -> vs.getId().equals(valueSetId)).findFirst().orElse(null); // TODO: Optimize
  }

  private Optional<FormItem> findContainerItem(Form form, String itemId) {
    return form.getData().values().stream().filter(i -> i.getItems().contains(itemId)).findFirst();
  }

  private String copySingleItem(ImmutableForm.Builder formBuilder, Form form, Map<String, String> idRenameMap, FormItem sourceItem) {
    Map<String, FormItem> formData = form.getData();
    String nextID = findNextID(formData, sourceItem.getId());
    ImmutableFormItem.Builder builder = ImmutableFormItem.builder()
      .from(sourceItem)
      .id(nextID);
    idRenameMap.put(sourceItem.getId(), nextID);
    // Children
    builder.items(sourceItem.getItems().stream().map(childId -> copySingleItem(formBuilder, form, idRenameMap, formData.get(childId))).collect(toList()));

    // ValueSets
    if (sourceItem.getValueSetId() != null) {
      String newValueSetId = findNextValuesetId(form, sourceItem.getValueSetId());
      FormValueSet sourceValueSet = findValueSet(form, sourceItem.getValueSetId());
      FormValueSet newValueSet = ImmutableFormValueSet.builder().from(sourceValueSet)
        .id(newValueSetId).build();
      builder.valueSetId(newValueSetId);
      formBuilder.addValueSets(newValueSet);
    }
    formBuilder.putData(nextID, builder.build());
    return nextID;
  }

  private void idRenamerSingleItem(Map<String, FormItem> renameItems, Form form, String sourceItemId, Map<String, String> idRenameMap) {
    idRenameMap.forEach((key, value) -> {
      UnaryOperator<String> idRenamer = compiler.createIdRenamer(key, value);
      FormItem renamedItem = renamerService.renameAttributes(renameItems.get(sourceItemId), idRenamer, key, value);
      renameItems.put(renamedItem.getId(), renamedItem);
      // Children
      renamedItem.getItems().forEach(childId -> idRenamerSingleItem(renameItems, form, childId, idRenameMap));
    });
  }

  @Override
  public Pair<Form, List<FormValidationError>> copyFormItem(Form form, String idToCopy) {
    ImmutableForm.Builder formBuilder = ImmutableForm.builder().from(form);
    List<FormValidationError> errors = new ArrayList<>();
    Map<String, FormItem> formData = form.getData();
    FormItem sourceItem = formData.get(idToCopy);
    if (sourceItem == null) {
      errors.add(ImmutableFormValidationError.builder()
        .itemId(idToCopy)
        .message("FORM_SOURCE_ITEM_NOT_FOUND")
        .type(FormValidationError.Type.GENERAL).build());
      return Pair.of(form, errors);
    }

    Map<String, String> idRenameMap = new HashMap<>();

    final String newId = copySingleItem(formBuilder, form, idRenameMap, sourceItem);

    // Update container, if any
    findContainerItem(form, idToCopy).ifPresent(containerItem -> {
      ImmutableFormItem.Builder formItemBuilder = ImmutableFormItem.builder().from(containerItem);
      int index = containerItem.getItems().indexOf(idToCopy);
      List<String> itemList = new ArrayList<>(containerItem.getItems());
      itemList.add(index + 1, newId);
      formItemBuilder.items(itemList);
      formBuilder.putData(containerItem.getId(), formItemBuilder.build());
    });

    // Update variable references within new branch
    Map<String, FormItem> renamedItems = new HashMap<>();
    ImmutableForm build = formBuilder.build();
    renamedItems.putAll(build.getData());
    idRenamerSingleItem(renamedItems, build, newId, idRenameMap);
    formBuilder.data(renamedItems);
    return Pair.of(formBuilder.build(), errors);
  }



}


