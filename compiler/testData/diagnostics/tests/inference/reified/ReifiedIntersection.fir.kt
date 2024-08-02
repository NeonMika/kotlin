// ISSUE: KT-52469

interface A
interface B

inline fun <reified T> reifiedType(value: T) {}

fun <T> testTypeParamter(value: T) {
    when (value) {
        is A -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        is B -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        is String -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        is Int -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        is List<*> -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        else -> <!TYPE_PARAMETER_AS_REIFIED!>reifiedType<!>(value)
    }
}

fun testInterfaces(value: A) {
    when (value) {
        is B -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        <!USELESS_IS_CHECK!>is String<!> -> <!INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION!>reifiedType<!>(value)
        <!USELESS_IS_CHECK!>is Int<!> -> <!INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION!>reifiedType<!>(value)
        is List<*> -> <!TYPE_INTERSECTION_AS_REIFIED!>reifiedType<!>(value)
        else -> reifiedType(value)
    }
}
