#!/usr/bin/env bash

PARENT_DIR=$(dirname "$0")

platform='unknown'
unamestr=`uname`
if [[ "$unamestr" == 'Linux' ]]; then
   platform='linux'
   java -Dlogback.configurationFile=./cfg/logbackRelease.xml -jar bin/Pixie.jar
elif [[ "$unamestr" == 'Darwin' ]]; then
   platform='Mac'
   java -Dlogback.configurationFile=./cfg/logbackRelease.xml -jar bin/Pixie.jar
fi
