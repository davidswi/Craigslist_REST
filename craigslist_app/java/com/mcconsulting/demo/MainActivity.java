package com.mcconsulting.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mcconsulting.demo.Contact;
import com.mcconsulting.demo.Address;
import com.mcconsulting.demo.PhoneNumber;

import com.mcconsulting.network.MCService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    public MCService mMCService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMCService = ((DemoApplication)getApplication()).getMCService();
    }
}
