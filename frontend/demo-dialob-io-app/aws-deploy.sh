#!/bin/bash                        
aws s3 sync ./dist s3://cdn.resys.io/demo-dialob-io-app/dev --exclude '*.html' --delete --cache-control public,max-age=3600 --acl public-read 
aws s3 sync ./dist s3://cdn.resys.io/demo-dialob-io-app/dev --include '*.html' --cache-control no-cache --acl public-read

