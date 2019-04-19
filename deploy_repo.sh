#!/bin/sh

setup_git() {
  git config --global user.email "${MAVEN_PUSH_EMAIL}"
  git config --global user.name "${MAVEN_PUSH_NAME}"
}

commit_files() {
  git add .
  git commit --author "${MAVEN_PUSH_NAME} <${MAVEN_PUSH_EMAIL}>" -m "Upload"
}

upload_files() {
  git push https://Trumeet:${MAVEN_PUSH_TOKEN}@github.com/Trumeet/maven.git HEAD:master
}

cd repo
setup_git
commit_files
upload_files
cd ..