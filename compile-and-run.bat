@echo off
del /Q /F /S "compiled"
del /Q /F /S "Compressed"
del /Q /F /S "Decompressed"
javac -d compiled -sourcepath . "app/App.java"

if %errorlevel% equ 0 (
    echo Compilation successful. Now running...
    cd compiled
    java app.App
) else (
    echo Compilation failed. 
)