# Java Concepts & Language Architecture

The **Smart Complaint Tracker** utilizes pure core Java (Java 21) without external libraries or frameworks.

## 1. Object-Oriented Programming (OOP)
- **Inheritance & Polymorphism**: `User` acts as an abstract base class inherited by `Customer` and `Staff`, allowing unified authentication flows in `AuthenticationService` and polymorphic dispatch.
- **Encapsulation**: Domain models (`Complaint`, `Department`, `Assignment`, `ComplaintHistory`) maintain strict encapsulation with validated getters, setters, and defensive copies.
- **Abstraction**: High-level services interact with interfaces (`Repository<T, ID>`, `SearchAlgorithm`, `SortAlgorithm`, `FlowAlgorithm`, `DPAlgorithm`) ensuring loose coupling.

## 2. Generics & Custom Data Structures
- Core data structures are implemented from scratch using Java Generics:
  - `CustomArrayList<T>`
  - `CustomLinkedList<T>`
  - `CustomStack<T>`
  - `CustomQueue<T>`
  - `CustomPriorityQueue<T>` (Binary Heap)
  - `CustomHashTable<K, V>` (Separate chaining with dynamic rehashing)
  - `CustomGraph<V>` (Adjacency list)
  - `CustomDisjointSetUnion<T>` (Union-Find with path compression)

## 3. Java Concurrency & ForkJoin Framework
- Uses `java.util.concurrent.ForkJoinPool`, `RecursiveTask<T>`, and `RecursiveAction` to implement:
  - `ParallelReduction`: Divides array segments to compute aggregate resolution time metrics in $O(\log N)$ span.
  - `ParallelPrefixSum`: Two-pass parallel tree scan algorithm.
  - `ParallelMergeSort`: Parallel divide-and-conquer mergesort.

## 4. Java Swing Desktop GUI
- Clean desktop user interfaces built with Java Swing:
  - `JFrame`, `JDialog`, `JPanel`, `JTable`, `JTabbedPane`, `JScrollPane`, `JButton`, `JTextField`, `JPasswordField`, `JComboBox`.
  - Custom cell renderers (`DefaultTableCellRenderer`) for glowing status badges and priority tags.
  - Interactive modal dialogs and responsive tab navigation.

## 5. Security & Cryptography
- `PasswordHasher`: Employs Java standard `java.security.MessageDigest` for salted SHA-256 password hashing.
- Role-Based Access Control (RBAC) enforced across `CUSTOMER`, `STAFF`, and `ORGANIZATION_ADMIN`.
