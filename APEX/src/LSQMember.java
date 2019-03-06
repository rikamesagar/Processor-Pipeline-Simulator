public class LSQMember {

    Integer cycleNumber;
    String operationType;
    Integer lsqAllocated;
    Integer literalOperand;
    PhysicalRegister source1;
    PhysicalRegister source2;
    PhysicalRegister destination;
    Instruction instruction;

    public Integer getLsqAllocated() {
        return lsqAllocated;
    }

    public void setLsqAllocated(Integer lsqAllocated) {
        this.lsqAllocated = lsqAllocated;
    }

    public Integer getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(Integer cycleNumber) {
        this.cycleNumber = cycleNumber;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Integer getLiteralOperand() {
        return literalOperand;
    }

    public void setLiteralOperand(Integer literalOperand) {
        this.literalOperand = literalOperand;
    }

    public PhysicalRegister getSource1() {
        return source1;
    }

    public void setSource1(PhysicalRegister source1) {
        this.source1 = source1;
    }

    public PhysicalRegister getSource2() {
        return source2;
    }

    public void setSource2(PhysicalRegister source2) {
        this.source2 = source2;
    }

    public PhysicalRegister getDestination() {
        return destination;
    }

    public void setDestination(PhysicalRegister destination) {
        this.destination = destination;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }
}
