# Design Patterns in Smart Complaint Tracker

## 1. Strategy Pattern
- **Used in**: String Searching (`SearchAlgorithm`) and Work Scheduling (`SchedulingStrategy`).
- **Structure**:
  ```
               SearchAlgorithm (Interface)
                     |
       +-------------+-------------+-------------+
       |             |             |             |
  KMPAlgorithm   ZAlgorithm   RabinKarp     NaiveSearch
  ```
- **Benefit**: Allows the `SearchEngineService` to evaluate problem characteristics (CO1) and switch search strategies dynamically at runtime without modifying the caller GUI.

---

## 2. Repository Pattern
- **Used in**: `ComplaintRepository`, `CustomerRepository`, `StaffRepository`, `DepartmentRepository`, `HistoryRepository`, `NotificationRepository`, `FeedbackRepository`.
- **Benefit**: Decouples in-memory business logic and fast lookup indices from low-level file I/O storage. Repositories maintain active hash maps and binary heaps while writing back to flat files.

---

## 3. Observer Pattern
- **Used in**: `NotificationManager` and `NotificationListener`.
- **Benefit**: Enables event-driven notifications. When a complaint is submitted, assigned, escalated, or responded to, observers receive updates without tight coupling between components.

---

## 4. Factory / Singleton Pattern
- **Used in**: Service and Repository singletons (`getInstance()`) and ID generation (`IdGenerator`).
- **Benefit**: Ensures single point of synchronization for in-memory index caches and file access across threads.

---

## 5. MVC (Model-View-Controller) Architecture
- **Model**: `model.*` (Domain entities: `Complaint`, `Staff`, `Department`, `History`)
- **View**: `gui.customer.*`, `gui.organization.*`, `gui.common.*` (Java Swing UI)
- **Controller / Service**: `services.*` (`ComplaintService`, `SearchEngineService`, `AssignmentEngineService`, `AnalyticsService`)
