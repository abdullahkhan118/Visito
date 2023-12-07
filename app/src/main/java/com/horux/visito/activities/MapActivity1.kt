package com.horux.visito.activities

import android.os.Bundle
import com.horux.visito.R
import com.horux.visito.fragments.MapsFragment

class MapActivity : PermissionActivity() {
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.mapFragment, MapsFragment::class.java, getIntent().getExtras())
            .commit()
    }
}