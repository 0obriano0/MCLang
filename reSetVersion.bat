@echo off
REM 只針對以 MCLang 開頭的資料夾、core 資料夾、以及根目錄的 pom.xml 批次更改 parent 版本號

setlocal enabledelayedexpansion

chcp 65001 >nul

set /p NEWVER=請輸入新的 MCLang-parent 版本號（例如 2k7 或 1.0.1）: 

REM 處理所有以 MCLang 開頭的資料夾
for /d %%D in (MCLang*) do (
    if exist "%%D\pom.xml" (
        echo 處理 %%D\pom.xml
        REM copy /Y "%%D\pom.xml" "%%D\pom.xml.bak"
        powershell -NoLogo -NoProfile -Command ^
          "$pom = Get-Content -Raw -Encoding UTF8 '%%D\pom.xml';" ^
          "$pom = [System.Text.RegularExpressions.Regex]::Replace($pom, '(<parent>.*?<version>)(.*?)(</version>)', '${1}%NEWVER%${3}', 'Singleline');" ^
          "$pom | Set-Content -Encoding UTF8 '%%D\pom.xml'"
    )
)

REM 處理 core 資料夾
if exist "core\pom.xml" (
    echo 處理 core\pom.xml
    REM copy /Y "core\pom.xml" "core\pom.xml.bak"
    powershell -NoLogo -NoProfile -Command ^
      "$pom = Get-Content -Raw -Encoding UTF8 'core\pom.xml';" ^
      "$pom = [System.Text.RegularExpressions.Regex]::Replace($pom, '(<parent>.*?<version>)(.*?)(</version>)', '${1}%NEWVER%${3}', 'Singleline');" ^
      "$pom | Set-Content -Encoding UTF8 'core\pom.xml'"
)

REM 處理根目錄自己的 pom.xml
if exist "pom.xml" (
    echo 處理 pom.xml
    REM copy /Y "pom.xml" "pom.xml.bak"
    powershell -NoLogo -NoProfile -Command ^
      "$pom = Get-Content -Raw -Encoding UTF8 'pom.xml';" ^
      "$pom = [System.Text.RegularExpressions.Regex]::Replace($pom, '(<parent>.*?<version>)(.*?)(</version>)', '${1}%NEWVER%${3}', 'Singleline');" ^
      "$pom | Set-Content -Encoding UTF8 'pom.xml'"
)

echo 處理完成，所有 MCLang*、core 目錄下以及根目錄的 pom.xml parent 版本都已替換為 %NEWVER%
pause