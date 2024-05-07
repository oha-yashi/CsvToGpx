@echo off
rem 選択して実行だと書き出しのアクセスが拒否されるので、バッチファイルに重ねるようにする

setlocal EnableDelayedExpansion

set APP_DIR=%~dp0
set APP_NAME=CsvToGpx.jar
set APP_PATH="%APP_DIR%%APP_NAME%"

set a=%*

call java -jar %APP_PATH% !a!

pause

exit
rem ここで終わり