package hostel;

import java.util.*;

/**
 * Manages multiple hostel branches (Savithri, Samyutha, Saudhamini).
 * Handles:
 *  - global roll number checking
 *  - allocation inside selected branch
 *  - searching by roll or name
 */
public class HostelManager {

    private ArrayList<HostelBranch> branches = new ArrayList<>();

    // Add a hostel branch
    public void addBranch(HostelBranch b) {
        branches.add(b);
    }

    // Get list of all branches
    public List<HostelBranch> getBranches() {
        return branches;
    }

    // GLOBAL: Check if a roll number exists in ANY hostel
    public boolean isRollExistsGlobally(String roll) {
        if (roll == null) return false;
        for (HostelBranch hb : branches) {
            if (hb.getHostel().hasRoll(roll)) return true;
        }
        return false;
    }

    // Add student into a specific branch
    public boolean addStudentToBranch(String branchName, Student s) {
        if (isRollExistsGlobally(s.getRollNo())) return false; // global duplicate
        HostelBranch b = getBranchByName(branchName);
        if (b == null) return false;
        return b.getHostel().addStudent(s);
    }

    // Allocate inside a specific hostel
    public Room allocateInBranch(String branchName, Student s) {
        HostelBranch b = getBranchByName(branchName);
        if (b == null) return null;
        return b.getHostel().allocateRoom(s);
    }

    // Get hostel branch by name
    public HostelBranch getBranchByName(String name) {
        for (HostelBranch b : branches) {
            if (b.getName().equalsIgnoreCase(name))
                return b;
        }
        return null;
    }

    // ---------- SEARCH FEATURES ---------- //

    // Find which branch contains the student (by roll)
    public HostelBranch findBranchByRoll(String roll) {
        if (roll == null) return null;
        for (HostelBranch b : branches) {
            if (b.getHostel().hasRoll(roll)) return b;
        }
        return null;
    }

    /**
     * Find student's location by roll number.
     * Returns: Map with keys:
     *   - "branch"
     *   - "room"
     *   - "student"
     *   - OR "waiting" = true (if in waiting list)
     */
    public Map<String, Object> findStudentLocationByRoll(String roll) {
        Map<String, Object> res = new HashMap<>();
        if (roll == null) return res;

        for (HostelBranch b : branches) {
            Hostel h = b.getHostel();

            // Check rooms
            for (Room r : h.getRooms()) {
                for (Student occ : r.getOccupants()) {
                    if (occ.getRollNo().equalsIgnoreCase(roll)) {
                        res.put("branch", b);
                        res.put("room", r);
                        res.put("student", occ);
                        return res;
                    }
                }
            }

            // Check waiting list
            for (Student w : h.getWaitingList()) {
                if (w.getRollNo().equalsIgnoreCase(roll)) {
                    res.put("branch", b);
                    res.put("waiting", true);
                    res.put("student", w);
                    return res;
                }
            }
        }
        return res;
    }

    /**
     * Search students by name (partial match).
     * Returns List<Map> with keys:
     *   - "branch"
     *   - "room" or "waiting"
     *   - "student"
     */
    public List<Map<String, Object>> findStudentByName(String nameQuery) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (nameQuery == null || nameQuery.trim().isEmpty()) return results;

        String q = nameQuery.trim().toLowerCase();

        for (HostelBranch b : branches) {
            Hostel h = b.getHostel();

            // Check rooms
            for (Room r : h.getRooms()) {
                for (Student occ : r.getOccupants()) {
                    if (occ.getName().toLowerCase().contains(q)) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("branch", b);
                        map.put("room", r);
                        map.put("student", occ);
                        results.add(map);
                    }
                }
            }

            // Check waiting list
            for (Student w : h.getWaitingList()) {
                if (w.getName().toLowerCase().contains(q)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("branch", b);
                    map.put("waiting", true);
                    map.put("student", w);
                    results.add(map);
                }
            }
        }

        return results;
    }
}
