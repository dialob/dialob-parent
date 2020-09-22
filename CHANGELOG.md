# next

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
