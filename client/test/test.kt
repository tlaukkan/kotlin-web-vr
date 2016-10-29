

@native("QUnit")
val qunit: dynamic = noImpl

val assert = qunit.assert

fun main(args: Array<String>) {

    qunit.test( "hello test")  { assert ->
        val t: Any = "1"
        assert.ok("1" == t, "Passed!")
    }

    jsonTests()

}