#!/bin/bash

#echo "Shell 传递参数实例！"
#echo "执行的文件名：$0"
echo "fist : $1"
echo "第二个参数：$2"
echo "第三个参数：$3"


LOCAL_PATH='/Users/zhangyongxue/dev/android/otherProjects/Android-SDK-Demo-master/MyTestA/local.properties'

sed -i '' "s#^key_applicationid=.*#key_applicationid=${1}#g" $LOCAL_PATH
sed -i '' "s#^key_oemappname=.*#key_oemappname=${2}#g" $LOCAL_PATH
sed -i '' "s#^key_targeturl=.*#key_targeturl=${3}#g" $LOCAL_PATH
sed -i '' "s#^key_applovin=.*#key_applovin=${4}#g" $LOCAL_PATH
