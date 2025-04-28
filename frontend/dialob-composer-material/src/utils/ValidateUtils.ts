import { RESERVED_WORDS, VALID_ID_PATTERN } from "../components/code/language"
import { ContextVariable, DialobItems, Variable } from "../types"

export const validateId = (value: string, items?: DialobItems, variables?: (ContextVariable | Variable)[]) => {
  return (
    VALID_ID_PATTERN.test(value)
    && RESERVED_WORDS.map(w => w.label).indexOf(value) === -1
    && (!items || items[value] === undefined)
    && (!variables || variables.map(v => v.name).indexOf(value) === -1)
  )
}
