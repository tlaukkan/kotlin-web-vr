package webvrapi

import org.khronos.webgl.ArrayBuffer

@native
class Float32Array {
    var BYTES_PER_ELEMENT: Number = noImpl
    var buffer: ArrayBuffer = noImpl
    var byteLength: Number = noImpl
    var byteOffset: Number = noImpl

    @native
    open fun copyWithin(target: Number, start: Number, end: Number? = null): Float32Array = noImpl

    @native
    open fun every(callbackfn: (value: Number, index: Number, array: Float32Array) -> Boolean, thisArg: Any? = null): Boolean = noImpl

    @native
    open fun fill(value: Number, start: Number? = null, end: Number? = null): Float32Array = noImpl

    @native
    open fun filter(callbackfn: (value: Number, index: Number, array: Float32Array) -> Boolean, thisArg: Any? = null): Float32Array = noImpl

    @native
    open fun find(predicate: (value: Number, index: Number, obj: Array<Number>) -> Boolean, thisArg: Any? = null): Number = noImpl

    @native
    open fun findIndex(predicate: (value: Number) -> Boolean, thisArg: Any? = null): Number = noImpl

    @native
    open fun forEach(callbackfn: (value: Number, index: Number, array: Float32Array) -> Unit, thisArg: Any? = null): Unit = noImpl

    @native
    open fun indexOf(searchElement: Number, fromIndex: Number? = null): Number = noImpl

    @native
    open fun join(separator: String? = null): String = noImpl

    @native
    open fun lastIndexOf(searchElement: Number, fromIndex: Number? = null): Number = noImpl

    @native
    var Float32Array.length: Number get() = noImpl

    @native
    open fun map(callbackfn: (value: Number, index: Number, array: Float32Array) -> Number, thisArg: Any? = null): Float32Array = noImpl

    @native
    open fun reduce(callbackfn: (previousValue: Number, currentValue: Number, currentIndex: Number, array: Float32Array) -> Number, initialValue: Number? = null): Number = noImpl

    @native
    fun <U> Float32Array.reduce(callbackfn: (previousValue: U, currentValue: Number, currentIndex: Number, array: Float32Array) -> U, initialValue: U): U = noImpl

    @native
    open fun reduceRight(callbackfn: (previousValue: Number, currentValue: Number, currentIndex: Number, array: Float32Array) -> Number, initialValue: Number? = null): Number = noImpl

    @native
    fun <U> Float32Array.reduceRight(callbackfn: (previousValue: U, currentValue: Number, currentIndex: Number, array: Float32Array) -> U, initialValue: U): U = noImpl

    @native
    open fun reverse(): Float32Array = noImpl

    //@native
    //open fun set(index: Number, value: Number): Unit = noImpl

    //@native
    //open fun set(array: ArrayLike<Number>, offset: Number? = null): Unit = noImpl

    @native
    open fun slice(start: Number? = null, end: Number? = null): Float32Array = noImpl

    @native
    open fun some(callbackfn: (value: Number, index: Number, array: Float32Array) -> Boolean, thisArg: Any? = null): Boolean = noImpl

    @native
    open fun sort(compareFn: ((a: Number, b: Number) -> Number)? = null): Float32Array = noImpl

    @native
    open fun subarray(begin: Number, end: Number? = null): Float32Array = noImpl

    @native
    open fun toLocaleString(): String = noImpl

    @native
    open fun get(index: Number): Number? = noImpl

    @native
    open fun set(index: Number, value: Number): Unit = noImpl

}