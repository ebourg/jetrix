@echo off

SETLOCAL

IF NOT JAVA_HOME == "" SET JAVA_EXE="%JAVA_HOME%\bin\java"
IF JAVA_HOME == "" SET JAVA_EXE=java

%JAVA_EXE% -Xmx128m -Djava.library.path=lib -jar lib/jetrix-launcher-@version@.jar %1
