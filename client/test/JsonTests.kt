import util.fromJson
import util.toJson
import java.util.*

class Pair(val key: String, val value: String)

class Test(val pairs: Array<Pair> = arrayOf(), val obj: Pair? = null) {
    fun print() {
        println(this)
    }
}

fun <V> PrimitiveHashMap(container: dynamic): HashMap<String, Any> {
    val m = HashMap<String, Any>().asDynamic()
    m.map = container
    val keys = js("Object.keys")
    m.`$size` = keys(container).length
    return m
}

/*
fun deepCopy(source: Any, target: Any) {
    val sourceMap = PrimitiveHashMap<Any>(source)
    val targetMap = PrimitiveHashMap<Any>(target)

    println("Target value type: ${target.jsClass.name}")

    for (key in sourceMap.keys) {
        val sourceValue = sourceMap[key]
        val sourceValueString = JSON.stringify(sourceValue!!)
        val sourceValueType = sourceValue.jsClass.name

        println("$key=$sourceValueString ($sourceValueType)")

        if (!sourceValueType.equals("String")) {
            val targetValue = targetMap[key]
            if (sourceValue != null && sourceValue != undefined) {
                if (targetValue != null) {
                    deepCopy(sourceValue, targetValue)
                } else {
                    println("Target value null.")
                }
            }
        } else {
            if (sourceValue != null && sourceValue != undefined) {
                targetMap[key] = sourceValue!!
            }
        }
    }
}
*/

fun jsonTests() {
    qunit.test( "json parser test")  {
        val test: Test = fromJson("{\"pairs\": [{\"key\":\"ka\", \"value\":\"va\"}, {\"key\":\"kb\", \"value\":\"vb\"}, {\"key\":\"kc\", \"value\":\"vc\"}], \"obj\": {\"key\":\"kd\", \"value\":\"vd\"}}")
        assert.equal(test.pairs[0].key, "ka")
        assert.equal(test.pairs[0].value, "va")
        assert.equal(test.pairs[1].key, "kb")
        assert.equal(test.pairs[1].value, "vb")
        assert.equal(test.pairs[2].key, "kc")
        assert.equal(test.pairs[2].value, "vc")
        assert.equal(test.obj!!.key, "kd")
        assert.equal(test.obj!!.value, "vd")

        val testJson = toJson(test)
        val test2: Test = fromJson(testJson)
        assert.equal(test2.pairs[0].key, "ka")
        assert.equal(test2.pairs[0].value, "va")
        assert.equal(test2.pairs[1].key, "kb")
        assert.equal(test2.pairs[1].value, "vb")
        assert.equal(test2.pairs[2].key, "kc")
        assert.equal(test2.pairs[2].value, "vc")
        assert.equal(test2.obj!!.key, "kd")
        assert.equal(test2.obj!!.value, "vd")

        //println(test.display())

        /*val testCopy: Test = Test()
        deepCopy(test, testCopy)

        assert.equal(testCopy.pairs[0].key, "ka")
        assert.equal(testCopy.pairs[0].value, "va")
        assert.equal(testCopy.pairs[1].key, "kb")
        assert.equal(testCopy.pairs[1].value, "vb")
        assert.equal(testCopy.pairs[2].key, "kc")
        assert.equal(testCopy.pairs[2].value, "vc")

        assert.equal(JSON.stringify(test), JSON.stringify(testCopy))

        testCopy.display()*/

        /*val testCopy: Test = Test(arrayOf())
        val map = PrimitiveHashMap<Any>(test)
        for (property in map.keys) {
            println(property + "=" + map[property])
        }*/
    }
}