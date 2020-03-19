package dev.dnights.rxbussample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dev.dnights.rxbussample.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        RxBus.instance.receiveEvent("1").subscribe({
//            Log.d("test", "it = $it")
//            tv_main.text = it.toString()
//        },{
//            it.printStackTrace()
//        })
//
//        RxBus.instance.receiveEvent("2").subscribe({
//            Log.d("test", "it = $it")
//            tv_main.text = it.toString()
//        },{
//            it.printStackTrace()
//        })
//
//
//        RxBus.instance.sendEvent("123456", "1")
//        RxBus.instance.sendEvent("asdfdfasfsaf", "99")
    }
}
