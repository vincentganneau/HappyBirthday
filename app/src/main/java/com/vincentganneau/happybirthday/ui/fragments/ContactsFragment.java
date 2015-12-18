package com.vincentganneau.happybirthday.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vincentganneau.happybirthday.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * {@link ListFragment} that displays contacts whose birthday is today.
 *
 * @author Vincent Ganneau
 *
 * Copyright (C) 2015
 */
public class ContactsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Adapter
    private SimpleCursorAdapter mListAdapter;

    // Loaders
    private static final int CONTACTS_LOADER = 0;

    // Permissions
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[] { Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME }, new int[] { android.R.id.text1 }, 0);
        setListAdapter(mListAdapter);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            requestPermissions(new String[] { Manifest.permission.READ_CONTACTS }, REQUEST_READ_CONTACTS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CONTACTS_LOADER) {
            return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CONTACTS_LOADER) {
            final MatrixCursor contacts = new MatrixCursor(new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME });
            if (data.moveToFirst()) {
                final int columnIndexId = data.getColumnIndex(ContactsContract.Contacts._ID);
                final int columnIndexName = data.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                final GregorianCalendar today = new GregorianCalendar();
                while (!data.isAfterLast()) {
                    final String id = data.getString(columnIndexId);
                    final Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[] { ContactsContract.CommonDataKinds.Event.DATA }, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Contacts.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Event.TYPE + " = ?", new String[] { id, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, Integer.toString(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) }, ContactsContract.Data.DISPLAY_NAME);
                    if (cursor != null && cursor.moveToFirst()) {
                        final String birthday = cursor.getString(0);
                        try {
                            final Date date = format.parse(birthday);
                            final GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            if (calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                                contacts.addRow(new String[] { id, data.getString(columnIndexName) });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } finally {
                            cursor.close();
                        }
                    }
                    data.moveToNext();
                }
            }
            mListAdapter.swapCursor(contacts);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CONTACTS_LOADER) {
            mListAdapter.swapCursor(null);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListAdapter.getCursor().moveToPosition(position);
        final String name = mListAdapter.getCursor().getString(mListAdapter.getCursor().getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.happy_birthday, name));
        startActivity(Intent.createChooser(intent, getString(R.string.happy_birthday_action_chooser_title)));
    }

    // Helper methods
    private void loadContacts() {
        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }
}
