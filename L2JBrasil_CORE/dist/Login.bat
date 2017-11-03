@echo off
title Login Server Console
:start

echo Starting L2JBrasil Login Server.
echo Visite https://www.L2JBrasil.com/
echo.

REM -------------------------------------
REM Default parameters for a basic server.
REM -------------------------------------

java -Xmx32m -XX:+UseParallelGC -XX:+AggressiveOpts -XX:ParallelGCThreads=2 -cp ./../Game/lib/*; com.it.br.loginserver.L2LoginServer

if ERRORLEVEL 1 goto error
goto exit
:error
echo ErrorLevel = 1 (error), please read log
:exit
pause
exit
