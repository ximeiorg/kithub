@echo off
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
cd /d %~dp0app
call ..\gradlew.bat assembleDebug