package hostel;

import java.util.ArrayList;

public class Hostel {

    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Student> waiting = new ArrayList<>();

    // Custom constructor
    public Hostel(String prefix, int roomCount, int capacity) {
        for (int i = 1; i <= roomCount; i++) {
            String id = String.format("%s%03d", prefix, i);
            rooms.add(new MultiRoom(id, capacity));
        }
    }

    public boolean addStudent(Student s) {
        if (hasRoll(s.getRollNo())) return false;
        students.add(s);
        return true;
    }

    public boolean hasRoll(String roll) {
        for (Student st : students)
            if (st.getRollNo().equalsIgnoreCase(roll)) return true;

        for (Student st : waiting)
            if (st.getRollNo().equalsIgnoreCase(roll)) return true;

        for (Room r : rooms)
            for (Student occ : r.getOccupants())
                if (occ.getRollNo().equalsIgnoreCase(roll)) return true;

        return false;
    }

    public Room allocateRoom(Student s) {
        for (Room r : rooms) {
            if (r.isAvailable() &&
                (s.getRoomPreference().equals("Any") ||
                 r.getType().equalsIgnoreCase(s.getRoomPreference())))
            {
                r.occupy(s);
                return r;
            }
        }

        waiting.add(s);
        return null;
    }

    public ArrayList<Room> getRooms() { return rooms; }
    public ArrayList<Student> getWaitingList() { return waiting; }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        for (Room r : rooms) {
            sb.append(r.getRoomId())
              .append(" [").append(r.getType()).append("] → ")
              .append(r.getOccupants().size()).append("/")
              .append(r.getCapacity())
              .append("  ").append(r.getOccupantNames())
              .append("\n");
        }
        return sb.toString();
    }
}
