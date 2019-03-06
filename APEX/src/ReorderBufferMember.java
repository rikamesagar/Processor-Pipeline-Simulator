public class ReorderBufferMember {

    Integer cycleNumber;
    Integer statusBit;
    Integer rbAllocated;
    String archDestReg;
    Instruction instruction;
    PhysicalRegister source1;
    PhysicalRegister source2;
    PhysicalRegister destination;
    String operationType;
    Integer index;
}
