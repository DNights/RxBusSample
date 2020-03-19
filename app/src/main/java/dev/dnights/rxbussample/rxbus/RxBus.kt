package dev.dnights.rxbussample.rxbus

import io.reactivex.subjects.PublishSubject

object RxBus {

    val instance = RxBus

    private var subject: PublishSubject<Any> = PublishSubject.create()

    fun sendEvent(any: Any) { subject.onNext(any) }

    fun receiveEvent() = subject
}