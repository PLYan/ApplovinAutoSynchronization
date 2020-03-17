#!/bin/bash

app_path=$(cd "$(dirname "$0")"; pwd)/Applovin_iOS_SDK.app

app_identifier=$1
call_back_url=$2
sdk_key=$3
app_name=$4
shutdown_sim='Y'

if [ -z "$app_identifier" ]; then
    echo "app_identifier is empty！"
    exit 0
fi

if [ -z "$call_back_url" ]; then
    echo "call_back_url is empty！"
    exit 0
fi

if [ -z "$sdk_key" ]; then
    echo "sdk_key is empty！"
    exit 0
fi

if [ -z "$app_name" ]; then
    echo "app_name is empty！"
    exit 0
fi

xcrun instruments -w "iPhone 11 (13.1)"
last_bundle_id=$(head -n +1 last_request_bundle_id)
if [ -n "$last_bundle_id" ]; then
    xcrun simctl uninstall booted $last_bundle_id
fi

echo "$app_identifier" > last_request_bundle_id

/usr/libexec/PlistBuddy -c "Set:CFBundleIdentifier  $app_identifier" ${app_path}/Info.plist
/usr/libexec/PlistBuddy -c "Set:CallbackUrl  $call_back_url" ${app_path}/Info.plist
/usr/libexec/PlistBuddy -c "Set:AppLovinSdkKey  $sdk_key" ${app_path}/Info.plist
/usr/libexec/PlistBuddy -c "Set:CFBundleDisplayName  $app_name" ${app_path}/Info.plist
/usr/libexec/PlistBuddy -c "Set:CFBundleName  $app_name" ${app_path}/Info.plist

echo "安装并启动应用"
xcrun simctl install booted ${app_path}
xcrun simctl launch booted ${app_identifier}

sleep 5
if [ $shutdown_sim = 'Y' ];then
    i=10
    while ((i > 0))
    do
        sleep 1
        echo "${i}秒后关闭模拟器"
        ((i--))
    done
    echo "模拟器关闭"
    xcrun simctl shutdown all
fi

