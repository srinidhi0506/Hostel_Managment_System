package hostel;

public class SingleRoom extends Room {
    public SingleRoom(String id) {
        super(id,1);
    }

    @Override
    public String getType() {
        return "Single";
    }
}
