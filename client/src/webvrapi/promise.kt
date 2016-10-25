package webvrapi

@native
interface Promise<T> : Ipromise<T> {
}

@native
interface Ipromise<T> {
    var resolve: (value: T) -> IThenable<T>
    var reject: (value: T) -> IThenable<T>
    var all: (array: Array<IThenable<Any>>) -> IThenable<Array<Any>>
    var denodeify: (fn: Function<T>) -> (args: Array<Any>) -> IThenable<Any>
    var nodeify: (fn: Function<T>) -> Function<T>
}

@native
interface IThenable<T> {
    fun <R> then(onFulfilled: ((value: T) -> dynamic /* IThenable<R> | R */)? = null, onRejected: ((error: Any) -> dynamic /* IThenable<R> | R */)? = null): IThenable<R>
    fun <R> catch(onRejected: ((error: Any) -> dynamic /* IThenable<R> | R */)? = null): IThenable<R>
    fun <R> done(onFulfilled: ((value: T) -> dynamic /* IThenable<R> | R */)? = null, onRejected: ((error: Any) -> dynamic /* IThenable<R> | R */)? = null): IThenable<R>
    fun <R> nodeify(callback: Function<R>): IThenable<R>
}