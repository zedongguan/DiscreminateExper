//
// Created by Administrator on 2017/3/10.
//
#include "robot_tangwa_discreminateexper_JniTest.h"

JNIEXPORT jstring JNICALL Java_robot_tangwa_discreminateexper_JniTest_hello_1jni
        (JNIEnv * env, jclass jclass){

    return  env->NewStringUTF("gaunzedong");


}

