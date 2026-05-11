@echo off
cd /d "%~dp0..\backend"
mvn.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
