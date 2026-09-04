@echo off
PowerShell -Command "Start-Process PowerShell -ArgumentList '-ExecutionPolicy Bypass -File ""%~dp0requirements.ps1""' -Verb RunAs"