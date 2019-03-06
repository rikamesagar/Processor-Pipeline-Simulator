import java.util.HashMap;
import java.util.Map;

public class PhysicalRegisterFile {


    Map<String, PhysicalRegister> physicalRegisterMap;

    public PhysicalRegisterFile() {
        physicalRegisterMap = new HashMap<String, PhysicalRegister>();
        for (int i = 0; i < 32; i++) {
            PhysicalRegister physicalRegister = new PhysicalRegister();
            physicalRegister.setRegName("P" + i);
            physicalRegister.setRegValid(1);
            physicalRegister.setRegData(0);
            physicalRegister.setPhyRegAllocated(0);
            physicalRegister.setArchRegName("");
            physicalRegisterMap.put(physicalRegister.getRegName(), physicalRegister);
        }
    }

    @Override
    public String toString() {
        String result = "Physical Register File : \n";
        for(Map.Entry<String, PhysicalRegister> entry: physicalRegisterMap.entrySet()){
            result += ": Register :"+ entry.getKey() + ": Valid :"+ entry.getValue().regValid+ ": Value :"+entry.getValue().regData;
        }
        return result;
    }
}
