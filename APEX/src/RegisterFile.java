import java.util.HashMap;
import java.util.Map;

public class RegisterFile {

    Map<String, ArchitectureRegister> architectureRegisterMap;

    public RegisterFile() {
        architectureRegisterMap = new HashMap<String, ArchitectureRegister>();
        for (int i = 0; i < 32; i++) {
            ArchitectureRegister architectureRegister  = new ArchitectureRegister();
            architectureRegister.regName = "R"+i;
            architectureRegister.regData = 0;
            architectureRegisterMap.put(architectureRegister.regName, architectureRegister);
        }
    }

    @Override
    public String toString() {
        String result = "Architecture Register File : \n";
        for(Map.Entry<String, ArchitectureRegister> entry: architectureRegisterMap.entrySet()){
            result += ": Register :"+ entry.getKey() + ": Value :"+entry.getValue().regData;
        }
        return result;
    }
}