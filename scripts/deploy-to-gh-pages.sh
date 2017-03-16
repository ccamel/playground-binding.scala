#!/bin/bash

set -o errexit

# initialize target/site
cd target

rm -rf site
mkdir site

git clone https://ccamel:$GH_TOKEN@github.com/ccamel/ccamel.github.io.git site

cd site

git config user.email "$USER_EMAIL"
git config user.name "$USER_NAME"

# add changes
mkdir -p playgrounds/playground-binding.scala
rm -rf playgrounds/playground-binding.scala/*
cp -R ../../dist/* playgrounds/playground-binding.scala/

# deploy
if ! git diff-index --quiet HEAD --; then
  git add --all .
  git commit -m "update playground-binding.scala showcases"

  git push --quiet
else
  echo "no changes"
fi

echo "done"
