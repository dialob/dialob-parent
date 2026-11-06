import { Completion, CompletionContext, CompletionResult } from '@codemirror/autocomplete';
import { isContextVariable } from '../../utils/ItemUtils';
import { ComposerState } from '../../types';

type TimeKind = 'date' | 'time';
type DiffKind = 'period' | 'duration';
type ValueKind = TimeKind | DiffKind;

const DATE_UNITS = ['year', 'years', 'month', 'months', 'week', 'weeks', 'day', 'days', 'hour', 'hours', 'hr', 'minute', 'minutes', 'min', 'second', 'seconds'];
const TIME_UNITS = ['hour', 'hours', 'hr', 'minute', 'minutes', 'min', 'second', 'seconds'];
const DURATION_UNITS = ['week', 'weeks', 'day', 'days', 'hour', 'hours', 'hr', 'minute', 'minutes', 'min', 'second', 'seconds'];

const CALCULATE_TIME_DIFF = 'calculateTimeDiff';
const CONVERT_TIME_DIFF = 'convertTimeDiff';

function unitCompletions(labels: string[], detailFor?: (label: string) => string): Completion[] {
  return labels.map(label => {
    const completion: Completion = { label, type: 'keyword' };
    if (detailFor) {
      completion.detail = detailFor(label);
    }
    return completion;
  });
}

function calculateTimeDiffDetail(label: string): string {
  return TIME_UNITS.includes(label) ? 'dates, times' : 'dates';
}

function convertTimeDiffDetail(label: string): string {
  return label === 'year' || label === 'years' || label === 'month' || label === 'months' ? 'period' : 'period, duration';
}

function optionsFor(name: string, kind: ValueKind | null): Completion[] {
  if (name === CALCULATE_TIME_DIFF) {
    if (kind === 'date') {
      return unitCompletions(DATE_UNITS);
    }
    if (kind === 'time') {
      return unitCompletions(TIME_UNITS);
    }
    return unitCompletions(DATE_UNITS, calculateTimeDiffDetail);
  }
  if (kind === 'period') {
    return unitCompletions(DATE_UNITS);
  }
  if (kind === 'duration') {
    return unitCompletions(DURATION_UNITS);
  }
  return unitCompletions(DATE_UNITS, convertTimeDiffDetail);
}

function identifierEndingAt(text: string, index: number): string | null {
  let end = index;
  while (end > 0 && /\s/.test(text[end - 1])) {
    end--;
  }
  let start = end;
  while (start > 0 && /[\w$]/.test(text[start - 1])) {
    start--;
  }
  if (start === end) {
    return null;
  }
  const identifier = text.slice(start, end);
  return /^[A-Za-z_$]/.test(identifier) ? identifier : null;
}

function skipQuoted(text: string, index: number, quote: '"' | "'"): number {
  let cursor = index + 1;
  while (cursor < text.length) {
    if (quote === "'" && text[cursor] === "'" && text[cursor + 1] === "'") {
      cursor += 2;
      continue;
    }
    if (quote === '"' && text[cursor] === '\\') {
      cursor += 2;
      continue;
    }
    if (text[cursor] === quote) {
      return cursor;
    }
    cursor++;
  }
  return text.length;
}

function splitTopLevelArgs(argText: string): string[] {
  const args: string[] = [];
  let current = '';
  let depth = 0;
  for (let i = 0; i < argText.length; i++) {
    const char = argText[i];
    if (char === '"' || char === "'") {
      const end = skipQuoted(argText, i, char);
      current += argText.slice(i, Math.min(end + 1, argText.length));
      i = end;
      continue;
    }
    if (char === '(') {
      depth++;
    } else if (char === ')') {
      depth = Math.max(0, depth - 1);
    } else if (char === ',' && depth === 0) {
      args.push(current.trim());
      current = '';
      continue;
    }
    current += char;
  }
  args.push(current.trim());
  return args;
}

function unclosedQuoteStart(text: string): number | null {
  let quote: '"' | "'" | null = null;
  let start = -1;
  for (let i = 0; i < text.length; i++) {
    const char = text[i];
    if (quote === null) {
      if (char === '"' || char === "'") {
        quote = char;
        start = i;
      }
      continue;
    }
    if (quote === "'" && char === "'" && text[i + 1] === "'") {
      i++;
      continue;
    }
    if (quote === '"' && char === '\\') {
      i++;
      continue;
    }
    if (char === quote) {
      quote = null;
      start = -1;
    }
  }
  return quote === null ? null : start;
}

