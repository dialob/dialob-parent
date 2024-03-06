import React, {useRef, useState, useEffect} from 'react';
import {Modal, Button, Label, Table, Tab, Segment, Dropdown, TextArea, Divider, Header} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideTranslation, updateItem, updateValuesetEntry, updateValidation, addLanguage, setActiveLanguage, deleteLanguage} from '../actions';
import { Understood } from '@resys/understood';
import * as Defaults from '../defaults';
import {MarkdownEditor} from './MarkdownEditor';
import md_strip_tags from 'remove-markdown';
import Papa from 'papaparse';
import FileSaver from 'file-saver';
import { isGlobalValueSet, findValueset } from "../helpers/utils"

const LanguageConfigurator = ({
  languages,
  activeLanguage,
  newLanguages,
  addLanguage,
  setActiveLanguage,
  deleteLanguage
}) => {
  const  [selectedLanguage, setSelectedLanguage] = useState(null);

  const rows = languages.map((l, i) => {
    const name = Defaults.LANGUAGES.find(c => c.code === l).name;
      return (
        <Table.Row key={i}>
          <Table.Cell onClick={() => setActiveLanguage(l)}>{l == activeLanguage ? <Label ribbon color='blue'>{name}</Label>: name}</Table.Cell>
          <Table.Cell><Button size='mini' disabled={l == activeLanguage} onClick={() => deleteLanguage(l)}>Delete</Button></Table.Cell>
        </Table.Row>);
    }
  );

  return (
    <Table celled>
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell>Language</Table.HeaderCell>
          <Table.HeaderCell collapsing />
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {rows}
      </Table.Body>
      <Table.Footer>
        <Table.Row>
          <Table.HeaderCell>Add new language: <Dropdown search selection
              options={newLanguages}
              value={selectedLanguage}
              onChange={(evt, data) => setSelectedLanguage(data.value)} /></Table.HeaderCell>
          <Table.HeaderCell singleLine>
            <Button size='mini' onClick={() => addLanguage(selectedLanguage, activeLanguage)} disabled={!selectedLanguage}>Copy from active</Button>
            <Button size='mini' onClick={() => addLanguage(selectedLanguage)} disabled={!selectedLanguage}>Create empty</Button>
          </Table.HeaderCell>
        </Table.Row>
      </Table.Footer>
    </Table>
  );
}


const TranslationEditor = ({value, onChange, metadata}) => {
  if (metadata.richText) {
    return (
      <MarkdownEditor onChange={(v) => onChange(v)} value={value} />
    );
  } else {
    return (
        <TextArea value={value} onChange={e => onChange(e.currentTarget.value)}/>
    );
  }
}

const Translator = ({translations, metadata, initialLanguage, onChange, languages}) => {
  return (
    <Segment basic style={{ height: 500 }}>
      <Understood
        initialTranslations={translations}
        format='keyToLanguage'
        initialLanguages={[initialLanguage]}
        onChangeItem={onChange}
        availableLanguages={languages}
        initialMetadata={metadata}
        components={{ Editor: TranslationEditor }}
        plaintextTranslation={md_strip_tags}
      />
    </Segment>
  );
};

function ScrollableSection({ title, content }) {
  return (
    <div style={{padding: "0px 8px" }}>
      <Header as='h4'style={{marginBottom: 0}}>{title}</Header>
      <div style={{padding: "8px", display: "flex", flexDirection: "column", maxHeight: "150px", overflowY: "auto"}}>
        {content?.map((item, index) => <p key={index} style={{margin: "2px 0px"}}>{item}</p>)}
      </div>
    </div>
  );
}

