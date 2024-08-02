// ISSUE: KT-52469

interface A
interface B

inline fun <reified T> reifiedType(value: T) {}

fun <T> testTypeParamter(value: T) {
    when (value) {
        is A -> reifiedType(<!DEBUG_INFO_SMARTCAST!>value<!>)
        is B -> reifiedType(<!DEBUG_INFO_SMARTCAST!>value<!>)
        is String -> reifiedType(<!DEBUG_INFO_SMARTCAST!>value<!>)
        is Int -> reifiedType(<!DEBUG_INFO_SMARTCAST!>value<!>)
        is List<*> -> reifiedType(<!DEBUG_INFO_SMARTCAST!>value<!>)
        else -> <!TYPE_PARAMETER_AS_REIFIED!>reifiedType<!>(value)
    }
}

fun testInterfaces(value: A) {
    when (value) {
        is B -> reifiedType(value)
        is <!INCOMPATIBLE_TYPES!>String<!> -> <!INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION!>reifiedType<!>(value)
        is <!INCOMPATIBLE_TYPES!>Int<!> -> <!INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION!>reifiedType<!>(value)
        is List<*> -> reifiedType(value)
        else -> reifiedType(value)
    }
}
