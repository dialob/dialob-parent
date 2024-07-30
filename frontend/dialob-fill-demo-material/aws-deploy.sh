#!/bin/bash
aws s3 sync ./build s3://cdn.resys.io/dialob/dialob-fill-demo-material/main --exclude '*.html' --delete --cache-control public,max-age=3600 --acl public-read 
aws s3 sync ./build s3://cdn.resys.io/dialob/dialob-fill-demo-material/main --include '*.html' --cache-control no-cache --acl public-read

