import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PipelineOperations {
    static Integer currentCycleValue;
    static Map<Integer, Instruction> instructionMap = new HashMap<>();
    static Integer currentPC = 4000;
    static Integer lastInstrctionAddress;
    static PipelineLatch fetchLatch;
    static PipelineStage fetchStage;
    static PipelineLatch decodeLatch;
    static PipelineStage decodeStage;
    static PipelineLatch intFULatch;
    static PipelineStage intFUStage;
    static PipelineLatch intFULatchinp;
    static PipelineLatch mulFULatchinp;
    static PipelineStage mulFUStage1;
    static PipelineLatch mulFULatch1;
    static PipelineStage mulFUStage2;
    static PipelineLatch mulFULatch2;
    static PipelineLatch divFULatchinp;
    static PipelineStage divFUStage1;
    static PipelineLatch divFULatch1;
    static PipelineStage divFUStage2;
    static PipelineLatch divFULatch2;
    static PipelineStage divFUStage3;
    static PipelineLatch divFULatch3;
    static PipelineStage divFUStage4;
    static PipelineLatch divFULatch4;
    static PipelineLatch robLatch;
    static IQ iq;
    static ReorderBuffer rob;
    static Map<String, String> renameTable;
    static PhysicalRegisterFile phyRegFile;
    static RegisterFile archRegisterFile;
    static Integer programAddress = 4000;
    static Instruction nopInstruction;
    static FileWriter writer;
    static PrintWriter printWriter;


    static void selectInstructionForFU() {
        IQMember issueQueue[] = iq.issueQueue;

        for (int i = 15; i >= 0; i--) {
            if (issueQueue[i].getIqAllocated() == 1) {
                if (issueQueue[i].getOperationType().equalsIgnoreCase("MUL")) {
                    if (issueQueue[i].getSource1() != null && issueQueue[i].getSource2() != null
                            && issueQueue[i].getSource1().getRegValid() == 1 && issueQueue[i].getSource2().getRegValid() == 1) {
                        issueQueue[i].getInstruction().setOperand1(issueQueue[i].getSource1());
                        issueQueue[i].getInstruction().setOperand2(issueQueue[i].getSource2());
                        mulFULatchinp.instruction = issueQueue[i].getInstruction();
                        issueQueue[i] = new IQMember();
                        issueQueue[i].setIqAllocated(0);
                        break;
                    } else
                        continue;
                } else if (issueQueue[i].getOperationType().equalsIgnoreCase("DIV")) {
                    if (issueQueue[i].getSource1() != null && issueQueue[i].getSource2() != null
                            && issueQueue[i].getSource1().getRegValid() == 1 && issueQueue[i].getSource2().getRegValid() == 1) {
                        issueQueue[i].getInstruction().setOperand1(issueQueue[i].getSource1());
                        issueQueue[i].getInstruction().setOperand2(issueQueue[i].getSource2());
                        divFULatchinp.instruction = issueQueue[i].getInstruction();
                        issueQueue[i] = new IQMember();
                        issueQueue[i].setIqAllocated(0);
                        break;
                    } else
                        continue;
                } else if (issueQueue[i].getOperationType().equalsIgnoreCase("ADD") ||
                        issueQueue[i].getOperationType().equalsIgnoreCase("SUB") ||
                        issueQueue[i].getOperationType().equalsIgnoreCase("EXOR") ||
                        issueQueue[i].getOperationType().equalsIgnoreCase("OR") ||
                        issueQueue[i].getOperationType().equalsIgnoreCase("AND")) {
                    if (issueQueue[i].getSource1() != null && issueQueue[i].getSource2() != null
                            && issueQueue[i].getSource1().getRegValid() == 1 && issueQueue[i].getSource2().getRegValid() == 1) {
                        issueQueue[i].getInstruction().setOperand1(issueQueue[i].getSource1());
                        issueQueue[i].getInstruction().setOperand2(issueQueue[i].getSource2());
                        intFULatch.instruction = issueQueue[i].getInstruction();
                        issueQueue[i] = new IQMember();
                        issueQueue[i].setIqAllocated(0);
                        break;
                    } else
                        continue;
                } else if (issueQueue[i].getOperationType().equalsIgnoreCase("MOVC")) {
                    intFULatch.instruction = issueQueue[i].getInstruction();
                    issueQueue[i] = new IQMember();
                    issueQueue[i].setIqAllocated(0);
                    break;
                } else if (issueQueue[i].getOperationType().equalsIgnoreCase("JUMP")) {
                    if (issueQueue[i].getSource1() != null && issueQueue[i].getSource1().getRegValid() == 1) {
                        issueQueue[i].getInstruction().setOperand1(issueQueue[i].getSource1());
                        intFULatch.instruction = issueQueue[i].getInstruction();
                        issueQueue[i] = new IQMember();
                        issueQueue[i].setIqAllocated(0);
                        break;
                    } else
                        continue;
                } else if (issueQueue[i].getOperationType().equalsIgnoreCase("BZ")
                        || issueQueue[i].getOperationType().equalsIgnoreCase("BNZ")) {
                    if (issueQueue[i].getBrancOnZero()) {
                        issueQueue[i].setBrancOnZero(true);
                        issueQueue[i].getInstruction().setZeroFlag(1);
                        if (issueQueue[i].getZeroFlag()) {
                            issueQueue[i].getInstruction().setZeroFlag(0);
                        }
                        intFULatch.instruction = issueQueue[i].getInstruction();
                        issueQueue[i] = new IQMember();
                        issueQueue[i].setIqAllocated(0);
                        break;
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    public static void main(String args[]) {

        String fileName = args[0];
        while (true) {
            String input = new String();
            System.out.println("User console \n  1.Initialize 2.Simulate 3.Display 4.Exit");
            Scanner scan = new Scanner(System.in);
            input = scan.nextLine();

            if (input.equals("1")) {
                initializeStageandElements(fileName);
            } else if (input.equals("2")) {
                System.out.println("Enter no of cycles");
                Scanner in = new Scanner(System.in);
                String no_of_cycles = in.nextLine();
                executeStages(no_of_cycles);
            } else if (input.equals("3")) {
                printOutput("Result.txt");
            } else if (input.equals("4"))
                System.exit(0);
        }
    }

    static void printOutput(String fileName)
    {
        try{
            FileReader file  = new FileReader(fileName);
            BufferedReader br = new BufferedReader(file);
            String line = "";
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
            br.close();
        }catch (Exception e){
            System.out.println("Problem reading output file");
        }
    }

    static void initializeStageandElements(String fileName) {
        currentCycleValue = 1;
        fetchStage = new PipelineStage();
        decodeStage = new PipelineStage();
        intFUStage = new PipelineStage();
        mulFUStage1 = new PipelineStage();
        mulFUStage2 = new PipelineStage();
        divFUStage1 = new PipelineStage();
        divFUStage2 = new PipelineStage();
        divFUStage3 = new PipelineStage();
        divFUStage4 = new PipelineStage();
        fetchLatch = new PipelineLatch();
        decodeLatch = new PipelineLatch();
        intFULatchinp = new PipelineLatch();
        intFULatch = new PipelineLatch();
        mulFULatchinp = new PipelineLatch();
        mulFULatch1 = new PipelineLatch();
        mulFULatch2 = new PipelineLatch();
        divFULatchinp = new PipelineLatch();
        divFULatch1 = new PipelineLatch();
        divFULatch2 = new PipelineLatch();
        divFULatch3 = new PipelineLatch();
        divFULatch4 = new PipelineLatch();
        robLatch = new PipelineLatch();

        nopInstruction = new Instruction();
        nopInstruction.setInstructionName("NOP");
        iq = new IQ();
        rob = new ReorderBuffer();
        renameTable = new HashMap<String, String>();
        phyRegFile = new PhysicalRegisterFile();
        archRegisterFile = new RegisterFile();
        readCSVfile(fileName);
        try {
            writer = new FileWriter("Result.txt");
        }catch (Exception e){
            System.out.println("Exception occured during file creation");
        }
        printWriter = new PrintWriter(writer);
    }

    static void executeStages(String no_of_cycles) {
        while (currentCycleValue < Integer.parseInt(no_of_cycles)) {
            commitReorderBuffer();
            selectInstructionForFU();
            executeDiv4();
            executeDiv3();
            executeDiv2();
            executeDiv1();
            executeMul2();
            executeMul1();
            if (!executeIntFu()) {
                executeIQ();
                if (!executeDecode()) {
                    executeFetch(programAddress);
                }
            }
            printCurrentStatus();
            programAddress += 4;
            currentCycleValue++;
        }
    }

    static void printCurrentStatus(){
        printWriter.println();
        printWriter.println("Cycle :"+ currentCycleValue);
        printWriter.println("Fetch :"+fetchLatch);
        printWriter.println("DRF :"+ decodeLatch);
        printWriter.println("Rename Table :");
        for(Map.Entry<String, String> entry: renameTable.entrySet()){
            printWriter.println(entry.getKey()+" : "+ entry.getValue());
        }
        printWriter.println(iq);
        printWriter.println(rob);
        printWriter.println("INTFU :"+intFULatch);
        printWriter.println("MUL1 :"+mulFULatch1);
        printWriter.println("MUL2 :"+mulFULatch2);
        printWriter.println("DIV1 :"+divFULatch1);
        printWriter.println("DIV2 :"+divFULatch2);
        printWriter.println("DIV3 :"+divFULatch3);
        printWriter.println("Div4 :"+divFULatch4);
        printWriter.println(archRegisterFile);
        printWriter.println(phyRegFile);

        printWriter.println("");
        printWriter.println("");
        printWriter.println("");

    }

    static void commitReorderBuffer() {
        Integer count = 0;
        rob.commitedInstructions = new ArrayList<Instruction> ();
        while (!rob.isROBEmpty()) {

            if (rob.reorderBuffer[rob.head].rbAllocated == 1 && rob.reorderBuffer[rob.head].statusBit == 1) {
                String archRegName = rob.reorderBuffer[rob.head].archDestReg;
                Integer result = rob.reorderBuffer[rob.head].destination.getRegData();
                archRegisterFile.architectureRegisterMap.get(archRegName).regData = result;
                rob.commitedInstructions.add(rob.reorderBuffer[rob.head].instruction);
                rob.removeROBMember();
            }
            if (count > 0) {
                return;
            }
            count++;
        }
    }


    static void restoreUnits(Integer cycleCount, Instruction instruction) {
        phyRegFile.physicalRegisterMap = instruction.phyRegMapBranch;
        renameTable = instruction.renameTableMapBranch;
        for (int i = 0; i < 16; i++) {
            if (iq.issueQueue[i].getIqAllocated() == 1) {
                if (iq.issueQueue[i].getInstruction().getCycleNumber() > cycleCount) {
                    iq.issueQueue[i] = new IQMember();
                    iq.issueQueue[i].setIqAllocated(0);
                }
            }
        }

        Integer branchingIndex = instruction.getRobIndex();
        rob.tail = branchingIndex + 1;
        if (branchingIndex == 31) {
            rob.tail = 0;
        }

        for (int i = 0; i < 32; i++) {
            if (rob.reorderBuffer[i].rbAllocated == 1 && rob.reorderBuffer[i].instruction.getCycleNumber() > cycleCount) {
                rob.reorderBuffer[i] = new ReorderBufferMember();
                rob.reorderBuffer[i].statusBit = 1;
                rob.reorderBuffer[i].rbAllocated = 0;
            }
        }
    }


    static void flushLatch(PipelineLatch latch, Integer cycleNumber) {
        if (!latch.instruction.getInstructionName().equalsIgnoreCase("NOP") &&
                latch.instruction.getCycleNumber() > cycleNumber) {
            latch.instruction = nopInstruction;
        }
    }


    static void flushInstructionForBranch(Integer cycleNumber, Instruction instruction) {
        restoreUnits(cycleNumber, instruction);
        flushLatch(intFULatchinp, cycleNumber);
        flushLatch(intFULatch, cycleNumber);
        flushLatch(mulFULatchinp, cycleNumber);
        flushLatch(mulFULatch1, cycleNumber);
        flushLatch(mulFULatch2, cycleNumber);
        flushLatch(divFULatchinp, cycleNumber);
        flushLatch(divFULatch1, cycleNumber);
        flushLatch(divFULatch2, cycleNumber);
        flushLatch(divFULatch3, cycleNumber);
        flushLatch(divFULatch4, cycleNumber);
        flushLatch(fetchLatch, cycleNumber);
        flushLatch(decodeLatch, cycleNumber);
    }


    static void forwardResult(Instruction instruction) {
        if (instruction.getDestOperand() != null) {
            if (phyRegFile.physicalRegisterMap.containsKey(instruction.destOperand.getRegName())) {
                phyRegFile.physicalRegisterMap.get(instruction.destOperand.getRegName()).setRegData(instruction.getResult());
                phyRegFile.physicalRegisterMap.get(instruction.destOperand.getRegName()).setRegValid(1);
            }
        }
        for (int i = 0; i < 32; i++) {
            if (rob.reorderBuffer[i].rbAllocated == 1 && rob.reorderBuffer[i].cycleNumber.equals(instruction.cycleNumber)) {
                rob.reorderBuffer[i].statusBit = 1;
            }
        }
    }


    static void executeDiv4() {
        Instruction instruction = divFULatch3.instruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            forwardResult(instruction);
            divFULatch4.instruction = instruction;
            divFULatch3.instruction = nopInstruction;
        } else {
            divFULatch4.instruction = nopInstruction;
        }
    }


    static void executeDiv3() {
        Instruction instruction = divFULatch2.instruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            divFULatch3.instruction = instruction;
            divFULatch2.instruction = nopInstruction;
        } else {
            divFULatch3.instruction = nopInstruction;
        }
    }


    static void executeDiv2() {
        Instruction instruction = divFULatch1.instruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            divFULatch2.instruction = instruction;
            divFULatch1.instruction = nopInstruction;
        } else {
            divFULatch2.instruction = nopInstruction;
        }
    }


    static void executeDiv1() {
        Instruction instruction = divFULatchinp.instruction;
        divFULatchinp.instruction = nopInstruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            instruction.setResult(instruction.getOperand1().getRegData() /
                    instruction.getOperand2().getRegData());
            divFULatch1.instruction = instruction;
        } else {
            divFULatch1.instruction = nopInstruction;
        }
    }


    static void executeMul2() {
        Instruction instruction = mulFULatch1.instruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {

            forwardResult(instruction);
            mulFULatch2.instruction = instruction;
            mulFULatch1.instruction = nopInstruction;
        } else {
            mulFULatch2.instruction = nopInstruction;
        }
    }


    static void executeMul1() {
        Instruction instruction = mulFULatchinp.instruction;
        mulFULatchinp.instruction = nopInstruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            instruction.setResult(instruction.getOperand1().getRegData() *
                    instruction.getOperand2().getRegData());

            mulFULatch1.instruction = instruction;
        } else {
            mulFULatch1.instruction = nopInstruction;
        }
    }


    static boolean executeIntFu() {
        Instruction instruction = intFULatch.instruction;
        intFULatch.instruction = nopInstruction;
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            if (instruction.getOperationType().equalsIgnoreCase("OR")) {
                instruction.setResult(instruction.getOperand1().getRegData() |
                        instruction.getOperand2().getRegData());
            } else if (instruction.getOperationType().equalsIgnoreCase("EXOR")) {
                instruction.setResult(instruction.getOperand1().getRegData() ^
                        instruction.getOperand2().getRegData());
            } else if (instruction.getOperationType().equalsIgnoreCase("ADD")) {
                instruction.setResult(instruction.getOperand1().getRegData() +
                        instruction.getOperand2().getRegData());
                if (instruction.getRecentArithmetic() != null && instruction.getRecentArithmetic()) {
                    instructionMap.get(instruction.getPc()).setZeroFlag(instruction.getResult());
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("SUB")) {
                instruction.setResult(instruction.getOperand1().getRegData() -
                        instruction.getOperand2().getRegData());
                if (instruction.getRecentArithmetic() != null && instruction.getRecentArithmetic()) {
                    instructionMap.get(instruction.getPc()).setZeroFlag(instruction.getResult());
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("AND")) {
                instruction.setResult(instruction.getOperand1().getRegData() &
                        instruction.getOperand2().getRegData());
            } else if (instruction.getOperationType().equalsIgnoreCase("MOVC")) {
                instruction.setResult(Integer.parseInt(instruction.getLiteral()));
            } else if (instruction.getOperationType().equalsIgnoreCase("BZ")) {
                if (instruction.getZeroFlag() == 0) {
                    programAddress = instruction.getPc() + Integer.parseInt(instruction.getLiteral());
                    flushInstructionForBranch(instruction.getCycleNumber(), instruction);
                    return true;
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("BNZ")) {
                if (instruction.getZeroFlag() != 0) {
                    programAddress = instruction.getPc() + Integer.parseInt(instruction.getLiteral());
                    flushInstructionForBranch(instruction.getCycleNumber(), instruction);
                    return true;
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("JUMP")) {
                programAddress = instruction.getOperand1().getRegData() + Integer.parseInt(instruction.getLiteral());
                flushInstructionForBranch(instruction.getCycleNumber(), instruction);
                return true;
            }
            forwardResult(instruction);

            intFULatch.instruction = instruction;
        } else {
            intFULatch.instruction = nopInstruction;
        }
        return false;
    }


    static void executeIQ() {
        Instruction instruction = decodeLatch.instruction;
        IQMember issueQueue[] = iq.issueQueue;

        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            Integer index = rob.addROBMember(instruction);
            instruction.setRobIndex(index);
            for (int i = 15; i >= 0; i--) {
                if (issueQueue[i].getIqAllocated() == 0) {
                    issueQueue[i].setInstruction(instruction);
                    issueQueue[i].setOperationType(instruction.operationType);
                    issueQueue[i].setIqAllocated(1);
                    if (instruction.getOperand1() != null) {
                        issueQueue[i].setSource1(instruction.getOperand1());
                    }
                    if (instruction.getOperand2() != null) {
                        issueQueue[i].setSource2(instruction.getOperand2());
                    }
                    if (instruction.getLiteral() != null) {
                        issueQueue[i].setLiteralOperand(instruction.getLiteral());
                    }
                    if (instruction.getDestOperand() != null) {
                        issueQueue[i].setDestination(instruction.getDestOperand());
                    }
                    if (instruction.getOperationType().equalsIgnoreCase("BZ")
                            || instruction.getOperationType().equalsIgnoreCase("BNZ")) {
                        issueQueue[i].setBrancOnZero(false);
                        issueQueue[i].setDependentInstruction(instruction.getDependentInstruction());
                        if (instructionMap.get(instruction.getDependentInstruction()).getZeroFlag() != null) {
                            issueQueue[i].setBrancOnZero(true);
                            if (instructionMap.get(instruction.getDependentInstruction()).getZeroFlag() == 0) {
                                issueQueue[i].setZeroFlag(true);
                            } else {
                                issueQueue[i].setZeroFlag(false);
                            }
                        }
                    }
                    break;
                }
            }
        }

        for (int i = 15; i >= 0; i--) {
            if (issueQueue[i].getIqAllocated() == 1) {
                if (issueQueue[i].getOperationType().equalsIgnoreCase("BZ")
                        || issueQueue[i].getOperationType().equalsIgnoreCase("BNZ")) {
                    if (instructionMap.get(issueQueue[i].getDependentInstruction()).getZeroFlag() != null) {
                        issueQueue[i].setBrancOnZero(true);
                        if (instructionMap.get(issueQueue[i].getDependentInstruction()).getZeroFlag() == 0) {
                            issueQueue[i].setZeroFlag(true);
                        } else {
                            issueQueue[i].setZeroFlag(false);
                        }
                    }
                }
            }
            if (issueQueue[i].getSource1() != null) {
                if (issueQueue[i].getSource1().getRegValid() == 0) {
                    if (phyRegFile.physicalRegisterMap.get(issueQueue[i].getSource1().getRegName()).getRegValid() == 1) {
                        issueQueue[i].getSource1().setRegValid(1);
                        issueQueue[i].getSource1().setRegData(phyRegFile.physicalRegisterMap.
                                get(issueQueue[i].getSource1().getRegName()).getRegData());
                    }
                }
            }
            if (issueQueue[i].getSource2() != null) {
                if (issueQueue[i].getSource2().getRegValid() == 0) {
                    if (phyRegFile.physicalRegisterMap.get(issueQueue[i].getSource2().getRegName()).getRegValid() == 1) {
                        issueQueue[i].getSource2().setRegValid(1);
                        issueQueue[i].getSource2().setRegData(phyRegFile.physicalRegisterMap.
                                get(issueQueue[i].getSource2().getRegName()).getRegData());
                    }
                }
            }
        }
    }

    static Boolean executeDecode() {

        Instruction instruction = fetchLatch.instruction;
        Map<String, PhysicalRegister> physicalRegisterMap = phyRegFile.physicalRegisterMap;
        if (decodeStage.stall == 1) {
            instruction = decodeLatch.instruction;
        }
        if (!instruction.getInstructionName().equalsIgnoreCase("NOP")) {
            String sourceReg1 = "";
            PhysicalRegister physicalRegister1 = null;
            String sourceReg2 = "";
            PhysicalRegister physicalRegister2 = null;
            Integer literal = null;
            String archDestReg = "";
            PhysicalRegister physicalRegisterDest = null;
            Boolean decodeStageStall = false;
            decodeStage.stall = 1;
            String[] instructionString = instruction.getInstructionName().split(",");
            if (instruction.getOperationType().equalsIgnoreCase("BZ")
                    || (instruction.getOperationType().equalsIgnoreCase("BNZ"))) {
                if (!iq.isIQFull() && !rob.isROBFull()) {
                    instruction.setLiteral(instructionString[1].replaceAll("#", ""));
                    Map<String, PhysicalRegister> temp1 = new HashMap<String, PhysicalRegister>();
                    temp1.putAll(physicalRegisterMap);
                    instruction.phyRegMapBranch = temp1;

                    Map<String, String> temp2 = new HashMap<String, String>();
                    temp2.putAll(renameTable);
                    instruction.renameTableMapBranch = temp2;


                } else {
                    decodeStageStall = true;
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("MOVC")) {
                if (!iq.isIQFull() && !rob.isROBFull() && checkPhysicalRegister()) {
                    physicalRegisterDest = fetchPhysicalRegister();
                    archDestReg = instructionString[1];
                    physicalRegisterDest.setRegValid(0);
                    physicalRegisterDest.setPhyRegAllocated(1);
                    physicalRegisterDest.setArchRegName(instructionString[1]);
                    renameTable.put(instructionString[1], physicalRegisterDest.getRegName());
                    instruction.setDestOperand(physicalRegisterDest);

                    instruction.setLiteral(instructionString[2].replaceAll("#", ""));
                    instruction.setInstructionName(instruction.getInstructionNumber() + instruction.getOperationType() + ","
                           + physicalRegisterDest.getRegName() + "," + instructionString[2]);

                } else {
                    decodeStageStall = true;
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("JUMP")) {
                if (!iq.isIQFull() && !rob.isROBFull()) {
                    Map<String, PhysicalRegister> temp1 = new HashMap<String, PhysicalRegister>();
                    temp1.putAll(physicalRegisterMap);
                    instruction.phyRegMapBranch = temp1;

                    Map<String, String> temp2 = new HashMap<String, String>();
                    temp2.putAll(renameTable);
                    instruction.renameTableMapBranch = temp2;

                    if (renameTable.containsKey(instructionString[1])) {
                        PhysicalRegister physicalRegisterTemp = physicalRegisterMap.get(renameTable.get(instructionString[1]));
                        physicalRegister1 = new PhysicalRegister();
                        physicalRegister1.setRegName(physicalRegisterTemp.getRegName());
                        physicalRegister1.setRegValid(physicalRegisterTemp.getRegValid());
                        sourceReg1 = physicalRegisterTemp.getRegName();

                        if (physicalRegisterTemp.getRegData() != null)
                            physicalRegister1.setRegData(physicalRegisterTemp.getRegData());

                        instruction.setOperand1(physicalRegister1);
                    }
                    instruction.setLiteral(instructionString[2].replaceAll("#", ""));
                    instruction.setInstructionName(instruction.getInstructionNumber() + instruction.getOperationType() + ","
                            + sourceReg1 + "," + instructionString[2]);

                } else {
                    decodeStageStall = true;
                }
            } else if (instruction.getOperationType().equalsIgnoreCase("HALT")) {
                decodeLatch.instruction = instruction;
                programAddress = lastInstrctionAddress + 4;
                decodeStage.stall = 0;
                flushLatch(fetchLatch, instruction.cycleNumber);
                return true;
            } else {
                if (!iq.isIQFull() && !rob.isROBFull() && checkPhysicalRegister()) {
                    physicalRegisterDest = fetchPhysicalRegister();
                    archDestReg = instructionString[1];
                    physicalRegisterDest.setRegValid(0);
                    physicalRegisterDest.setPhyRegAllocated(1);
                    physicalRegisterDest.setArchRegName(instructionString[1]);
                    renameTable.put(instructionString[1], physicalRegisterDest.getRegName());
                    instruction.setDestOperand(physicalRegisterDest);

                    if (renameTable.containsKey(instructionString[2])) {
                        PhysicalRegister physicalRegisterTemp = physicalRegisterMap.get(renameTable.get(instructionString[2]));
                        physicalRegister1 = new PhysicalRegister();
                        physicalRegister1.setRegName(physicalRegisterTemp.getRegName());
                        physicalRegister1.setRegValid(physicalRegisterTemp.getRegValid());
                        sourceReg1 = physicalRegisterTemp.getRegName();

                        if (physicalRegisterTemp.getRegData() != null)
                            physicalRegister1.setRegData(physicalRegisterTemp.getRegData());

                        instruction.setOperand1(physicalRegister1);
                    }

                    if (renameTable.containsKey(instructionString[3])) {
                        PhysicalRegister physicalRegisterTemp2 = physicalRegisterMap.get(renameTable.get(instructionString[3]));
                        physicalRegister2 = new PhysicalRegister();
                        physicalRegister2.setRegName(physicalRegisterTemp2.getRegName());
                        physicalRegister2.setRegValid(physicalRegisterTemp2.getRegValid());
                        sourceReg2 = physicalRegisterTemp2.getRegName();

                        if (physicalRegisterTemp2.getRegData() != null)
                            physicalRegister2.setRegData(physicalRegisterTemp2.getRegData());

                        instruction.setOperand2(physicalRegister2);
                        instruction.setInstructionName(instruction.getInstructionNumber() + instruction.getOperationType() + ","+
                                physicalRegisterDest.getRegName()+"," + sourceReg1 + "," + sourceReg2);
                    }

                } else {
                    decodeStageStall = true;
                }
            }
            decodeLatch.instruction = instruction;
            if (decodeStageStall == true) {
                return false;
            }
            decodeStage.stall = 0;
            decodeLatch.instruction = instruction;
            fetchLatch.instruction = nopInstruction;
        } else {
            decodeLatch.instruction = nopInstruction;
        }
        return false;
    }

    static void executeFetch(Integer pcAddress) {
        if (instructionMap.containsKey(pcAddress)) {
            if (decodeStage.stall == 0) {
                Instruction fetchInstruction = new Instruction();
                Instruction instruction = instructionMap.get(pcAddress);
                fetchInstruction.setPc(instruction.getPc());
                fetchInstruction.setInstructionName(instruction.getInstructionName());
                fetchInstruction.setCycleNumber(currentCycleValue);
                fetchInstruction.setDependentInstruction(instruction.getDependentInstruction());
                fetchInstruction.setOperationType(instruction.getOperationType());
                fetchInstruction.setInstructionNumber(instruction.getInstructionNumber());
                fetchInstruction.setInstructionName(fetchInstruction.getInstructionNumber() + instruction.getInstructionName());
                fetchLatch.instruction = fetchInstruction;
            }
        } else {
            fetchLatch.instruction = nopInstruction;
        }
    }


    static void readCSVfile(String file) {
        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            int i = 0;
            int arithmeticInstruction = 4000;
            Integer dependentInstrcutionAddress = null;
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                Instruction instruction = new Instruction();

                // use comma as separator
                String[] instructionString = line.split(cvsSplitBy);
                instruction.setInstructionName(line);
                instruction.setOperationType(instructionString[0]);
                instruction.setInstructionNumber("(I" + i + ")");
                instruction.setPc(currentPC);
                lastInstrctionAddress = currentPC;

                if (instruction.getOperationType().equalsIgnoreCase("Add") ||
                        instruction.getOperationType().equalsIgnoreCase("Sub") ||
                        instruction.getOperationType().equalsIgnoreCase("Mul") ||
                        instruction.getOperationType().equalsIgnoreCase("Div")) {
                    dependentInstrcutionAddress = instruction.getPc();
                    arithmeticInstruction = currentPC;
                } else if (instruction.getOperationType().equalsIgnoreCase("BZ") ||
                        instruction.getOperationType().equalsIgnoreCase("BNZ")) {
                    instruction.setDependentInstruction(dependentInstrcutionAddress);
                    Instruction tempInstructiion = instructionMap.get(arithmeticInstruction);
                    tempInstructiion.setRecentArithmetic(true);
                }
                instructionMap.put(currentPC, instruction);
                i++;

                currentPC += 4;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static Boolean checkPhysicalRegister() {
        for (Map.Entry<String, PhysicalRegister> keyValuePair : phyRegFile.physicalRegisterMap.entrySet()) {
            if (keyValuePair.getValue().getPhyRegAllocated() == 0) {
                return true;
            }
        }
        return false;
    }

    static PhysicalRegister fetchPhysicalRegister() {
        for (Map.Entry<String, PhysicalRegister> keyValuePair : phyRegFile.physicalRegisterMap.entrySet()) {
            if (keyValuePair.getValue().getPhyRegAllocated() == 0) {
                return keyValuePair.getValue();
            }
        }
        return null;
    }
}
