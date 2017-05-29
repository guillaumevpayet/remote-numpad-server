#include "com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer.h"

#include <QDebug>


JNIEXPORT void JNICALL Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_addListener
  (JNIEnv *env, jobject obj, jobject listener)
{
    qInfo() << "Adding listener";
}

JNIEXPORT void JNICALL Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_removeListener
  (JNIEnv *env, jobject obj, jobject listener)
{
    qInfo() << "Removing listener";
}

JNIEXPORT void JNICALL Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_open
  (JNIEnv *env, jobject obj)
{
    qInfo() << "Opening server";
}

JNIEXPORT void JNICALL Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_close
  (JNIEnv *env, jobject obj)
{
    qInfo() << "Closing server";
}
