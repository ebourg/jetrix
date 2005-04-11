@echo off

SETLOCAL

IF NOT JAVA_HOME == "" SET JAVA_EXE=%JAVA_HOME%\bin\java
IF JAVA_HOME == "" SET JAVA_EXE=java

%JAVA_EXE% -cp lib/jetrix-@version@.jar net.jetrix.tools.patcher.JetrixUpdate
