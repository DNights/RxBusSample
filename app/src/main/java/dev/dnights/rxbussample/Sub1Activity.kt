package dev.dnights.rxbussample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import dev.dnights.rxbussample.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_sub1.*

class Sub1Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub1)

        bt_next.setOnClickListener {
            startActivity(Intent(this , Sub2Activity::class.java))
        }

        RxBus.instance.receiveEvent("main").subscribe({
            Log.d("test", "sub1 = $it")
            tv_main.text = it.toString()
        },{
            it.printStackTrace()
        }).let {
            compositeDisposable.add(it)
        }

        RxBus.instance.receiveEvent("sub1").subscribe({
            Log.d("test", "sub1 = $it")
            tv_sub1.text = it.toString()
        },{
            it.printStackTrace()
        }).let {
            compositeDisposable.add(it)
        }

        RxBus.instance.receiveEvent("sub2").subscribe({
            Log.d("test", "sub2 = $it")
            tv_sub2.text = it.toString()
        },{
            it.printStackTrace()
        }).let {
            compositeDisposable.add(it)
        }

        bt_send_sub1.setOnClickListener {
            RxBus.instance.sendEvent("send event sub1", "sub1")
        }

    }
}