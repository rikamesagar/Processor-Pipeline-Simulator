public class IQMember {

    Integer cycleNumber;
    String operationType;
    String literalOperand;
    Integer iqAllocated;
    PhysicalRegister source1;
    PhysicalRegister source2;
    PhysicalRegister destination;
    Boolean brancOnZero;
    Boolean zeroFlag;
    Instruction instruction;
    Integer dependentInstruction;

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

    public Boolean getBrancOnZero() {
        return brancOnZero;
    }

    public void setBrancOnZero(Boolean brancOnZero) {
        this.brancOnZero = brancOnZero;
    }

    public Boolean getZeroFlag() {
        return zeroFlag;
    }

    public void setZeroFlag(Boolean zeroFlag) {
        this.zeroFlag = zeroFlag;
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

    public String getLiteralOperand() {
        return literalOperand;
    }

    public void setLiteralOperand(String literalOperand) {
        this.literalOperand = literalOperand;
    }

    public Integer getIqAllocated() {
        return iqAllocated;
    }

    public void setIqAllocated(Integer iqAllocated) {
        this.iqAllocated = iqAllocated;
    }

    public PhysicalRegister getSource1() {
        return source1;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Integer getDependentInstruction() {
        return dependentInstruction;
    }

    public void setDependentInstruction(Integer dependentInstruction) {
        this.dependentInstruction = dependentInstruction;
    }
}
