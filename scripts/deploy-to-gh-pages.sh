#!/bin/bash

set -o errexit

rm -rf site
mkdir site

git clone https://ccamel:$GH_TOKEN@github.com/ccamel/playground-binding.scala.git -b gh-pages --depth 1 site

cd site

git config user.email "$USER_EMAIL"
git config user.name "$USER_NAME"
git config push.default simple

# add changes
cp -R ../dist/* .

# deploy
if [[ `git status --porcelain` ]]; then
  git add --all .
  git commit -m "update playground-binding.scala showcases"

  git push --quiet
else
  echo "no changes"
fi

echo "done"
