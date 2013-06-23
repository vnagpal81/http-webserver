@ECHO OFF
rem Code borrowed from Apache Tomcat bin/*.bat

rem Clear any previous options
set DOGMATIX_OPTS=

if not exist setenv.bat goto setenvDone
setenv.bat
:setenvDone

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
set VERSION=1.0
set JAR_FILE=%BASEDIR%\lib\dogmatix-%VERSION%.jar

if exist "%JAR_FILE%" goto doAction
echo Binary Distribution not found at %JAR_FILE%
goto exit

:doAction
set _EXECJAVA=%_RUNJAVA% -jar %JAR_FILE%

if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop
if ""%1"" == ""version"" goto doVersion
if ""%1"" == ""help"" goto doHelp

:printHelp
echo Usage:  dogmatix ( commands ... )
echo commands:
echo   start  [options]  	  Start Dogmatix server
echo          quiet               Start Dogmatix server in quiet mode
echo          verbose             Start Dogmatix server in verbose mode
echo          config "<file>"     Start Dogmatix server with the config file 
echo   stop              	  Stop Dogmatix server
echo   version           	  What version are you running?
echo   help              	  Displays server help info
goto exit

:doStart

rem Get remaining unshifted command line arguments
:setArgs
shift
if ""%1""=="""" goto doneSetArgs
if ""%1"" == ""quiet"" set DOGMATIX_OPTS=%DOGMATIX_OPTS% -l INFO
if ""%1"" == ""verbose"" set DOGMATIX_OPTS=%DOGMATIX_OPTS% -l DEBUG
if ""%1"" == ""config"" goto handleConfig 
shift
goto setArgs

:handleConfig
shift 
set DOGMATIX_OPTS=%DOGMATIX_OPTS% -f %1
shift
goto setArgs

:doneSetArgs

echo Starting Server...
goto execCmd

:doStop
shift
set DOGMATIX_OPTS=-a stop
echo Stopping Server...
goto execCmd

:doVersion
set DOGMATIX_OPTS=-v
goto execCmd

:doHelp
goto printHelp

:execCmd
rem Execute Java with the applicable properties
:execCmd
%_EXECJAVA% %DOGMATIX_OPTS%
goto end

:exit
exit /b 1

:end
exit /b 0