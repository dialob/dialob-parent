#!/bin/bash

# Dialob API URL
DIALOB_FORMS_URL=http://localhost:8085/dialob/api/forms

# Update or intall form file ($1)
function updateForm () {
	# Extract form name from json
	FORM_NAME=$(jq -r .name $1)
	echo "*** Updating $FORM_NAME..."
	
	# Try update (PUT) first
	RESPONSE=$(curl --write-out '%{http_code}' --silent -X PUT -H "Content-type: application/json" -d "@$1" --output /dev/null ${DIALOB_FORMS_URL}/${FORM_NAME}?force=true)
	
	if [ $RESPONSE -eq "404" ]; then
		# If not found install it as new (POST)
		echo "New form, installing..."
		INST_RESP=$(curl --write-out '%{http_code}' --silent -X POST -H "Content-type: application/json" -d "@$1" --output /dev/null ${DIALOB_FORMS_URL})
		if [ $INST_RESP -eq "201" ]; then
			echo "Installed"
		else
			eecho "Install failed: $INST_RESP "
		fi	
  elif [ $RESPONSE -eq "200" ]; then
  	echo "Updated"
  else
  	echo "Update failed: $RESPONSE"
	fi
}

# Install 

if [ -z "$1" ]; then
	echo "Usage: ./intstallForm.sh form.json"
	exit 1
fi

updateForm $1
