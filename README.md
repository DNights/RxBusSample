# RxBusSample

**KO** RxJava2의 `PublishSubject`로 만든 간단한 EventBus(RxBus) 샘플입니다.
**EN** A small EventBus-style helper (RxBus) built on RxJava2's `PublishSubject`.

> 📖 **Blog / 블로그** · [RxJava2로 EventBus 구현 하기 (Rxbus)](https://dnight.tistory.com/entry/RxJava2%EB%A1%9C-EventBus-%EA%B5%AC%ED%98%84-%ED%95%98%EA%B8%B0-Rxbus)
> **KO** 만든 배경과 설명은 블로그 글에서 볼 수 있습니다.
> **EN** The background and walkthrough are in the blog post.

---

## Why / 만든 이유

**KO** EventBus를 쓰던 중 Dexguard 적용 시 문제가 있어서, Rx를 이용해 EventBus처럼 쓸 수 있도록 바꿨습니다.
**EN** While using EventBus, we ran into problems with Dexguard, so I replaced it with an RxJava-based implementation that works the same way.

---

## Code / 코드

```kotlin
import io.reactivex.subjects.PublishSubject
import java.util.*

object RxBus {

    val instance = RxBus
    private val subjectTable = Hashtable<String, PublishSubject<Any>>()

    fun sendEvent(any: Any, key: String = "RxBus") { subjectTable[key]?.onNext(any) }

    fun receiveEvent(key: String = "RxBus"): PublishSubject<Any> {
        synchronized(this) {
            if (subjectTable.containsKey(key).not()) {
                subjectTable[key] = PublishSubject.create()
            }
            return subjectTable[key]!!
        }
    }
}
```

**KO** 키(key)별로 `PublishSubject`를 하나씩 두고, 같은 키로 보내고 받습니다.
**EN** One `PublishSubject` is kept per key; send and receive with the same key.

---

## Usage / 사용법

**Send / 보내기**

```kotlin
RxBus.instance.sendEvent("send event", "key1")
```

**Receive / 받기**

```kotlin
RxBus.instance.receiveEvent("key1").subscribe({
    Log.d("test", "key1 = $it")
}, {
    it.printStackTrace()
})
```

---

## Notes / 주의할 점

- **KO** `PublishSubject`라서 **구독하기 전에 보낸 이벤트는 받을 수 없습니다.** 이전 값을 받아야 하면 `BehaviorSubject`나 `ReplaySubject`가 필요합니다.
  **EN** It uses `PublishSubject`, so **events sent before subscribing are lost.** Use `BehaviorSubject` or `ReplaySubject` if you need earlier values.
  → **KO** Subject 종류별 차이는 [ReactiveX - Subject](https://dnight.tistory.com/entry/ReactiveX-Subject) 글을 참고하세요. **EN** See [ReactiveX - Subject](https://dnight.tistory.com/entry/ReactiveX-Subject) for the differences between Subject types.
- **KO** 구독은 직접 해제해야 합니다. 화면 생명주기에 맞춰 `Disposable`을 `dispose()` 하세요. (아래 예시)
  **EN** Unsubscribe yourself: `dispose()` the `Disposable` with your screen's lifecycle (example below).
- **KO** 받는 쪽이 아직 없을 때 `sendEvent`는 아무 일도 하지 않습니다.
  **EN** `sendEvent` is a no-op if nothing has subscribed to that key yet.

```kotlin
private val disposables = CompositeDisposable()

override fun onStart() {
    super.onStart()
    disposables.add(RxBus.instance.receiveEvent("key1").subscribe({ /* handle */ }, { it.printStackTrace() }))
}

override fun onStop() {
    disposables.clear()
    super.onStop()
}
```

> **KO** 블로그 원문에는 구독 해제 안내가 없지만, 이 레포의 샘플은 `BaseActivity`에서 `CompositeDisposable`에 모아 두었다가 `onDestroy()`에서 `clear()` 합니다.
> **EN** The blog post doesn't cover unsubscribing, but the sample in this repo collects subscriptions in a `CompositeDisposable` in `BaseActivity` and calls `clear()` in `onDestroy()`.

---

## Dependencies / 의존성

```groovy
implementation 'io.reactivex.rxjava2:rxjava:2.2.18'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
```

**KO** 샘플 앱: minSdk 16, targetSdk 29, Kotlin.
**EN** Sample app: minSdk 16, targetSdk 29, Kotlin.

---

## 📚 Related posts / 관련 글

- [ReactiveX - Subject](https://dnight.tistory.com/entry/ReactiveX-Subject) — **KO** Subject의 종류와 차이 · **EN** Types of Subjects and how they differ
- [SubscribeOn 와 ObserveOn 그리고 Schedulers](https://dnight.tistory.com/entry/SubscribeOn-%EC%99%80-ObserveOn-%EA%B7%B8%EB%A6%AC%EA%B3%A0-Schedulers) — **KO** 스레드 전환 · **EN** Switching threads
- [Rx 카테고리 전체 / All Rx posts](https://dnight.tistory.com/category/Android/Rx)
