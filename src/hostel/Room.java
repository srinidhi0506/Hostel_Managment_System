package hostel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Room {
    protected String roomId;
    protected int capacity;
    protected List<Student> occupants = new ArrayList<>();

    public Room(String roomId, int capacity) {
        this.roomId = roomId;
        this.capacity = capacity;
    }

    public String getRoomId() { return roomId; }
    public int getCapacity() { return capacity; }

    public boolean isAvailable() {
        return occupants.size() < capacity;
    }

    public boolean occupy(Student s) {
        if (!isAvailable()) return false;
        occupants.add(s);
        return true;
    }

    public void vacateStudent(Student s) {
        occupants.remove(s);
    }

    public List<Student> getOccupants() {
        return occupants;
    }

    public String getOccupantNames() {
        return occupants.stream()
                .map(Student::getName)
                .collect(Collectors.joining(", "));
    }

    public abstract String getType();
}
