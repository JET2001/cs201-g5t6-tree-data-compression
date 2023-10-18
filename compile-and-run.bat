@echo off
javac -d compiled -sourcepath . "app/App.java"
echo "Compilation successful, now running..." 
@REM java -classpath compiled app.App (Why is this not working)
cd compiled
java app.App
