;-------------------------------------------------------------------------------
;
; Jetrix Launcher
; 
; based on the Java Launcher from 
; http://nsis.sf.net/archive/nsisweb.php?page=326
;
;-------------------------------------------------------------------------------

Name "Jetrix TetriNET Server"
Caption "Jetrix TetriNET Server"
Icon "jetrix.ico"
OutFile "jetrix.exe"

SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

!define ARGS "-Djava.library.path=lib -jar lib/jetrix-@version@.jar"

Section ""
  Call GetJRE
  Pop $R0

  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" ${ARGS}'

  SetOutPath $EXEDIR
  Exec $0
SectionEnd


;
; Find JRE (javaw.exe)
; 1 - in JAVA_HOME environment variable
; 2 - in the registry
; 3 - assume javaw.exe in current dir or PATH

Function GetJRE

  Push $R0
  Push $R1

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound

  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"

  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"
        
 JreFound:
  Pop $R1
  Exch $R0

FunctionEnd
