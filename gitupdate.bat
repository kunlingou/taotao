@echo off
setlocal
title 代码更新与推送

goto pull

:pull
echo 模块名称:taotao
git pull
set /p a=是否提交(默认为false,输入y为yes)：
if "%a%"=="y" ( goto commitandpush
) else (goto end)

:commitandpush
set /p comment=请输入备注信息：
git add .
git commit -m "%date:~0,4%%date:~5,2%%date:~8,2%-%comment%"
git push github master
git push gitee master
goto end

:end
pause
endlocal