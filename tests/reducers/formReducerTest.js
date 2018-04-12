import { formReducer } from "../../src/reducers/formReducer";
import * as Actions from "../../src/actions/constants";
import { addItem } from '../../src/actions';
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
      items: ["group1", "group2"]
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
});
