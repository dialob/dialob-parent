import { Note } from "@mui/icons-material";
import { Group } from "../items/Group";
import { SimpleField } from "../items/SimpleField";
import { LabelField, VisibilityField } from "../items/ItemComponents";
import { ChoiceEditor } from "../components/editors/ChoiceEditor";
import { DefaultValueEditor } from "../components/editors/DefaultValueEditor";
import { DescriptionEditor } from "../components/editors/DescriptionEditor";
import { LabelEditor } from "../components/editors/LabelEditor";
import { LocalizedStringEditor } from "../components/editors/LocalizedStringEditor";
import { PropertiesEditor } from "../components/editors/PropertiesEditor";
import { RuleEditor } from "../components/editors/RuleEditor";
import { RulesEditor } from "../components/editors/RulesEditor";
import { ValidationRuleEditor } from "../components/editors/ValidationRuleEditor";
import { LanguageEditor } from "../components/translations/LanguageEditor";
import { TranslationFileEditor } from "../components/translations/TranslationFileEditor";
import { MissingTranslations } from "../components/translations/MissingTranslations";
import NavigationTreeItem from "../components/tree/NavigationTreeItem";
import NavigationTreeView from "../components/tree/NavigationTreeView";
import ContextVariableRow from "../components/variables/ContextVariableRow";
import ContextVariables from "../components/variables/ContextVariables";
import ExpressionVariableRow from "../components/variables/ExpressionVariableRow";
import ExpressionVariables from "../components/variables/ExpressionVariables";
import ChoiceItem from "../components/ChoiceItem";
import ChoiceList from "../components/ChoiceList";
import GlobalList from "../components/GlobalList";
import { itemFactory } from "../items/ItemFactory";
import Editor from "../components/Editor";
import PageTabs from "../components/PageTabs";
import PropItem from "../components/PropItem";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import ConfirmationDialog from "../dialogs/ConfirmationDialog";
import ConvertConfirmationDialog from "../dialogs/ConvertConfirmationDialog";
import CreateTagDialog from "../dialogs/CreateTagDialog";
import FormOptionsDialog from "../dialogs/FormOptionsDialog";
import GlobalListsDialog from "../dialogs/GlobalListsDialog";
import ItemOptionsDialog from "../dialogs/ItemOptionsDialog";
import PreviewDialog from "../dialogs/PreviewDialog";
import TranslationDialog from "../dialogs/TranslationDialog";
import UploadValuesetDialog from "../dialogs/UploadValuesetDialog";
import VariablesDialog from "../dialogs/VariablesDialog";
import VersioningDialog from "../dialogs/VersioningDialog";
import { DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG, DEFAULT_VALUESET_PROPS } from "../defaults";
import DialobComposer from "../dialob/DialobComposer";
import * as BackendTypes from "../backend/types";
import * as DialobTypes from "../dialob/types";
import * as DefaultTypes from "../defaults/types";

// export for building the library
export {
  Group, SimpleField, Note, LabelField, VisibilityField, itemFactory,    // items
  ChoiceEditor, DefaultValueEditor, DescriptionEditor, LabelEditor, LocalizedStringEditor, PropertiesEditor, RuleEditor, RulesEditor, ValidationRuleEditor, // editors
  LanguageEditor, TranslationFileEditor, MissingTranslations, // translations
  NavigationTreeItem, NavigationTreeView, // tree
  ContextVariableRow, ContextVariables, ExpressionVariableRow, ExpressionVariables, // variables
  ChoiceItem, ChoiceList, GlobalList, // choice
  Editor, PageTabs, PropItem, // components
  ChoiceDeleteDialog, ConfirmationDialog, ConvertConfirmationDialog, CreateTagDialog, FormOptionsDialog, GlobalListsDialog,
  ItemOptionsDialog, PreviewDialog, TranslationDialog, UploadValuesetDialog, VariablesDialog, VersioningDialog, // dialogs
  DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG, DEFAULT_VALUESET_PROPS, // defaults
  DialobComposer, // composer
};

export type {
  BackendTypes, DialobTypes, DefaultTypes
}
