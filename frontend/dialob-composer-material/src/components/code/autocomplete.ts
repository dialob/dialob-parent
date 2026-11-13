import { CompletionContext, CompletionResult, CompletionSource, completeFromList } from '@codemirror/autocomplete';
import { ComposerState } from '../../types';
import { IGNORED_ITEM_TYPES, RESERVED_WORDS } from './language';
import { unitCompletion } from './unitCompletions';

type ContextualCompletion = (context: CompletionContext, form: ComposerState) => CompletionResult | null;

const contextualCompletions: ContextualCompletion[] = [
  unitCompletion,
];

export function dialobAutocomplete(form: ComposerState): CompletionSource {
  const itemIds = Object.values(form.data)
    .filter(item => !IGNORED_ITEM_TYPES.includes(item.type))
    .map(item => item.id);
  const variableIds = form.variables ? form.variables.map(variable => variable.name) : [];
  const general = completeFromList([
    ...RESERVED_WORDS,
    ...[...itemIds, ...variableIds].map(id => ({ label: id, type: 'variable' as const }))
  ]);

  return (context: CompletionContext) => {
    for (const completion of contextualCompletions) {
      const result = completion(context, form);
      if (result) {
        return result;
      }
    }
    return general(context);
  };
}
