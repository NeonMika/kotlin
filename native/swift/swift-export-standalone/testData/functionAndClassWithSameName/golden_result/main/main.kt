import kotlin.native.internal.ExportedBridge
import kotlinx.cinterop.*

@ExportedBridge("testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init__TypesOfArguments__int32_t__")
public fun testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init(x: Int): kotlin.native.internal.NativePtr {
    val __x = x
    val _result = testData.functionAndClassWithSameName.functionAndClassWithSameName.UtcOffset(__x)
    return kotlin.native.internal.ref.createRetainedExternalRCRef(_result)
}

@ExportedBridge("testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init_allocate")
public fun testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init_allocate(): kotlin.native.internal.NativePtr {
    val _result = kotlin.native.internal.createUninitializedInstance<testData.functionAndClassWithSameName.functionAndClassWithSameName.UtcOffset>()
    return kotlin.native.internal.ref.createRetainedExternalRCRef(_result)
}

@ExportedBridge("testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init_initialize__TypesOfArguments__uintptr_t__")
public fun testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init_initialize(__kt: kotlin.native.internal.NativePtr): Unit {
    val ____kt = kotlin.native.internal.ref.dereferenceExternalRCRef(__kt)
    kotlin.native.internal.initInstance(____kt, testData.functionAndClassWithSameName.functionAndClassWithSameName.UtcOffset())
}
