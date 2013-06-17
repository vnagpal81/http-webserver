@ECHO OFF
rem Code borrowed from Apache Tomcat bin/*.bat

rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto gotJdkHome
if not "%JRE_HOME%" == "" goto gotJreHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo At least one of these environment variable is needed to run this program
goto exit

:gotJreHome
if not exist "%JRE_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJavaHome
goto okJavaHome

:gotJdkHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javac.exe" goto noJavaHome
if not "%JRE_HOME%" == "" goto okJavaHome
set "JRE_HOME=%JAVA_HOME%"
goto okJavaHome

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
echo NB: JAVA_HOME should point to a JDK not a JRE
goto exit

:okJavaHome
rem Set standard command for invoking Java.
rem Note that NT requires a window name argument when using start.
rem Also note the quoting as JAVA_HOME may contain spaces.
set _RUNJAVA="%JRE_HOME%\bin\java"


set BASEDIR=%DOGMATIX_HOME%
if exist %BASEDIR% goto baseDirSet
set BASEDIR=%CD%\..

:baseDirSet
set VERSION=1.0-jar-with-dependencies
set JAR_FILE=%BASEDIR%\target\dogmatix-%VERSION%.jar

if exist "%JAR_FILE%" goto doAction
echo Binary Distribution not found at %JAR_FILE%
goto exit

:doAction
if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop
if ""%1"" == ""version"" goto doVersion

echo Usage:  dogmatix ( commands ... )
echo commands:
echo   start             Start Dogmatix server
echo   stop              Stop Dogmatix server
echo   version           What version are you running?
goto exit

:doStart
set _EXECJAVA=%_RUNJAVA% -jar %JAR_FILE%
echo Starting Server...
goto execCmd

:doStop
shift
set ACTION=stop
set DOGMATIX_OPTS=-a %ACTION%
echo Stopping Server...
goto execCmd

:doVersion
set DOGMATIX_OPTS=-v
goto execCmd

:execCmd
rem Get remaining unshifted command line arguments and save them in the
set CMD_LINE_ARGS=

:setArgs
shift
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

rem Execute Java with the applicable properties
:execCmd
%_EXECJAVA% %DOGMATIX_OPTS% %CMD_LINE_ARGS%
goto end

:exit
exit /b 1

:end
exit /b 0