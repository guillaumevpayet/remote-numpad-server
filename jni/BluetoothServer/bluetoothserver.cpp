#include "bluetoothserver.h"

static const QBluetoothUuid NUMPAD_UUID(QString("6be5ccef-5d32-48e3-a3a0-d89e558a40f1"));

BluetoothServer::BluetoothServer(JNIEnv *env) : QObject(Q_NULLPTR), env(env), onStatusChange(Q_NULLPTR)
{
    jclass INumpadServerListener = env->FindClass("com/guillaumepayet/remotenumpad/server/INumpadServerListener");

    onStatusChange = env->GetMethodID(INumpadServerListener,
                                      "onStatusChange",
                                      "(Ljava/lang/String;)V");
}


void BluetoothServer::addListener(jobject listener)
{
    listeners.insert(listener);
}

void BluetoothServer::removeListener(jobject listener)
{
    listeners.erase(listener);
}

void BluetoothServer::open()
{
    changeStatus("Opened");
}

void BluetoothServer::close()
{
    changeStatus("Closed");
}


void BluetoothServer::changeStatus(const std::string &status)
{
    char *buffer = new char[status.length() + 1];
    strcpy(buffer, status.c_str());
//    jstring jStatus = env->NewStringUTF(buffer);

    for (jobject listener : listeners)
        qInfo() << buffer << "->" << listener;
//        env->CallVoidMethod(listener, onStatusChange, jStatus);

//    env->DeleteLocalRef(jStatus);
    delete[] buffer;
}
