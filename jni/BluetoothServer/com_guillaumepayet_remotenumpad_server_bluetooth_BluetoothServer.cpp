#include "bluetoothserver.h"

#include <QDebug>


static BluetoothServer *server = nullptr;


void Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_init
    (JNIEnv *env, jobject)
{
    server = new BluetoothServer(env);
}

void Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_addListener
    (JNIEnv *, jobject, jobject listener)
{
    server->addListener(listener);
}

void Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_removeListener
    (JNIEnv *, jobject, jobject listener)
{
    server->removeListener(listener);
}

void Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_open
    (JNIEnv *, jobject)
{
    server->open();
}

void Java_com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer_close
    (JNIEnv *, jobject)
{
    server->close();
}
