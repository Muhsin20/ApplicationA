package com.example.applicationa;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class AlbumProvider extends ContentProvider {

    //define the authority so other applications can access
    static final String PROVIDER_NAME = "com.example.applicationa.provider";
    static final String PATH = "albums";

    //defining the uri
    static final String URL = "content://" + PROVIDER_NAME + "/"+ PATH;

    static final Uri CONTENT_URI = Uri.parse(URL);

    static final UriMatcher uriMatcher;

    private static final int ALBUMS = 1;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //to access whole table
        uriMatcher.addURI(PROVIDER_NAME, "albums", ALBUMS);



    }

    //create a writeable db if it already doesnt exist.
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        return true;


    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = dbHelper.getWritableDatabase();
        long row_id = db.insert(dbHelper.ALBUMS_TABLE_NAME,"",values);

        //if insertion successful, notify changes and return the uri of newly inserted row else throw an error
        if (row_id>0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, row_id);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }

    //function that queries the database for album records
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        db = dbHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);

        //if the uri matches  albums (meaning the content uri) return all the rows else throw and exception
        if (match == ALBUMS){
            return db.query(dbHelper.ALBUMS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    //function that deletes albums records
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int count = 0;
        int match = uriMatcher.match(uri);

        //if the uri matches album delete the specified row and return the # of deleted rows
        if (match==ALBUMS) {
            count = db.delete(dbHelper.ALBUMS_TABLE_NAME, selection, selectionArgs);


            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
        else{
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    //function that updates an album record
    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        //if the uri matches album delete the specified row and return the # of updated rows
        if (match==ALBUMS) {
            count = db.update(dbHelper.ALBUMS_TABLE_NAME, values, selection, selectionArgs);


            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
        else{
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    //function to get MIME type of URI
    @Override
    public String getType(Uri uri) {
        int match= uriMatcher.match(uri);
        if (match==ALBUMS){
            return "vnd.android.cursor.dir/vnd.example.albums";
        }
        else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }
}





