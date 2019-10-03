@echo off

rem Brisanje starih fajlova za knjigu
rmdir docs\ /S /Q

rem Kopiranje novih fajlova za knjigu
mkdir docs\
cd knjiga\
xcopy . ..\docs\ /E /Y /Q
cd ..
