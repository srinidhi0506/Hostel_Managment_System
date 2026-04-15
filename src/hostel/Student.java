package hostel;

public class Student {
    private String rollNo;
    private String name;
    private String gender;
    private String roomPreference;
    private int year;

    public Student(String rollNo, String name, String gender, String roomPreference, int year) {
        this.rollNo = rollNo;
        this.name = name;
        this.gender = gender;
        this.roomPreference = roomPreference;
        this.year = year;
    }

    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getRoomPreference() { return roomPreference; }
    public int getYear() { return year; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return this.rollNo.equalsIgnoreCase(s.rollNo);
    }

    @Override
    public int hashCode() {
        return rollNo.toLowerCase().hashCode();
    }
}
