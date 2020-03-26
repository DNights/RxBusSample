package dev.dnights.rxbussample.rxbus

import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import java.util.*


object RxBus {
    val instance = RxBus
    private val subjectTable = Hashtable<String, PublishSubject<Any>>()
    private var autoDisposableList = arrayListOf<AutoDisposable>()

    fun sendEvent(any: Any, key: String = "RxBus") {
        subjectTable[key]?.onNext(any)
    }

    fun receiveEvent(key: String = "RxBus"): PublishSubject<Any> {
        synchronized(this) {
            if (subjectTable.containsKey(key).not()) {
                subjectTable[key] = PublishSubject.create()
            }
            return subjectTable[key]!!
        }
    }

    fun register(key: String = "RxBus", lifecycle: Lifecycle?, onNext: Consumer<in Any>) {
        register(key, lifecycle, onNext, Lifecycle.Event.ON_DESTROY)
    }

    fun register(key: String = "RxBus", lifecycle: Lifecycle?, onNext: Consumer<in Any>, lifeCycleEventForDispose: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
        synchronized(this) {
            if (subjectTable.containsKey(key).not()) {
                subjectTable[key] = PublishSubject.create()
            }
            autoDisposableList.add(AutoDisposable(key, null, lifecycle, subjectTable[key]!!.subscribe(onNext), lifeCycleEventForDispose))
        }
    }

    fun register(key: String = "RxBus", view: View?, onNext: Consumer<in Any>) {
        synchronized(this) {
            if (subjectTable.containsKey(key).not()) {
                subjectTable[key] = PublishSubject.create()
            }
            val disposable = subjectTable[key]!!.subscribe(onNext)
            view?.addOnAttachStateChangeListener(ViewStateListener(key, disposable))
            autoDisposableList.add(AutoDisposable(key, view, null, disposable, null))
        }
    }

    private fun notifyDataChange(key: String) {
        autoDisposableList.removeAll {
            it.disposable.isDisposed
        }
        val isAllDisposeByKey = autoDisposableList.none {
            it.key == key
        }

        if (isAllDisposeByKey) {
            subjectTable.remove(key)
        }
    }

    class ViewStateListener(val key: String, val disposable: Disposable) : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {
        }

        override fun onViewDetachedFromWindow(view: View) {
            disposable.dispose()
            notifyDataChange(key)
            view.removeOnAttachStateChangeListener(this)
        }
    }

    class AutoDisposable(val key: String, val view: View?, private val lifecycle: Lifecycle?, val disposable: Disposable, private val disposeLifecycleEvent: Lifecycle.Event?) : LifecycleObserver {
        init {
            lifecycle?.addObserver(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            if (Lifecycle.Event.ON_STOP == disposeLifecycleEvent) {
                disposeWithLifecycle()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            if (Lifecycle.Event.ON_PAUSE == disposeLifecycleEvent) {
                disposeWithLifecycle()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            if (Lifecycle.Event.ON_DESTROY == disposeLifecycleEvent) {
                disposeWithLifecycle()
            }
        }

        private fun disposeWithLifecycle() {
            disposable.dispose()
            lifecycle?.removeObserver(this)
            notifyDataChange(key)
        }
    }
}