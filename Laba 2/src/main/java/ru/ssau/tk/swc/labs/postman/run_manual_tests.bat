@echo off
echo ========================================
echo  Manual Branch API Tests (Tomcat)
echo ========================================
echo.
echo Убедитесь, что Tomcat запущен на порту 8080
echo.
pause

echo Установка Newman (если не установлен)...
npm list -g newman || npm install -g newman

echo.
echo Запуск тестов для Manual ветки...
echo.

newman run manual_collection.json ^
  -r htmlextra,json,cli ^
  --reporter-htmlextra-export manual_report.html ^
  --reporter-json-export manual_results.json ^
  --verbose ^
  --timeout 30000

echo.
echo ========================================
echo Тесты завершены!
echo.
echo Отчеты:
echo 1. HTML отчет: manual_report.html
echo 2. JSON данные: manual_results.json
echo.
echo Для сравнения с framework:
echo 1. Запусти framework тесты
echo 2. Запусти compare_performance.py
echo ========================================
pause