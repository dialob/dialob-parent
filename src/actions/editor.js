import * as Actions from './constants';

export function setActiveItem(itemId) {
  return {
    type: Actions.SET_ACTIVE_ITEM,
    itemId
  };
}

export function setActivePage(itemId) {
  return {
    type: Actions.SET_ACTIVE_PAGE,
    itemId
  };
}

export function addItem(config, parentItemId, afterItemId = null) {
  return {
    type: Actions.ADD_ITEM,
    config,
    parentItemId,
    afterItemId,
    saveNeeded: true
  };
}

export function changeItemType(config, itemId) {
  return {
    type: Actions.CHANGE_ITEM_TYPE,
    config,
    itemId,
    saveNeeded: true
  };
}

export function updateItem(itemId, attribute, value, language = null) {
  return {
    type: Actions.UPDATE_ITEM,
    itemId,
    attribute,
    value,
    language,
    saveNeeded: true
  };
}

export function setActiveLanguage(language) {
  return {
    type: Actions.SET_ACTIVE_LANGUAGE,
    language
  };
}

export function cancelConfirmation() {
  return {
    type: Actions.CANCEL_CONFIRMATION
  };
}

export function askConfirmation(action) {
  return {
    type: Actions.ASK_CONFIRMATION,
    action
  };
}

export function deleteItem(itemId) {
  return {
    type: Actions.DELETE_ITEM,
    itemId,
    confirm: true,
    saveNeeded: true
  };
}

export function showItemOptions(itemId) {
  return {
    type: Actions.SHOW_ITEM_OPTIONS,
    itemId
  };
}

export function hideItemOptions() {
  return {
    type: Actions.HIDE_ITEM_OPTIONS
  };
}

export function createValueset(forItem = null) {
  return {
    type: Actions.CREATE_VALUESET,
    forItem,
    saveNeeded: true
  };
}

export function createValuesetEntry(valueSetId) {
  return {
    type: Actions.CREATE_VALUESET_ENTRY,
    valueSetId,
    saveNeeded: true
  };
}

export function updateValuesetEntry(valueSetId, index, id, label, language) {
  return {
    type: Actions.UPDATE_VALUESET_ENTRY,
    valueSetId,
    index,
    id,
    label,
    language,
    saveNeeded: true
  };
}

export function deleteValuesetEntry(valueSetId, index) {
  return {
    type: Actions.DELETE_VALUESET_ENTRY,
    valueSetId,
    index,
    confirm: true,
    saveNeeded: true
  };
}

export function loadForm(formId) {
  return {
    type: Actions.LOAD_FORM,
    formId
  };
}

export function setForm(formData) {
  return {
    type: Actions.SET_FORM,
    formData
  };
}

export function saveForm() {
  return {
    type: Actions.SAVE_FORM
  };
}

export function setFormRevision(revision) {
  return {
    type: Actions.SET_FORM_REVISION,
    revision
  };
}
