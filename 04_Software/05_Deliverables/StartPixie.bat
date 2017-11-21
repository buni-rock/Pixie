
@echo off
REM make variables local to the scope of the script
SETLOCAL  

REM returns the path of the batch file.
SET PARENT_DIR=%~dp0

set DLL_DIR=%PARENT_DIR%bin\dll
set LOG_DIR=%PARENT_DIR%log

set LOG_FILE=%LOG_DIR%\log.txt

set PATH=%DLL_DIR%;%PATH%

ECHO on
java -Dlogback.configurationFile=./cfg/logbackRelease.xml -jar bin/Pixie.jar 