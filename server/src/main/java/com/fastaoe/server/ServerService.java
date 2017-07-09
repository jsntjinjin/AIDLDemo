package com.fastaoe.server;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jinjin on 2017/7/9.
 * description:
 */

public class ServerService extends Service {

    private CopyOnWriteArrayList<Book> list = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IAddBookListener> listeners = new RemoteCallbackList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        list.add(new Book(1, "岛上书店"));
        list.add(new Book(2, "白夜行"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.fastaoe.server.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return binder;
    }

    private IBinder binder = new IBookManager.Stub(){

        @Override
        public List<Book> getListBook() throws RemoteException {
            return list;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            onNewBookAdd(book);
        }

        @Override
        public void registerListener(IAddBookListener listener) throws RemoteException {
            listeners.register(listener);
        }

        @Override
        public void unregisterListener(IAddBookListener listener) throws RemoteException {
            listeners.unregister(listener);
        }
    };

    private void onNewBookAdd(Book book) throws RemoteException {
        list.add(book);
        int N = listeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IAddBookListener broadcastItem = listeners.getBroadcastItem(i);
            broadcastItem.OnNewBookAddListener(book);
        }
        listeners.finishBroadcast();
    }
}
