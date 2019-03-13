import React, {Component} from 'react';
import {Grid, Sticky, Segment, Loader} from 'semantic-ui-react';
import {loadForm, setConfig} from './actions';
import MainMenu from './components/MainMenu';
import TreeView from './components/TreeView';
import Editor from './components/Editor';
import ErrorList from './components/ErrorList';
import * as Defaults from './defaults';
import ConfirmationDialog from './components/ConfirmationDialog';
import ItemOptionsDialog from './components/ItemOptionsDialog';
import FormOptionsDialog from './components/FormOptionsDialog';
import VariablesDialog from './components/VariablesDialog';
import IdChangeDialog from './components/IdChangeDialog';
import PreviewContextDialog from './components/PreviewContextDialog';
import ValueSetDialog from './components/ValueSetDialog';
import TranslationDialog from './components/TranslationDialog';
import FatalErrorDialog from './components/FatalErrorDialog';
import VersioningDialog from './components/VersioningDialog';
import NewTagDialog from './components/NewTagDialog';
import {connect} from 'react-redux';
import {findRoot} from './helpers/utils';

require('./style.css');

class DialobComposer extends Component {

  componentDidMount() {
    if (!this.config) {
      this.props.setConfig(this.props.configuration);
    }
    const rootItem = this.props.findRootItem();
    if (!rootItem) {
      this.props.loadForm(this.props.formId);
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.formId !== this.props.formId) {
      this.props.loadForm(this.props.formId);
    }
  }

  render() {
    const rootItem = this.props.findRootItem();
    if (!rootItem || !this.props.config)  {
      return <Segment basic padded><Loader active /></Segment>;
    }
    const {configuration, formId} = this.props;
    const marginTop = '42px';
    const menuWidth = Defaults.TREE_WIDTH;
    return (
      <React.Fragment>
        <MainMenu />
        <div >
          <TreeView marginTop={marginTop} menuWidth={menuWidth} />
          <div style={{marginLeft: menuWidth, marginTop}}>
            <Grid columns='equal'>
              <Grid.Row>
                <Grid.Column>
                  <Editor />
                </Grid.Column>
                <Grid.Column computer={4}>
                  <Sticky offset={50}>
                    <ErrorList />
                  </Sticky>
                </Grid.Column>
              </Grid.Row>
            </Grid>
          </div>
        </div>
        <ConfirmationDialog />
        <ItemOptionsDialog />
        <FormOptionsDialog />
        <VariablesDialog />
        <IdChangeDialog />
        <PreviewContextDialog />
        <ValueSetDialog />
        <TranslationDialog />
        <FatalErrorDialog />
        <VersioningDialog />
        <NewTagDialog />
      </React.Fragment>);
  }
}

const DialobComposerConnected = connect(
  state => ({
    config: state.dialobComposer.config,
    items: state.dialobComposer.form && state.dialobComposer.form.get('data'),
    get findRootItem() { return () => findRoot(this.items); }
  }),
  {
    loadForm,
    setConfig
  }
)(DialobComposer);

export {
  DialobComposerConnected as default,
  DialobComposer
};
