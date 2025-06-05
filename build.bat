
@echo off
echo Uruchamiam docker-compose...
docker-compose up -d
if %ERRORLEVEL% neq 0 (
    echo Blad podczas uruchamiania docker-compose
    pause
    exit /b %ERRORLEVEL%
)
echo docker-compose uruchomiony pomyslnie.
pause

mvn clean install
