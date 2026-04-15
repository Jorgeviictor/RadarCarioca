@echo off
echo [Radar Carioca] Iniciando build debug...
call gradlew.bat assembleDebug --stacktrace
if %ERRORLEVEL% == 0 (
    echo.
    echo [BUILD SUCCESSFUL] APK gerado em app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo [BUILD FAILED] Verifique os erros acima.
)
echo.
echo [Aguardando... Pressione Ctrl+C para encerrar]
:LOOP
timeout /t 30 /nobreak >nul
goto LOOP
