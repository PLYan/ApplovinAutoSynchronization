#!/bin/bash
# emulator为android sdk/tools目录下程序
avds=`emulator -list-avds`
echo 列出所有的模拟器
i=0
for item in $avds
do
    echo [$i] $item
    let i+=1
done
echo 输入模拟器编号，例如0
read index
# 转换为数组
avds=($avds)
# echo ${avds[$index]}
# 启动输入的第index模拟器

cd ~/Library/Android/sdk/tools
#./emulator @Pixel_3_API_29
./emulator @${avds[$index]}


#emulator -avd ${avds[$index]}