const ImportConfirmationDialog = ({ handleConfirm, handleClose, confirmationDialogData, open }) => {

  const itemIDs = confirmationDialogData?.itemIDs;
  const valueSetIDs = confirmationDialogData?.valueSetIDs;
  const notExistInFormArray = confirmationDialogData?.notExistInFormArray;

  const mainContent = !itemIDs && !valueSetIDs && !notExistInFormArray ?
    (
      <Header as='h4' style={{marginLeft: "8px"}}> No problems found during validation </Header>
    ) : (
      <>
        <ScrollableSection title="Items missing in CSV" content={itemIDs} />
        <Divider style={{margin: "8px 0px"}}/>
        <ScrollableSection title="Value sets missing in CSV" content={valueSetIDs} />
        <Divider style={{margin: "8px 0px"}}/>
        <ScrollableSection title="Missing on the form" content={notExistInFormArray} />
      </>
    )

  return (
    <Modal open={open} size='mini' centered={false}>
      <Modal.Header>Import confirmation</Modal.Header>
      <Modal.Content style={{padding: "8px 0px"}}>
        {mainContent}
      </Modal.Content>
      <Modal.Actions>
        <div style={{display: "flex", justifyContent: "flex-end"}}>
          <Button primary onClick={() => handleClose()}>Cancel</Button>
          <Button primary onClick={() => { handleConfirm(); handleClose(); }}>Confirm</Button>
        </div>
      </Modal.Actions>
    </Modal>
  )
}

