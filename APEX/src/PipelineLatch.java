public class PipelineLatch {

    Instruction instruction;

    public PipelineLatch() {
        flushLatch();
    }

    void flushLatch(){
        Instruction nopInstruction = new Instruction();
        nopInstruction.setInstructionName("NOP");
        this.instruction = nopInstruction;
    }

    @Override
    public String toString() {
        return instruction.getInstructionName();
    }
}
