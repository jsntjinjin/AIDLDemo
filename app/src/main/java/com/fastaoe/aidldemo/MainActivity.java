package com.fastaoe.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fastaoe.server.Book;
import com.fastaoe.server.IAddBookListener;
import com.fastaoe.server.IBookManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Book> list = new ArrayList<>();

    private int index = 3;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list.add((Book) msg.obj);
                    tv_book.setText(list.toString());
                    break;

            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManager.Stub.asInterface(service);
            try {
                iBookManager.registerListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBookManager = null;
        }
    };

    private IBookManager iBookManager;
    private IAddBookListener listener = new IAddBookListener.Stub() {
        @Override
        public void OnNewBookAddListener(Book book) throws RemoteException {
            handler.obtainMessage(1, book).sendToTarget();
        }
    };
    private Button btn_search;
    private Button btn_add;
    private TextView tv_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_search = (Button) findViewById(R.id.btn_search);
        btn_add = (Button) findViewById(R.id.btn_add);
        tv_book = (TextView) findViewById(R.id.tv_book);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iBookManager != null) {
                    try {
                        list = iBookManager.getListBook();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    tv_book.setText(list.toString());
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iBookManager != null) {
                    Book book = new Book(index, "图书"+ index);
                    index += 1;
                    try {
                        iBookManager.addBook(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Intent intent = new Intent();
        intent.setAction("com.fastaoe.server.ServerService");
        intent.setComponent(new ComponentName("com.fastaoe.server", "com.fastaoe.server.ServerService"));
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        try {
            iBookManager.unregisterListener(listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(conn);
        super.onDestroy();
    }
}
