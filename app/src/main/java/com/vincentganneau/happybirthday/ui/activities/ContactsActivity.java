package com.vincentganneau.happybirthday.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.vincentganneau.happybirthday.R;

/**
 * {@link AppCompatActivity} that displays contacts whose birthday is today.
 *
 * @author Vincent Ganneau
 *
 * Copyright (C) 2015
 */
public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }
}
