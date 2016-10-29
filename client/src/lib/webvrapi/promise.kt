package lib.webvrapi

@native
interface Promise<T> {
    fun then(onFulfilled: ((value: T) -> dynamic /* IThenable<T> | T */)? = null, onTejected: ((error: Any) -> dynamic /* IThenable<T> | T */)? = null): Promise<T>
    fun catch(onTejected: ((error: Any) -> dynamic /* IThenable<T> | T */)? = null): Promise<T>
    fun done(onFulfilled: ((value: T) -> dynamic /* IThenable<T> | T */)? = null, onTejected: ((error: Any) -> dynamic /* IThenable<T> | T */)? = null): Promise<T>
    fun nodeify(callback: Function<T>): Promise<T>
}