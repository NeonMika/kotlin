fun <T : CharSequence> foo(x: Array<Any>, block: (T, Int) -> Int) {
    var r: Any?

    @Suppress("UNCHECKED_CAST") r = block(x[0] as T, "" <!CAST_NEVER_SUCCEEDS!>as<!> Int)

    // to prevent unused assignment diagnostic for the above statement
    r.hashCode()

    var i = 1

    if (i != 1) {
        @Suppress("UNCHECKED_CAST") i += block(x[0] as T, "" <!CAST_NEVER_SUCCEEDS!>as<!> Int).<!REDUNDANT_CALL_OF_CONVERSION_METHOD!>toInt()<!>
    }

    if (i != 1) @Suppress("UNCHECKED_CAST")
    i += block(x[0] as T, "" <!CAST_NEVER_SUCCEEDS!>as<!> Int).<!REDUNDANT_CALL_OF_CONVERSION_METHOD!>toInt()<!>

    if (i != 1) @Suppress("UNCHECKED_CAST") i += block(x[0] as T, "" <!CAST_NEVER_SUCCEEDS!>as<!> Int).<!REDUNDANT_CALL_OF_CONVERSION_METHOD!>toInt()<!>
}
