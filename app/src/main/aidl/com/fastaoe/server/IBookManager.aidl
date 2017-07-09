// IBookManager.aidl
package com.fastaoe.server;

// Declare any non-default types here with import statements

import com.fastaoe.server.Book;
import com.fastaoe.server.IAddBookListener;

interface IBookManager {
    List<Book> getListBook();
    void addBook(in Book book);
    void registerListener(IAddBookListener listener);
    void unregisterListener(IAddBookListener listener);
}