function enclosingCall(beforeQuote: string): { name: string; args: string[] } | null {
  let depth = 0;
  let name: string | null = null;
  let argStart = -1;
  for (let i = 0; i < beforeQuote.length; i++) {
    const char = beforeQuote[i];
    if (char === '"' || char === "'") {
      i = skipQuoted(beforeQuote, i, char);
      continue;
    }
    if (char === '(') {
      if (depth === 0) {
        name = identifierEndingAt(beforeQuote, i);
        argStart = i + 1;
      }
      depth++;
      continue;
    }
    if (char === ')') {
      depth = Math.max(0, depth - 1);
      if (depth === 0) {
        name = null;
        argStart = -1;
      }
    }
  }
  if (depth !== 1 || argStart < 0 || name === null) {
    return null;
  }
  return { name, args: splitTopLevelArgs(beforeQuote.slice(argStart)) };
}

function isIdentifier(value: string): boolean {
  return /^[A-Za-z_][\w$]*$/.test(value);
}

function valueKind(name: string, form: ComposerState, seen: Set<string>): ValueKind | null {
  if (seen.has(name)) {
    return null;
  }
  const item = form.data[name];
  if (item?.type === 'date' || item?.type === 'time') {
    return item.type;
  }
  const variable = form.variables?.find(entry => entry.name === name);
  if (!variable) {
    return null;
  }
  if (isContextVariable(variable)) {
    return variable.contextType === 'date' || variable.contextType === 'time' ? variable.contextType : null;
  }
  return expressionKind(variable.expression, form, new Set(seen).add(name));
}

function expressionKind(expression: string | undefined, form: ComposerState, seen: Set<string>): DiffKind | null {
  if (!expression) {
    return null;
  }
  const match = expression.trim().match(/^([A-Za-z_][\w$]*)\s*-\s*([A-Za-z_][\w$]*)$/);
  if (!match) {
    return null;
  }
  const left = valueKind(match[1], form, seen);
  const right = valueKind(match[2], form, seen);
  if (left === 'date' && right === 'date') {
    return 'period';
  }
  if (left === 'time' && right === 'time') {
    return 'duration';
  }
  return null;
}

function operandKind(arg: string, form: ComposerState): TimeKind | null {
  const name = arg.trim();
  if (!isIdentifier(name)) {
    return null;
  }
  const kind = valueKind(name, form, new Set());
  return kind === 'date' || kind === 'time' ? kind : null;
}

function differenceKind(arg: string, form: ComposerState): DiffKind | null {
  const inline = arg.trim().match(/^([A-Za-z_][\w$]*)\s*-\s*([A-Za-z_][\w$]*)$/);
  if (inline) {
    const left = operandKind(inline[1], form);
    const right = operandKind(inline[2], form);
    if (left === 'date' && right === 'date') {
      return 'period';
    }
    if (left === 'time' && right === 'time') {
      return 'duration';
    }
    return null;
  }
  const name = arg.trim();
  if (!isIdentifier(name)) {
    return null;
  }
  const kind = valueKind(name, form, new Set());
  return kind === 'period' || kind === 'duration' ? kind : null;
}

function resolvedKind(name: string, args: string[], form: ComposerState): ValueKind | null {
  if (name === CALCULATE_TIME_DIFF && args.length === 3 && args[2] === '') {
    const left = operandKind(args[0], form);
    const right = operandKind(args[1], form);
    return left !== null && left === right ? left : null;
  }
  if (name === CONVERT_TIME_DIFF && args.length === 2 && args[1] === '') {
    return differenceKind(args[0], form);
  }
  return null;
}

function unitCall(beforeCursor: string): { name: string; args: string[] } | null {
  const quoteStart = unclosedQuoteStart(beforeCursor);
  if (quoteStart === null) {
    return null;
  }
  const call = enclosingCall(beforeCursor.slice(0, quoteStart));
  if (!call) {
    return null;
  }
  const expected = call.name === CALCULATE_TIME_DIFF ? 3 : call.name === CONVERT_TIME_DIFF ? 2 : 0;
  if (expected === 0 || call.args.length !== expected || call.args[expected - 1] !== '') {
    return null;
  }
  return call;
}

export function unitCompletion(context: CompletionContext, form: ComposerState): CompletionResult | null {
  const call = unitCall(context.state.sliceDoc(0, context.pos));
  if (!call) {
    return null;
  }
  const word = context.matchBefore(/[A-Za-z]*/);
  return {
    from: word ? word.from : context.pos,
    options: optionsFor(call.name, resolvedKind(call.name, call.args, form)),
    validFor: /^[A-Za-z]*$/
  };
}
