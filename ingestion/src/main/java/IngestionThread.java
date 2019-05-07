public class IngestionThread implements Runnable {
    private ObjectStorage storage;
    private long checkTimeInterval;

    public void IngestionThread(ObjectStorage storage, long checkTimeInterval){
        this.storage = storage;
        this.checkTimeInterval = checkTimeInterval;
    }

    @Override public void run() {
        while (true){
            int numObjects = storage.getNumObjects();
            if(numObjects == 0 && !storage.isOpen()){
                break;
            }
            long beginTime = System.currentTimeMillis();
            for(int i = 0; i < numObjects; i++){
                tagObject(storage.pollObject());
            }
            try {
                Thread.sleep(checkTimeInterval - System.currentTimeMillis() - beginTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tagObject(Object o){

    }


}
