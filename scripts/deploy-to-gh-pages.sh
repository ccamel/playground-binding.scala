#!/bin/bash

set -o errexit

git worktree add site gh-pages

cd site

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
