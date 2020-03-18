#!/bin/bash

cd ~/Library/Android/sdk/tools

echo 列出所有的模拟器
./emulator -list-avds

# 启动模拟器
./emulator @Nexus_6_API_29

