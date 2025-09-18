@echo off
echo Building File Transfer System...

REM Create build directory
if not exist build mkdir build

REM Compile Java files
echo Compiling Java files...
javac -d build src/*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Build successful!
echo.
echo To run the GUI application:
echo   java -cp build FileTransferUI
echo.
echo To run the command-line server:
echo   java -cp build Server
echo.
echo To run the command-line client:
echo   java -cp build Client
echo.
pause
