// WITH_EXTENDED_CHECKERS

import kotlin.contracts.*

fun branch(r: Result<Int>): Int =
    when {
        r.isSuccess -> r.result
        else -> 0
    }

fun branchThrow(r: Result<String>): Throwable =
    when {
        r.isSuccess -> Exception(r.result)
        else -> r.exception
    }

fun branchThrowReverse(r: Result<String>): Throwable =
    when {
        r.isFailure -> r.exception
        else -> Exception(r.result)
    }

fun incorrectNoCheck(r: Result<Int>): Int =
    <!UNSAFE_RESULT!>r.result<!>

@OptIn(ExperimentalContracts::class)
fun correct(r: Result<Int>): Boolean {
    contract {
        returns(true) implies r.isSuccess
    }
    return if (r.isSuccess) true else false
}

@OptIn(ExperimentalContracts::class)
fun incorrect(r: Result<Int>): Boolean {
    contract {
        <!WRONG_IMPLIES_CONDITION!>returns(true) implies r.isSuccess<!>
    }
    return if (r.isSuccess) false else true
}

