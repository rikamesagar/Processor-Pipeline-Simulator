import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReorderBuffer {

    Integer head, tail, maxSize;

    ReorderBufferMember reorderBuffer[];

    List<Instruction> commitedInstructions;

    ReorderBuffer(){
        reorderBuffer = new ReorderBufferMember[32];
        head = 0;
        tail = 0;
        maxSize = 32;

        for(int i = 0; i < maxSize; i++){
            ReorderBufferMember robMem = new ReorderBufferMember();
            robMem.rbAllocated = 0;
            robMem.statusBit = 1;
            reorderBuffer[i] = robMem;
        }
        commitedInstructions =  new ArrayList<Instruction>();
    }

    Boolean isROBEmpty(){
        Boolean empty = true;

        for(int i = 0; i < 32; i++){
            if(reorderBuffer[i].rbAllocated != 0) {
                empty = false;
                break;
            }
        }

        return empty;
    }


    Boolean isROBFull(){
        Boolean full = true;

        for(int i = 0; i < 32; i++){
            if(reorderBuffer[i].rbAllocated == 0) {
                full = false;
                break;
            }
        }

        return full;
    }

    Integer addROBMember(Instruction instruction){
        Integer index = tail;
        ReorderBufferMember robMember = new ReorderBufferMember();
        robMember.index = index;

        robMember.instruction = instruction;
        robMember.statusBit = 0;
        robMember.rbAllocated = 1;
        robMember.operationType = instruction.getOperationType();
        robMember.cycleNumber = instruction.cycleNumber;

        if(instruction.getDestOperand() != null && instruction.getDestOperand().getArchRegName() != null && !instruction.getDestOperand().getArchRegName().equalsIgnoreCase("")){
            robMember.archDestReg = instruction.getDestOperand().getArchRegName();
        }
        if (instruction.getDestOperand() != null){
            robMember.destination = instruction.getDestOperand();
        }

        reorderBuffer[tail] = robMember;
        tail = (tail + 1) % maxSize;

        return index;
    }

    ReorderBufferMember removeROBMember(){
        ReorderBufferMember robMember = reorderBuffer[head];
        robMember.rbAllocated = 0;
        robMember.statusBit = 1;
        head = (head + 1) % maxSize;

        return robMember;

    }

    @Override
    public String toString() {
        String result = "\n <ROB>";
        for(int i =0; i< 32; i++){
            if(reorderBuffer[i].rbAllocated == 1) {
                result += "\n *"+ reorderBuffer[i].instruction.getInstructionName();
            }
        }
        result += "\n Commit :";
        if(commitedInstructions.size()>0){
            for(Instruction instruction: commitedInstructions){
                result += "\n *" + instruction.getInstructionName();
            }
        }
        return result;
    }
}
