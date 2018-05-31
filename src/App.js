import React, {Component} from 'react';
import {Grid, Sticky} from 'semantic-ui-react';
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

require('./style.css');

class App extends Component {
  render() {
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
      </React.Fragment>);
  }
}

export default App;
