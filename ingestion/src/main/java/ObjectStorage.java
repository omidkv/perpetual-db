import java.util.LinkedList;
import java.util.Queue;

public class ObjectStorage {
    private Queue<Object> data = new LinkedList<>();
    private String type;
    private boolean open;

    public void ObjectStorage(String type){
        this.type = type;
        this.open = true;
    }

    public void addObject(Object o){
        data.add(o);
    }

    public int getNumObjects(){
        return data.size();
    }

    public Object pollObject(){
        return data.poll();
    }

    public String getType() {
        return type;
    }

    public boolean isOpen(){
        return open;
    }

    public void close(){
        this.open = false;
    }
}
