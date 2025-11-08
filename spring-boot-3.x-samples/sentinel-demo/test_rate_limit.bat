@echo off
setlocal enabledelayedexpansion

echo Starting rate limit test...

for /L %%i in (1,1,50) do (
    echo Test %%i:
    curl -s -o response.txt -w "Status: %%{http_code}" http://localhost:8081/sentinel-demo/api/hello
    echo Response: 
    type response.txt
    echo.
    ping -n 1 127.0.0.1 > nul
)

del response.txt
endlocal
echo Test completed.