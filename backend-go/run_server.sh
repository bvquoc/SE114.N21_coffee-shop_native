#!/bin/sh

echo "[LOG] Building Back-end execute file...\n"

go build main.go

echo "[LOG] Successful!\n"

echo "[LOG] Running server..."

nohup ./main &


