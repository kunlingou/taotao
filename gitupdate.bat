@echo off
setlocal
title �������������

goto pull

:pull
echo ģ������:taotao
git pull
set /p a=�Ƿ��ύ(Ĭ��Ϊfalse,����yΪyes)��
if "%a%"=="y" ( goto commitandpush
) else (goto end)

:commitandpush
set /p comment=�����뱸ע��Ϣ��
git add .
git commit -m "%date:~0,4%%date:~5,2%%date:~8,2%-%comment%"
git push github master
git push gitee master
goto end

:end
pause
endlocal