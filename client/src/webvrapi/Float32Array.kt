package webvrapi

import org.khronos.webgl.ArrayBuffer

@native
class Float32Array {
    @native
    var Float32Array.BYTES_PER_ELEMENT: Number get() = noImpl
    @native
    var Float32Array.buffer: ArrayBuffer get() = noImpl
    @native
    var Float32Array.byteLength: Number get() = noImpl
    @native
    var Float32Array.byteOffset: Number get() = noImpl

    @native
    fun Float32Array.copyWithin(target: Number, start: Number, end: Number? = null): Float32Array = noImpl

    @native
    fun Float32Array.every(callbackfn: (value: Number, index: Number, array: Float32Array) -> Boolean, thisArg: Any? = null): Boolean = noImpl

    @native
    fun Float32Array.fill(value: Number, start: Number? = null, end: Number? = null): Float32Array = noImpl

    @native
    fun Float32Array.filter(callbackfn: (value: Number, index: Number, array: Float32Array) -> Boolean, thisArg: Any? = null): Float32Array = noImpl

    @native
    fun Float32Array.find(predicate: (value: Number, index: Number, obj: Array<Number>) -> Boolean, thisArg: Any? = null): Number = noImpl

    @native
    fun Float32Array.findIndex(predicate: (value: Number) -> Boolean, thisArg: Any? = null): Number = noImpl

    @native
    fun Float32Array.forEach(callbackfn: (value: Number, index: Number, array: Float32Array) -> Unit, thisArg: Any? = null): Unit = noImpl

    @native
    fun Float32Array.indexOf(searchElement: Number, fromIndex: Number? = null): Number = noImpl

    @native
    fun Float32Array.join(separator: String? = null): String = noImpl

    @native
    fun Float32Array.lastIndexOf(searchElement: Number, fromIndex: Number? = null): Number = noImpl

    @native
    var Float32Array.length: Number get() = noImpl

    @native
    fun Float32Array.map(callbackfn: (value: Number, index: Number, array: Float32Array) -> Number, thisArg: Any? = null): Float32Array = noImpl

    @native
    fun Float32Array.reduce(callbackfn: (previousValue: Number, currentValue: Number, currentIndex: Number, array: Float32Array) -> Number, initialValue: Number? = null): Number = noImpl

    @native
    fun <U> Float32Array.reduce(callbackfn: (previousValue: U, currentValue: Number, currentIndex: Number, array: Float32Array) -> U, initialValue: U): U = noImpl

    @native
    fun Float32Array.reduceRight(callbackfn: (previousValue: Number, currentValue: Number, currentIndex: Number, array: Float32Array) -> Number, initialValue: Number? = null): Number = noImpl

    @native
    fun <U> Float32Array.reduceRight(callbackfn: (previousValue: U, currentValue: Number, currentIndex: Number, array: Float32Array) -> U, initialValue: U): U = noImpl

    @native
    fun Float32Array.reverse(): Float32Array = noImpl

    //@native
    //fun Float32Array.set(index: Number, value: Number): Unit = noImpl

    //@native
    //fun Float32Array.set(array: ArrayLike<Number>, offset: Number? = null): Unit = noImpl

    @native
    fun Float32Array.slice(start: Number? = null, end: Number? = null): Float32Array = noImpl

    @native
    fun Float32Array.some(callbackfn: (value: Number, index: Number, array: Float32Array) -> Boolean, thisArg: Any? = null): Boolean = noImpl

    @native
    fun Float32Array.sort(compareFn: ((a: Number, b: Number) -> Number)? = null): Float32Array = noImpl

    @native
    fun Float32Array.subarray(begin: Number, end: Number? = null): Float32Array = noImpl

    @native
    fun Float32Array.toLocaleString(): String = noImpl

    @native
    fun Float32Array.toString(): String = noImpl

    @native
    fun Float32Array.get(index: Number): Number? = noImpl

    @native
    fun Float32Array.set(index: Number, value: Number): Unit = noImpl

}