# File Handling & Persistence Architecture

## Zero-Database Flat File Architecture

All persistent information in **Smart Complaint Tracker** is stored directly in physical flat text files inside the `data/` directory.

```
data/
├── complaints.txt         # All registered complaints with full metadata
├── customers.txt          # Registered customer accounts and profiles
├── staff.txt              # Staff directory, workloads, and specializations
├── departments.txt        # Department capacity, SLA rules, and hierarchy
├── complaint_history.txt  # Immutable audit trail of every status/priority change
├── responses.txt          # Public messages and internal staff notes
├── assignments.txt        # Allocation logs connecting complaints to staff
├── feedback.txt           # Customer satisfaction ratings (1-5 stars) and comments
├── notifications.txt      # Internal notification broker messages
└── users.txt              # Base user credentials
```

---

## 1. Java File Handling APIs Used
- `java.io.File`: Directory validation, file existence, length, and timestamp checks.
- `java.io.FileReader` / `java.io.BufferedReader`: Buffered line-by-line reading for streaming record processing.
- `java.io.FileWriter` / `java.io.BufferedWriter`: Fast buffered disk writes and appends.
- `java.io.FileInputStream` / `java.io.FileOutputStream`: Byte stream processing.
- `java.nio.file.Files`: Atomic file replacement and directory backup copies.

---

## 2. Robustness & Fault Tolerance
1. **Atomic File Writes**: When updating entire datasets (e.g. `ComplaintFileHandler.saveAll()`), data is first written to a temporary file (`complaints.txt.tmp`). Once safely written and flushed, the temporary file replaces the main file atomically.
2. **Missing Files**: If a file is missing, the file handler returns an empty collection gracefully and creates the file on first write without throwing unhandled exceptions.
3. **Corrupted Records**: The parser wraps individual record parsing in defensive try-catch blocks. If a line is corrupted or malformed, it is skipped without crashing the system or corrupting valid records.
4. **Delimited Escaping**: String fields are sanitized using character escaping (`\t` -> `\\t`, `\n` -> `\\n`) so multiline complaint descriptions never break record boundaries.

---

## 3. Automated Backup & Recovery
- [`BackupManager`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/filehandling/BackupManager.java) provides snapshot backups to `backup/backup_YYYYMMDD_HHMMSS/`.
- Administrators can view backup history, timestamp, and file count, and restore any previous snapshot directly from the **File Management** interface.
