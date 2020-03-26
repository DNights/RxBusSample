package dev.dnights.rxbussample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import dev.dnights.rxbussample.rxbus.RxBus
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_next.setOnClickListener {
            startActivity(Intent(this , Sub1Activity::class.java))
        }

        RxBus.instance.register("main",this.lifecycle, Consumer{
            Log.d("test", "main = $it")
            tv_main.text = it.toString()
        })

        RxBus.instance.register("sub1",this.lifecycle, Consumer{
            Log.d("test", "sub1 = $it")
            tv_sub1.text = it.toString()
        })

        RxBus.instance.register("sub2",this.lifecycle, Consumer{
            Log.d("test", "sub2 = $it")
            tv_sub2.text = it.toString()
        })

        bt_send_main.setOnClickListener {
            RxBus.instance.sendEvent("send event main", "main")
        }

        //legacy
//        RxBus.instance.receiveEvent("main").subscribe({
//            Log.d("test", "sub1 = $it")
//            tv_main.text = it.toString()
//        },{
//            it.printStackTrace()
//        }).let {
//            compositeDisposable.add(it)
//        }
//
//        RxBus.instance.receiveEvent("sub1").subscribe({
//            Log.d("test", "sub1 = $it")
//            tv_sub1.text = it.toString()
//        },{
//            it.printStackTrace()
//        }).let {
//            compositeDisposable.add(it)
//        }
//
//        RxBus.instance.receiveEvent("sub2").subscribe({
//            Log.d("test", "sub2 = $it")
//            tv_sub2.text = it.toString()
//        },{
//            it.printStackTrace()
//        }).let {
//            compositeDisposable.add(it)
//        }
//

    }
}
