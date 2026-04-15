package hostel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainUI extends JFrame {

    private HostelManager manager = new HostelManager();

    private JTextField rollField, nameField, yearField;
    private JComboBox<String> genderBox, roomPrefBox, hostelBox;
    private JTextArea output;

    // New UI components
    private JPanel roomGridPanel;   // visual grid of rooms
    private JTextField searchField;
    private JButton searchBtn;
    private JButton statusBtn;

    public MainUI() {
        // Create custom hostels as requested
        Hostel savithri = new Hostel("SV", 30, 6);      // 30 rooms, 6-bed
        Hostel samyutha = new Hostel("SM", 28, 3);      // 28 rooms, 3-bed
        Hostel saudhamini = new Hostel("SD", 28, 4);    // 28 rooms, 4-bed

        manager.addBranch(new HostelBranch("Savithri Hostel", savithri));
        manager.addBranch(new HostelBranch("Samyutha Hostel", samyutha));
        manager.addBranch(new HostelBranch("Saudhamini Hostel", saudhamini));

        setTitle("🏠 Hostel Room Allocation System");
        setSize(980, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(true);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        JLabel title = new JLabel("Hostel Room Allocation System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Left: Form + Search + Buttons
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        left.setPreferredSize(new Dimension(360, 0));

        // Form panel
        JPanel form = new JPanel(new GridLayout(9, 2, 8, 8));
        form.setMaximumSize(new Dimension(340, 300));

        hostelBox = new JComboBox<>();
        for (HostelBranch b : manager.getBranches()) hostelBox.addItem(b.getName());

        rollField = new JTextField();
        nameField = new JTextField();
        yearField = new JTextField();

        genderBox = new JComboBox<>(new String[]{"Male", "Female"});
        // will be filled dynamically when hostel selected
        roomPrefBox = new JComboBox<>(new String[]{"Any"});

        form.add(new JLabel("Select Hostel:"));
        form.add(hostelBox);
        form.add(new JLabel("Roll No:"));
        form.add(rollField);
        form.add(new JLabel("Student Name:"));
        form.add(nameField);
        form.add(new JLabel("Gender:"));
        form.add(genderBox);
        form.add(new JLabel("Year:"));
        form.add(yearField);
        form.add(new JLabel("Room Preference:"));
        form.add(roomPrefBox);

        JButton allocateBtn = new JButton("Allocate Room");
        allocateBtn.setBackground(new Color(46, 204, 113));
        allocateBtn.setForeground(Color.WHITE);
        allocateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        allocateBtn.addActionListener(e -> allocate());

        form.add(new JLabel());
        form.add(allocateBtn);

        left.add(form);
        left.add(Box.createVerticalStrut(10));

        // Status + Search row
        JPanel row = new JPanel(new GridLayout(2, 1, 6, 6));
        statusBtn = new JButton("View Hostel Status");
        statusBtn.addActionListener(e -> showHostelStatus());
        row.add(statusBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(6, 6));
        searchField = new JTextField();
        searchBtn = new JButton("Search (Roll/Name)");
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        row.add(searchPanel);

        left.add(row);

        // Output log
        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 13));
        output.setBackground(new Color(245, 245, 245));
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createTitledBorder("Allocation Log"));
        scroll.setPreferredSize(new Dimension(340, 220));
        left.add(Box.createVerticalStrut(10));
        left.add(scroll);

        add(left, BorderLayout.WEST);

        // Right: Room grid visualization
        roomGridPanel = new JPanel();
        roomGridPanel.setBorder(BorderFactory.createTitledBorder("Rooms"));
        roomGridPanel.setLayout(new GridLayout(0, 6, 6, 6)); // will adjust rows automatically
        JScrollPane gridScroll = new JScrollPane(roomGridPanel);
        add(gridScroll, BorderLayout.CENTER);

        // Event: when hostel selection changes, update room-pref options and room grid
        hostelBox.addActionListener(e -> {
            updateRoomPreferences();
            refreshRoomGrid();
        });

        // Initialize pref & grid
        updateRoomPreferences();
        refreshRoomGrid();

        setVisible(true);
    }

    // Update room preference combobox according to selected hostel
    private void updateRoomPreferences() {
        String branchName = (String) hostelBox.getSelectedItem();
        roomPrefBox.removeAllItems();
        roomPrefBox.addItem("Any");
        HostelBranch hb = manager.getBranchByName(branchName);
        if (hb != null) {
            // gather unique room types
            Set<String> types = new LinkedHashSet<>();
            for (Room r : hb.getHostel().getRooms()) types.add(r.getType());
            for (String t : types) roomPrefBox.addItem(t);
        }
    }

    // Build room grid for selected hostel
    private void refreshRoomGrid() {
        roomGridPanel.removeAll();
        String branchName = (String) hostelBox.getSelectedItem();
        HostelBranch hb = manager.getBranchByName(branchName);
        if (hb == null) {
            roomGridPanel.revalidate();
            roomGridPanel.repaint();
            return;
        }
        List<Room> rooms = hb.getHostel().getRooms();

        // determine columns based on number of rooms (keep 6 columns)
        int cols = 6;
        int rows = (rooms.size() + cols - 1) / cols;
        roomGridPanel.setLayout(new GridLayout(rows, cols, 6, 6));

        for (Room r : rooms) {
            String label = "<html><b>" + r.getRoomId() + "</b><br/>" + r.getType() +
                    "<br/>" + r.getOccupants().size() + "/" + r.getCapacity() + "</html>";
            JButton btn = new JButton(label);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            // color coding
            if (r.getOccupants().isEmpty()) {
                btn.setBackground(new Color(46, 204, 113)); // green
            } else if (r.getOccupants().size() < r.getCapacity()) {
                btn.setBackground(new Color(241, 196, 15)); // yellow
            } else {
                btn.setBackground(new Color(231, 76, 60)); // red
            }
            btn.setOpaque(true);
            btn.setBorderPainted(false);

            // show occupant details on click
            btn.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Room: ").append(r.getRoomId()).append(" (").append(r.getType()).append(")\n");
                sb.append("Capacity: ").append(r.getCapacity()).append("\n");
                sb.append("Occupied: ").append(r.getOccupants().size()).append("\n\n");
                if (r.getOccupants().isEmpty()) {
                    sb.append("No occupants.");
                } else {
                    for (Student s : r.getOccupants()) {
                        sb.append(s.getName()).append(" - ").append(s.getRollNo()).append("\n");
                    }
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Room Info", JOptionPane.INFORMATION_MESSAGE);
            });

            roomGridPanel.add(btn);
        }

        // fill remaining cells (so grid looks neat)
        int totalCells = rows * cols;
        int need = totalCells - rooms.size();
        for (int i = 0; i < need; i++) {
            JPanel p = new JPanel();
            p.setBackground(Color.WHITE);
            roomGridPanel.add(p);
        }

        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    // Allocate with preference handling (Any = wildcard)
    private void allocate() {
        String branch = (String) hostelBox.getSelectedItem();
        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String pref = roomPrefBox.getSelectedItem() != null ? roomPrefBox.getSelectedItem().toString() : "Any";
        int year;

        if (branch == null) {
            JOptionPane.showMessageDialog(this, "Please select a hostel branch.");
            return;
        }
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ROLL NUMBER.");
            return;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter NAME.");
            return;
        }
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid YEAR (numeric).");
            return;
        }

        // Global duplicate check
        if (manager.isRollExistsGlobally(roll)) {
            JOptionPane.showMessageDialog(this, "Roll number already exists in the system! Allocation aborted.");
            return;
        }

        Student s = new Student(roll, name, gender, pref, year);

        // Add to branch
        boolean added = manager.addStudentToBranch(branch, s);
        if (!added) {
            JOptionPane.showMessageDialog(this, "Could not register student in selected hostel.");
            return;
        }

        // If pref == Any, we'll allow any room type. Otherwise, try the specific type.
        // Temporarily adjust student preference to "Any" to allocate in any type (we let Hostel.allocateRoom match type).
        // If pref is "Any", call allocate directly; if pref is specific, allocate as-is.
        Room allocated = manager.allocateInBranch(branch, s);

        // If specific preference was selected (not Any) but no room, we might try Any fallback (optional)
        if (allocated == null && !"Any".equalsIgnoreCase(pref)) {
            // Try fallback: temporarily set preference to any by trying to place in any room manually
            HostelBranch hb = manager.getBranchByName(branch);
            if (hb != null) {
                for (Room r : hb.getHostel().getRooms()) {
                    if (r.isAvailable()) {
                        r.occupy(s);
                        allocated = r;
                        break;
                    }
                }
            }
        }

        if (allocated != null) {
            output.append("✔ " + name + " (" + roll + ") allocated in " + branch + ": " +
                    allocated.getRoomId() + " (" + allocated.getType() + ")\n");
        } else {
            output.append("⚠ " + name + " (" + roll + ") added to waiting list of " + branch + ".\n");
        }

        // refresh UI
        refreshRoomGrid();

        // clear inputs
        rollField.setText("");
        nameField.setText("");
        yearField.setText("");
    }

    // Show detailed hostel status popup
    private void showHostelStatus() {
        String branch = (String) hostelBox.getSelectedItem();
        if (branch == null) {
            JOptionPane.showMessageDialog(this, "Please select a hostel first.");
            return;
        }
        HostelBranch hb = manager.getBranchByName(branch);
        if (hb == null) return;
        Hostel h = hb.getHostel();

        int totalBeds = 0;
        int filledBeds = 0;
        int totalRooms = h.getRooms().size();
        for (Room r : h.getRooms()) {
            totalBeds += r.getCapacity();
            filledBeds += r.getOccupants().size();
        }
        int waitingCount = h.getWaitingList().size();

        StringBuilder sb = new StringBuilder();
        sb.append("Hostel: ").append(branch).append("\n\n");
        sb.append("Total Rooms: ").append(totalRooms).append("\n");
        sb.append("Total Beds: ").append(totalBeds).append("\n");
        sb.append("Filled Beds: ").append(filledBeds).append("\n");
        sb.append("Vacant Beds: ").append(totalBeds - filledBeds).append("\n");
        sb.append("Waiting List: ").append(waitingCount).append("\n\n");
        sb.append("Room-wise detail:\n");
        sb.append(h.getStatus());

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(520, 420));

        JOptionPane.showMessageDialog(this, sp, "Hostel Status", JOptionPane.INFORMATION_MESSAGE);
    }

    // Search by roll or name
    private void performSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter roll number or name to search.");
            return;
        }

        // First try by roll exact match
        Map<String, Object> loc = manager.findStudentLocationByRoll(q);
        if (!loc.isEmpty()) {
            if (loc.containsKey("waiting") && Boolean.TRUE.equals(loc.get("waiting"))) {
                HostelBranch b = (HostelBranch) loc.get("branch");
                Student s = (Student) loc.get("student");
                JOptionPane.showMessageDialog(this, s.getName() + " (" + s.getRollNo() + ") is on waiting list of " + b.getName());
                return;
            } else if (loc.containsKey("room")) {
                HostelBranch b = (HostelBranch) loc.get("branch");
                Room r = (Room) loc.get("room");
                Student s = (Student) loc.get("student");
                JOptionPane.showMessageDialog(this, s.getName() + " (" + s.getRollNo() + ") is in " + b.getName() + " → " + r.getRoomId());
                return;
            }
        }

        // If not found by roll, search by name (partial)
        List<Map<String, Object>> matches = manager.findStudentByName(q);
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found for: " + q);
            return;
        }

        // Build result summary
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> m : matches) {
            HostelBranch b = (HostelBranch) m.get("branch");
            Student s = (Student) m.get("student");
            sb.append(s.getName()).append(" (").append(s.getRollNo()).append(") - ").append(b.getName());
            if (m.containsKey("waiting") && Boolean.TRUE.equals(m.get("waiting"))) {
                sb.append(" [Waiting]\n");
            } else if (m.containsKey("room")) {
                Room r = (Room) m.get("room");
                sb.append(" → ").append(r.getRoomId()).append("\n");
            } else {
                sb.append("\n");
            }
        }

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(420, 240));
        JOptionPane.showMessageDialog(this, sp, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
