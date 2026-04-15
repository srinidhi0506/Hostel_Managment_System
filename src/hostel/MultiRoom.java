package hostel;

public class MultiRoom extends Room {

    public MultiRoom(String id, int capacity) {
        super(id, capacity);
    }

    @Override
    public String getType() {
        return getCapacity() + "-Bed";
    }
}
