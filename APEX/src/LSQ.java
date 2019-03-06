public class LSQ {

    LSQMember lsQueue[];

    LSQ(){
        lsQueue = new LSQMember[32];

        for(int i = 0; i < 32; i++){
            LSQMember lsqMem = new LSQMember();
            lsqMem.setLsqAllocated(0);
            lsQueue[i] = lsqMem;
        }
    }

    Boolean isLSQEmpty(){
        Boolean empty = true;

        for(int i = 0; i < 32; i++){
            if(lsQueue[i].getLsqAllocated() != 0) {
                empty = false;
                break;
            }
        }

        return empty;
    }

    Boolean isLSQFull(){
        Boolean full = true;

        for(int i = 0; i < 32; i++){
            if(lsQueue[i].getLsqAllocated() == 0) {
                full = false;
                break;
            }
        }

        return full;
    }

}
