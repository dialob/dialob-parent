import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */
const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    'home',
    '01-introduction',
    '02-basic-operations',
    '03-advanced-operations',
    '04-input-and-output-types',
    '05-dialob-expression-language-del',
    '06-options-and-settings',
    '07-new-form-walkthrough',
    '08-customization',
  ],
};

export default sidebars;
