// IAddBookListener.aidl
package com.fastaoe.server;

// Declare any non-default types here with import statements
import com.fastaoe.server.Book;

interface IAddBookListener {
    void OnNewBookAddListener(in Book book);
}
