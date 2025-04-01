#!/usr/bin/env bash
SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

POSITIONAL_ARGS=()

while [[ $# -gt 0 ]]; do
  case $1 in
    -o|--outdir)
      OUTDIR="$2"
      shift # past argument
      shift # past value
      ;;
    -r|--release)
      MODE="package"
      shift # past argument
      ;;
    -*|--*)
      echo "Unknown option $1"
      exit 1
      ;;
    *)
      POSITIONAL_ARGS+=("$1") # save positional arg
      shift # past argument
      ;;
  esac
done
set -- "${POSITIONAL_ARGS[@]}" # restore positional parameters

PROJECT_DIR=$1

if [ -z "$PROJECT_DIR" ]; then
  echo "No project dir set."
  exit 1
fi

PACKAGE_JSON=$PROJECT_DIR/package.json

if [ ! -e $PACKAGE_JSON ]; then
  echo "$PACKAGE_JSON does not exists."
  exit 1
fi

HAS_DIST=`jq '.scripts | has("dist")' < $PACKAGE_JSON`
if [ "$HAS_DIST" = "false" ]; then
  echo "$(basename $PROJECT_DIR): No dist target "
  exit 0
fi

if [ -z "$OUTDIR" ]; then
  echo "No output folder set."
  exit 1
else
  mkdir -p $OUTDIR
fi

NAME=$(jq -r .name $PROJECT_DIR/package.json)

case $MODE in
package)
  VERSION=$(jq -r .version $PROJECT_DIR/package.json)
  ;;
*)
  VERSION=$GITHUB_REF_NAME
  ;;
esac

if [ -z "$VERSION" ]; then
  echo "Version not set."
  exit 1
fi

PROJECT_NAME=$(echo "$NAME" | awk -F'/' '{print $NF}')

cd $PROJECT_DIR
DIST_BASE=https://${CDN_DOMAIN}/${GITHUB_REPOSITORY_OWNER}/${PROJECT_NAME}/${VERSION} pnpm run dist
cd -

if [ -e "$PROJECT_DIR/dist" ]; then
  tar vzcf $OUTDIR/$PROJECT_NAME-${VERSION//\//-}.tar.gz --transform "s|^.|$PROJECT_NAME/$VERSION|" --show-stored-names -C $PROJECT_DIR/dist .
fi
-
