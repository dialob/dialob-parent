#!/bin/bash                        
aws s3 sync ./dist s3://cdn.resys.io/dialob-composer-material/dev --exclude '*.html' --delete --cache-control public,max-age=3600 --acl public-read 
aws s3 sync ./dist s3://cdn.resys.io/dialob-composer-material/dev --include '*.html' --cache-control no-cache --acl public-read