const TranslationDialog = (props) => {
  const fileInputRef = useRef();
  const [parsedImportData, setParsedImportData] = useState(null);
  const [confirmationModalOpen, setConfirmationModalOpen] = useState(false);
  const [confirmationDialogData, setConfirmationDialogData] = useState(null);

  const closeConfirmationDialogModal = () => {
    setConfirmationModalOpen(false);
    setConfirmationDialogData(null);
    setParsedImportData(null);
  }

  const parse = (inputFile) => {
    return new Promise((resolve, reject) => {
      Papa.parse(inputFile, {
        header: false,
        transformHeader: h => h.trim(),
        skipEmptyLines: true,
        error: (error) => {
          console.error('CSV Parse error', error);
          reject(error);
        },
        complete: (results) => {
          resolve(results);
        }
      });
    });
  };

  const validateParsedFileHeaders = (data) => {
    if(data[0][0] !== props.formLabel || data[1][0] !== 'Item ID' || data[1][1] !== 'PageID' || data[1][2] !== 'ParentID ItemType' || data[1][3] !== 'Description')
      return false
    for(let i=4; i<data[1].length; i++){
      if(data[1][i].length !== 2)
        return false
    }
    return true
  }

  const validateParsedFileData = (data) => {
    let allItems = props.form.get('data').toJS();
    let valueSets = props.form.get('valueSets').toJS();
    let itemIDs = Object.keys(allItems);
    let valueSetIDs = valueSets.map((valueSet) => valueSet.id)
    let parsedDataIds = new Set();
    let notExistInFormArray = [];

    for (let i = 2; i < data.length; i++) {
      const firstColumn = data[i][0];
      const itemID = firstColumn.split(':')[1]
      parsedDataIds.add(itemID);
    }

    parsedDataIds.forEach((id) => {
      if(itemIDs.includes(id)){
        itemIDs = itemIDs.filter(itemID => itemID !== id && itemID !== "questionnaire");
      }else if(valueSetIDs.includes(id)){
        valueSetIDs = valueSetIDs.filter(valueSetID => valueSetID !== id);
      }else{
        notExistInFormArray.push(id);
      }
    });

    itemIDs = itemIDs?.length ? itemIDs : null;
    valueSetIDs = valueSetIDs?.length ? valueSetIDs : null;
    notExistInFormArray = notExistInFormArray?.length ? notExistInFormArray : null;

    return { itemIDs, valueSetIDs, notExistInFormArray }
  }

  const updateTranslation = (key, language, text) => {
    const keyTokens = key.split(':');
    if (keyTokens[0] === 'i') {
      // Item
      let attribute = '';
      if (keyTokens[2] === 'l') {
        props.updateItem(keyTokens[1], 'label', text, language);
      } else if (keyTokens[2] === 'd') {
        props.updateItem(keyTokens[1], 'description', text, language);
      } else if (keyTokens[2] === 'v') {
        props.updateValidation(keyTokens[1], parseInt(keyTokens[3]), 'message', text, language);
      }
    } else if (keyTokens[0] === 'v') {
        // Valueset
        props.updateValuesetEntry(keyTokens[1], parseInt(keyTokens[2]), null, text, language);
    }
  }

  const handleConfirmTranslation = () => {
    const languages = props.formLanguages.toJS();
    // checking if there is additional languages inside CSV
    if(parsedImportData[1].length - 4 > languages.length){
      for(let i = 4; i < parsedImportData[1].length; i++)
        if(!languages.includes(parsedImportData[1][i])){
          props.addLanguage(parsedImportData[1][i]);
        }
    }
    // translating
    for (let i = 2; i < parsedImportData.length; i++) {
      for(let j = 4; j < parsedImportData[i].length; j++){
        if(parsedImportData[i].length !== 0 && parsedImportData[i][0]){
          if(!confirmationDialogData?.notExistInFormArray){
            updateTranslation(parsedImportData[i][0], parsedImportData[1][j], parsedImportData[i][j].trim());
          }else{
            const key = parsedImportData[i][0].split(':')[1];
            if(!confirmationDialogData?.notExistInFormArray.includes(key)){
              updateTranslation(parsedImportData[i][0], parsedImportData[1][j], parsedImportData[i][j].trim());
            }
          }
        }
      }
    }
  }

  const fileChange = async (event) => {
    const csvResult = await parse(event.target.files[0]);
    const { data } = csvResult;
    if(validateParsedFileHeaders(data)){
      const dataValidationResult = validateParsedFileData(data);
      setParsedImportData(data)
      setConfirmationModalOpen(true);
      setConfirmationDialogData(dataValidationResult);
    }else{
      alert("CSV validation error, check the header");
    }
  };

  const getValueSetTranslations = () => {
    let valueSets = props.form.get('valueSets');
    if (valueSets) {
      let translations = {};
      let metadata = {key : {}};
      valueSets.forEach((vs, k) => {
        if (vs.get('entries')) {
          vs.get('entries').forEach((e, k) => {
            const key = `v:${vs.get('id')}:${k}:${e.get('id')}`;
            translations[key] = e.get('label').toJS();
            metadata.key[key] = {description: 'Valueset entry'};
            return true;
          });
        }
        return true;
      });
      return {translations, metadata};
    } else {
      return {translations: {}, metadata: {}};
    }
  }

  const getItemTranslations = () => {
    let translations = {};
    let metadata = {key : {}};
    let items = props.form.get('data');
    items.forEach((v, k) => {
      if (v.get('label')) {
        const key = `i:${v.get('id')}:l`;
        translations[key] = v.get('label').toJS();
        metadata.key[key] = {description: 'Item label', richText: v.get('type') === 'note'};
      }
      if (v.get('description')) {
        const key = `i:${v.get('id')}:d`;
        translations[key] = v.get('description').toJS();
        metadata.key[key] = {description: 'Item description', richText: true};
      }
      if (v.get('validations')) {
        v.get('validations').forEach((val, idx) => {
          const key = `i:${v.get('id')}:v:${idx}`;
          translations[key] = val.get('message').toJS();
          metadata.key[key] = {description: 'Validation'};
          return true;
        });
      }
      return true;
    });
    return {translations, metadata};
  }

  const getAllItemTranslations = () => {
    let translations = {};
    let metadata = {key : {}};
    let globalValueSets = props.form.getIn(['metadata', 'composer', 'globalValueSets']);
    let allItems = props.form.get('data').toJS();

    function visitItem(item, pageId, parent) {
      const key = `i:${item.id}:l`;
      translations[key] = item.label || "";
      metadata.key[key] = { description: 'Item label', richText: item.type === 'note', pageId: pageId, parent: `${parent.id} ${parent.type}`};
      if (item.description) {
        const key = `i:${item.id}:d`;
        translations[key] = item.description;
        metadata.key[key] = {description: 'Item description', richText: true, pageId: pageId, parent: `${parent.id} ${parent.type}`};
      }
      if (item.validations) {
        item.validations.forEach((val, idx) => {
          const key = `i:${item.id}:v:${idx}`;
          translations[key] = val.message;
          metadata.key[key] = {description: 'Validation', pageId: pageId, parent: `${parent.id} ${parent.type}`};
          return true;
        });
      }

      if(item.valueSetId){
        let valueSet = null;
        if(findValueset(props.form, item.valueSetId)){
          valueSet = findValueset(props.form, item.valueSetId).toJS();
        }
        if(valueSet && valueSet.entries){
          if(!isGlobalValueSet(globalValueSets, item.valueSetId)){
            valueSet.entries.forEach((entry, index) => {
              const key = `v:${valueSet.id}:${index}:${entry.id}`;
              translations[key] = entry.label;
              metadata.key[key] = {description: 'Valueset entry', pageId: pageId, parent: `${parent.id} ${parent.type}`};
            })
          }
        }
      }

      if (item.items instanceof Array) {
        item.items.forEach(childId => {
          let child = allItems[childId];
          visitItem(child, pageId, item);
        });
      }
    }

    let pageIds = allItems["questionnaire"].items;
    let pages = []
    pageIds.forEach((pageId) => {
      pages.push(allItems[pageId])
    })
    pages.forEach((page) => {
      visitItem(page, page.id, allItems["questionnaire"])
    })

    return {translations, metadata};
  }

  const getGlobalValueSetTranslations = () => {
    let globalValueSets = props.form.getIn(['metadata', 'composer', 'globalValueSets']);
    if (globalValueSets?.size > 0) {
      let translations = {};
      let metadata = {key : {}};

      globalValueSets.forEach((globalValueSet, k) => {
        let valueSet = null;
        if(findValueset(props.form, globalValueSet.get("valueSetId"))){
          valueSet = findValueset(props.form, globalValueSet.get("valueSetId")).toJS();
        }
        if(valueSet && valueSet.entries){
          valueSet.entries.forEach((entry, index) => {
            const key = `v:${valueSet.id}:${index}:${entry.id}`;
            translations[key] = entry.label;
            metadata.key[key] = {description: 'Valueset entry', pageId: "Root", parent: "Global list"};
          })
        }
      })
      return {translations, metadata}
    }else{
      return null;
    }
  }

  const createTranslationCSVRow = (value, key, translations) => {
    let row = [];
    row.push(key)
    row.push(value?.pageId);
    row.push(value?.parent);
    row.push(`${value.description} for ${key.split(":")[1]}`)
    props.formLanguages.forEach(l => {
      let name = translations.translations[key];
      row.push(name[l])
    })
    return row;
  }

  const createTranslationCSVformat = (allItemTranslations, globalValueSetTranslations, result) => {
    for(let [key, value] of Object.entries(allItemTranslations.metadata.key)){
      let row = createTranslationCSVRow(value, key, allItemTranslations);
      result.push(row)
    }
    if(globalValueSetTranslations){
      for(let [key, value] of Object.entries(globalValueSetTranslations.metadata.key)){
        let row = createTranslationCSVRow(value, key, globalValueSetTranslations);
        result.push(row)
      }
    }
    return result
  }

  const downloadFormData = () => {
    let allItemTranslations = getAllItemTranslations();
    let globalValueSetTranslations = getGlobalValueSetTranslations();

    let result = [];
    const firstRow = [props.formLabel]
    result.push(firstRow)
    const secondRow = ["Item ID", "PageID", "ParentID ItemType", "Description"];
    props.formLanguages.forEach(l => {
      secondRow.push(l);
    })
    result.push(secondRow)
    result = createTranslationCSVformat(allItemTranslations, globalValueSetTranslations, result)

    const csv = Papa.unparse(result);
    const blob = new Blob([csv], {type: 'text/csv'});
    FileSaver.saveAs(blob, `translation_${props.formLabel}.csv`);
  }

  if (props.translationOpen) {
    const newLanguages = Defaults.LANGUAGES
        .filter(lang => !props.formLanguages.contains(lang.code))
        .map(lang => ({key: lang.code, text: lang.name, value: lang.code}));

    const languages = {};
    props.formLanguages.forEach(l => {
      const lang = Defaults.LANGUAGES.find(c => c.code === l);
      languages[l] = {
        longName: lang.name || l,
        flag: lang.flag || l
      };
    })
    let itemTranslations = getItemTranslations();
    let valueSetTranslations = getValueSetTranslations();
    itemTranslations.metadata.language = languages;
    valueSetTranslations.metadata.language = languages;

    const panes = [
      {menuItem: 'Languages', render: () => <LanguageConfigurator
                                              languages={props.formLanguages}
                                              activeLanguage={props.language}
                                              newLanguages={newLanguages}
                                              addLanguage={props.addLanguage}
                                              setActiveLanguage={props.setActiveLanguage}
                                              deleteLanguage={props.deleteLanguage}
                                            />
      },
      {menuItem: 'Fields', render: () => <Translator
                                            key='fields'
                                            translations={itemTranslations.translations}
                                            metadata={itemTranslations.metadata}
                                            initialLanguage={props.language}
                                            onChange={updateTranslation}
                                            languages={props.formLanguages.toJS()} />
      },
      {menuItem: 'Lists', render: () =>  <Translator
                                            key='lists'
                                            translations={valueSetTranslations.translations}
                                            metadata={valueSetTranslations.metadata}
                                            initialLanguage={props.language}
                                            onChange={updateTranslation}
                                            languages={props.formLanguages.toJS()} />
      }
    ];

    return (
      <>
        <ImportConfirmationDialog
          open={confirmationModalOpen}
          handleClose={closeConfirmationDialogModal}
          confirmationDialogData={confirmationDialogData}
          handleConfirm={handleConfirmTranslation}
        />
        <Modal open size='large' centered={false}>
          <Modal.Header>Translation</Modal.Header>
          <Modal.Content >
            <Tab panes={panes} />
          </Modal.Content>
          <Modal.Actions>
            <div style={{display: "flex", justifyContent: "space-between"}}>
              <div>
                <Button size='tiny' icon='download' onClick={() => downloadFormData()}/>
                <Button size='tiny' icon='upload' onClick={() => fileInputRef.current.click()} />
                <input
                  ref={fileInputRef}
                  type='file'
                  accept='text/csv'
                  hidden
                  onChange={(e) => fileChange(e)}
                />
              </div>
              <Button primary onClick={() => props.hideTranslation()}>OK</Button>
            </div>
          </Modal.Actions>
        </Modal>
      </>
    );
  } else {
    return null;
  }
}

const TranslationDialogConnected = connect(
  state => ({
    translationOpen: state.dialobComposer.editor && state.dialobComposer.editor.get('translationOpen'),
    language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    formLanguages: state.dialobComposer.form.getIn(['metadata', 'languages']),
    formLabel: state.dialobComposer.form.getIn(['metadata', 'label']),
    form: state.dialobComposer.form && state.dialobComposer.form
  }), {
    hideTranslation,
    updateItem,
    updateValuesetEntry,
    updateValidation,
    addLanguage,
    setActiveLanguage,
    deleteLanguage
  }
)(TranslationDialog);

export {
  TranslationDialogConnected as default,
  TranslationDialog
};
