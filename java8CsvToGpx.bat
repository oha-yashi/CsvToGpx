@echo off

setlocal EnableDelayedExpansion

set APP_DIR=%~dp0
set APP_NAME=Java8CsvToGpx.jar
set APP_PATH="%APP_DIR%%APP_NAME%"

set a=%*

call java -jar %APP_PATH% !a!

pause

exit
