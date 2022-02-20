package com.air.wuyunliuqi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.air.wuyunliuqi.model.PielItem
import com.air.wuyunliuqi.view.WheelView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val first = findViewById<WheelView>(R.id.first)
        val second = findViewById<WheelView>(R.id.second)
        val firstData = listOf(
            PielItem("初"), PielItem("二"), PielItem("三"),
            PielItem("四"), PielItem("五"), PielItem("终")
        )
        first.setData(firstData)
        val secondData = listOf(
            PielItem("少阴", "SE"), PielItem("少阳", "S"), PielItem("太阴", "SW"),
            PielItem("阳明", "E"), PielItem("太阳", "N"), PielItem("厥阴", "NE")
        )
        second.setData(secondData)
    }
}