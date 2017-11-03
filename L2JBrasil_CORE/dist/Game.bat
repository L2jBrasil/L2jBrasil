@echo off
title Game Server Console
:start
echo Starting L2JBrasil Game Server.
echo Visite https://www.L2JBrasil.com/
echo.
REM ------------------------------------------------------------------------
REM #======================================================================#
REM # You need to set here your JDK/JRE params in case of x64 bits System. #
REM # Remove the "REM" after set PATH variable                             #
REM # If you're not a x64 system user just leave                           # 
REM #======================================================================#
REM set PATH="type here your path to java jdk/jre (including bin folder)"
REM ------------------------------------------------------------------------

REM -------------------------------------
REM Default parameters for a basic server.
REM -------------------------------------
java  -Xmx512m -XX:+AggressiveOpts -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -cp ./lib/*; com.it.br.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause