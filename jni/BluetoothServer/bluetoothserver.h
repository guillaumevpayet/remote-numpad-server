#ifndef BLUETOOTHSERVER_H
#define BLUETOOTHSERVER_H

#include "com_guillaumepayet_remotenumpad_server_bluetooth_BluetoothServer.h"

#include <QBluetoothServer>

#include <set>

class BluetoothServer : public QObject
{
    Q_OBJECT
public:
    explicit BluetoothServer(JNIEnv *env);

signals:

public:
    void addListener(jobject listener);
    void removeListener(jobject listener);
    void open();
    void close();

private slots:

private:
    void changeStatus(const std::string &status);

private:
    JNIEnv *env;
    jmethodID onStatusChange;
    std::set<jobject> listeners;
};

#endif // BLUETOOTHSERVER_H
