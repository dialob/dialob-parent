# 2.2.0

Upload and download valueset entries from CSV file.

For upload, click upload button in any valueset editor. CSV file structure:

|ID|en|fi|...|
|-|-|-|-|
|e1|English label|Finnish label|...
|e2|English label|Finnish label|...

* First row is header row
  * First column of header row must be `ID``
  * Rest of header columns must be two-letter language ID-s for labels
* First column of content rows is entry ID
* Rest of content colums are entry labels in corresponding language

**Notes**

* Empty rows are ignored
* Order of entries in the file is retained
* All defined languages are imported, in case form doesn't support a language, value remains hidden.
* All entry ID-s are imported as is, in case of conflicts or other problems, error messages will be displayed and these are to be resolved by user as normal.

For download, click download button in any valueset editor. Valueset entries are downloaded as CSV file in the same format as described above.

**Following import modes are supported**

* **Replace all** Replaces all valueset entries with values from file
* **Append** Appends values from file to existing valueset entries
* **Update** Updates existing entries by ID and adds new entries from file

# 2.1.2-5

Visibility and requirement rules are now edited in a separate section in the bottom of the page. Activate an item to edit.

# 2.1.2

Visibility and Requirement rules are now hidden for all items that are not being edited. When item is clicked and activated, all options will be visible.
> "For performance reasons."
