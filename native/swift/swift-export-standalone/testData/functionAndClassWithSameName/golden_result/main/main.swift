@_exported import ExportedKotlinPackages
@_implementationOnly import KotlinBridges_main
import KotlinRuntime

public extension ExportedKotlinPackages.testData.functionAndClassWithSameName.functionAndClassWithSameName.UtcOffset {
    public convenience init(
        x: Swift.Int32
    ) {
        let __kt = testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init__TypesOfArguments__int32_t__(x)
        self.init(__externalRCRef: __kt)
    }
}
public extension ExportedKotlinPackages.testData.functionAndClassWithSameName.functionAndClassWithSameName {
    public class UtcOffset : KotlinRuntime.KotlinBase {
        public override init() {
            let __kt = testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init_allocate()
            super.init(__externalRCRef: __kt)
            testData_functionAndClassWithSameName_functionAndClassWithSameName_UtcOffset_init_initialize__TypesOfArguments__uintptr_t__(__kt)
        }
        public override init(
            __externalRCRef: Swift.UInt
        ) {
            super.init(__externalRCRef: __externalRCRef)
        }
    }
}
