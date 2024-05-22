@echo off

set APP_DIR=src
set JAR_NAME=CsvToGpx.jar
set MAN_NAME=%APP_DIR%\CsvToGpx.mani
set MAIN_CLASS=CsvToGpx

cd

echo delete jar file if it exsit 
if exist %JAR_NAME% del %JAR_NAME%

rem echo delete class files
del %APP_DIR%\*.class

echo make class files
call javac %APP_DIR%\%MAIN_CLASS%.java
rem メインのCsvEdit.javaだけコンパイルにかければ依存関係にある他のjavaからclassができる

echo make jar
call jar cvf %JAR_NAME% %APP_DIR%
call jar uvfm %JAR_NAME% %MAN_NAME%

echo check jar
call jar tvf %JAR_NAME%

pause