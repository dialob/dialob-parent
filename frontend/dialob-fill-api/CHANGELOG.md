# next
* 1.4.8
* Improves error message

# 1.4.6
* Fixes issue from Immer upgrade

# 1.4.5
* Errors with the same id & code replace existing ones. This should fix issues with persistent errors after locale change.

# 1.4.4
* Fix state initialization

# 1.4.3
* Fix accidentally removed attribute `session.id`

# 1.4.2
* `CLIENT` error type has been removed. It wasn't really used anyway.
* Errors during sync are no longer thrown. They can be handled through a listener.

# 1.4.1
* Package renamed to `@dialob/fill-api` and is published to npmjs
* Package is built with CommonJS module target

# 1.4.0
* Handle incoming context and expression variable value ITEM messages, make them available from `session.getVariable('id')`.

# 1.3.1
* Fixes issue where on a big form sometimes event listeners go out of sync, which for example can
result in rendering issues when using React

# 1.3.0
* Implemented SET_LOCALE action (`session.setLocale(locale)`) (Requires Dialob Backend 1.0.8)

# 1.2.3
* Fix bug where an answer could get lost if a sync took a long time

# 1.2.2
* Implemented GOTO action (`session.goToPage(pageId)`)
