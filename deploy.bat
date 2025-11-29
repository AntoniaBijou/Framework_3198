@echo off
setlocal enabledelayedexpansion

:: CONFIGURATION
set JAVA_HOME=C:\Program Files\Java\jdk-17
set TOMCAT_HOME=D:\ITU\Tomcat\apache-tomcat-10.1.34

set app_name=framework
set SRC_DIR=framework\java
set BUILD_DIR=framework\build
set JAR_DIR=dist
set JAR_FILE=%JAR_DIR%\%app_name%.jar

echo --- Nettoyage anciens fichiers ---
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%JAR_DIR%" rmdir /s /q "%JAR_DIR%"
mkdir "%BUILD_DIR%"
mkdir "%JAR_DIR%"

echo --- Compilation du framework ---
set FILES=
for /R "%SRC_DIR%" %%f in (*.java) do (
    set FILES=!FILES! "%%f"
)

javac -parameters -cp "%TOMCAT_HOME%\lib\servlet-api.jar" -d "%BUILD_DIR%" %FILES%
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR COMPILATION FRAMEWORK
    exit /b 1
)

echo --- Creation du jar du framework ---
jar cf "%JAR_FILE%" -C "%BUILD_DIR%" .

echo --- Copie du jar dans test/WEB-INF/lib ---
if not exist "test\WEB-INF\lib" mkdir "test\WEB-INF\lib"
copy "%JAR_FILE%" "test\WEB-INF\lib\" /Y

echo --- Compilation du projet test (Controllers + Models) ---
set TEST_SRC=test\java
set TEST_CLASSES=test\WEB-INF\classes
set FILES=

for /R "%TEST_SRC%" %%f in (*.java) do (
    set FILES=!FILES! "%%f"
)

javac -parameters -cp "%TOMCAT_HOME%\lib\servlet-api.jar;test\WEB-INF\lib\framework.jar" -d "%TEST_CLASSES%" %FILES%
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR COMPILATION TEST
    exit /b 1
)

echo --- Deploiement dans Tomcat ---
rmdir /s /q "%TOMCAT_HOME%\webapps\test"
xcopy "test" "%TOMCAT_HOME%\webapps\test" /E /I /Y

echo --- Nettoyage ---
rmdir /s /q "%BUILD_DIR%"
rmdir /s /q "%JAR_DIR%"

echo DEPLOIEMENT TERMINE AVEC SUCCES
pause