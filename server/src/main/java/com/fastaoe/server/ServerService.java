package com.fastaoe.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
    private CopyOnWriteArrayList<IAddBookListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        list.add(new Book(1, "岛上书店"));
        list.add(new Book(2, "白夜行"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        @Override
        public void unregisterListener(IAddBookListener listener) throws RemoteException {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    };

    private void onNewBookAdd(Book book) throws RemoteException {
        list.add(book);
        for (IAddBookListener listener : listeners) {
            listener.OnNewBookAddListener(book);
        }
    }
}
