import { formReducer } from "../../src/reducers/formReducer";
import * as Actions from "../../src/actions/constants";
import { addItem, updateItem, deleteItem } from '../../src/actions';
import Immutable from "immutable";
import sinon from "sinon";

const INITAL_STATE = Immutable.fromJS({
  _id: "e8fa5f95873b08b1013a5f1fdd16be41",
  _rev: "5-044c0e3931d84d4a67de422da46aa57a",
  data: {
    questionnaire: {
      id: "questionnaire",
      type: "questionnaire",
      label: { en: "New Dialog" },
      items: ["page1"]
    },
    page1: {
      id: "page1",
      type: "group",
      label: { en: "New Page" },
      items: ["group1", "group2", "group4"]
    },
    group1: {
      id: "group1",
      type: "group",
      label: { en: "New Group" },
      items: ["text1", "text2"]
    },
    group2: {
      id: "group2",
      type: "group",
      label: { en: "New Group" },
      items: ["text3", "text4", "group3"]
    },
    group3: {
      id: "group3",
      type: "group",
      label: { en: "New Group" },
      items: ["text5"]
    },
    group4: {
      id: "group4",
      type: "group",
      label: { en: "New Group" }
    },
    text1: { id: "text1", type: "text", label: { en: "Text" } },
    text2: { id: "text2", type: "text", label: { en: "Text" } },
    text3: { id: "text3", type: "text", label: { en: "Text" } },
    text4: { id: "text4", type: "text", label: { en: "Text" } },
    text5: { id: "text5", type: "text", label: { en: "Text" } }
  },
  metadata: {
    label: "Simple",
    created: "2018-04-10T08:58:40.585+0000",
    lastSaved: "2018-04-10T08:58:56.438+0000",
    valid: true,
    creator: "xxx",
    tenantId: "itest",
    savedBy: "xxx",
    languages: ["en"]
  }
});

describe("formReducer", () => {
  it ('Adds new item into the end of the group', () => {
    const state = formReducer(INITAL_STATE, addItem({type: 'text'}, 'group3'));
    expect(state).to.be.an.instanceof(Immutable.Map);
    expect(state.toJS().data.text6).to.deep.equal({id: 'text6', type: 'text'});
    expect(state.toJS().data.group3.items).to.deep.equal(['text5', 'text6']);
  });
  it ('Adds new item into group that doesn\'t have any items', () => {
    const state = formReducer(INITAL_STATE, addItem({type: 'text'}, 'group4'));
    expect(state).to.be.an.instanceof(Immutable.Map);
    expect(state.toJS().data.text6).to.deep.equal({id: 'text6', type: 'text'});
    expect(state.toJS().data.group4.items).to.deep.equal(['text6']);
  });
  it ('Adds new item into the group after given item', () => {
    const state = formReducer(INITAL_STATE, addItem({type: 'text'}, 'group2', 'text4'));
    expect(state).to.be.an.instanceof(Immutable.Map);
    expect(state.toJS().data.text6).to.deep.equal({id: 'text6', type: 'text'});
    expect(state.toJS().data.group2.items).to.deep.equal(['text3', 'text4', 'text6', 'group3']);
  });
  it ('Updates normal attribute of an item', () => {
    const state = formReducer(INITAL_STATE, updateItem('text5', 'activeWhen', 'true'));
    expect(state).to.be.an.instanceof(Immutable.Map);
    expect(state.toJS().data.text5.activeWhen).to.equal('true');
  });
  it ('Updates translated attribute of an item', () => {
    const state = formReducer(INITAL_STATE, updateItem('text5', 'label', 'meh', 'fi'));
    expect(state).to.be.an.instanceof(Immutable.Map);
    expect(state.toJS().data.text5.label).to.deep.equal({'en': 'Text', 'fi': 'meh'});
  });
  it ('Removes item and all descendants', () => {
    const state = formReducer(INITAL_STATE, deleteItem('group2'));
    expect(state).to.be.an.instanceof(Immutable.Map);
    // Descendants
    expect(state.toJS().data).to.not.have.property('group2');
    expect(state.toJS().data).to.not.have.property('group3');
    expect(state.toJS().data).to.not.have.property('text3');
    expect(state.toJS().data).to.not.have.property('text4');
    expect(state.toJS().data).to.not.have.property('text5');

    // Reference
    expect(state.toJS().data.page1.items).to.deep.equal(['group1', 'group4']);

    // Survivors
    expect(state.toJS().data).to.have.property('questionnaire');
    expect(state.toJS().data).to.have.property('page1');
    expect(state.toJS().data).to.have.property('group1');
    expect(state.toJS().data).to.have.property('group4');
    expect(state.toJS().data).to.have.property('text1');
    expect(state.toJS().data).to.have.property('text2');
  });
});
