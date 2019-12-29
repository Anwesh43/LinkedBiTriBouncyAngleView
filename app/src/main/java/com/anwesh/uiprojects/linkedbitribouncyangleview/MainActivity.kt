package com.anwesh.uiprojects.linkedbitribouncyangleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.bitribouncyangleview.BiTriBouncyAngleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BiTriBouncyAngleView.create(this)
    }
}
