public class PhysicalRegister {

    String regName;
    String archRegName;
    Integer regData;
    Integer regValid;
    Integer phyRegAllocated;

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public String getArchRegName() {
        return archRegName;
    }

    public void setArchRegName(String archRegName) {
        this.archRegName = archRegName;
    }

    public Integer getRegData() {
        return regData;
    }

    public void setRegData(Integer regData) {
        this.regData = regData;
    }

    public Integer getRegValid() {
        return regValid;
    }

    public void setRegValid(Integer regValid) {
        this.regValid = regValid;
    }

    public Integer getPhyRegAllocated() {
        return phyRegAllocated;
    }

    public void setPhyRegAllocated(Integer phyRegAllocated) {
        this.phyRegAllocated = phyRegAllocated;
    }
}
