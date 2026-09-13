# SMART COMPLAINT DSA TRACKER (Full Algorithmic Engine)
### Clean, Compact & Feature-Complete Java Data Structures & Algorithms Project

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%26%20Modular-brightgreen.svg)
![UI](https://img.shields.io/badge/GUI-Modern%20Swing%20+%20CLI-blue.svg)
![Tests](https://img.shields.io/badge/Master%20Tests-13%2F13%20Passed-success.svg)

---

## 📌 Repository Overview

This repository houses the **Smart Complaint Tracker** academic DSA project along with coursework implementations:

```
Complaint-tracker-/
├── project/                               # Main Complaint Tracker System
│   ├── src/                              # Source code (MVC + DSA Architecture)
│   │   ├── algorithms/                   # Exact, fuzzy, graph, flow, string algorithms
│   │   ├── datastructures/               # Custom heap, trie, graph, hash table, DSU
│   │   ├── gui/                          # Modern Swing GUI (Customer & Org portals)
│   │   ├── model/                        # Domain models (Complaint, SLA, User, etc.)
│   │   ├── repository/                   # Data repositories
│   │   ├── services/                     # Business logic & algorithm engines
│   │   ├── tests/                        # Comprehensive test suite (13/13 tests)
│   │   └── Main.java                     # Application entry point
│   ├── data/                             # Persistent file storage
│   ├── docs/                             # Documentation, concept guides, UML diagrams
│   ├── build.bat                         # Compilation script
│   ├── run.bat                           # Launch application script
│   ├── Complaint_Tracker .pptx           # Project presentation slides
│   ├── Team No_X_DSA-3_Project Abstract.docx # Formal project abstract
│   └── README.md                         # Project documentation
│
└── class codes/                          # Coursework exercises & algorithm practice
    ├── BinarySearch.java
    ├── FileKeywordSearch.java
    ├── ReadTextFile.java
    └── SearchKey.java
```

---

## 📌 1. Concrete Functional Role of Every Algorithm

In this project, every single algorithm has a dedicated, real-world operational role:

| Algorithm Category | Algorithm Name | Functional Role in the Application |
|---|---|---|
| **Priority Queue** | **Binary Max-Heap** | **Dynamic Priority Triage**: Ranks tickets by dynamic urgency score ($S = \text{Priority} \times 25 + \text{Affected} \times 0.8 + \text{Overdue Boost}$). |
| **Priority Queue** | **Binary Min-Heap (EDF)** | **SLA Deadline Monitor**: Tracks Earliest Deadline First (EDF) and alerts for approaching or overdue SLA breaches. |
| **Exact Matching** | **Knuth-Morris-Pratt (KMP)** | **Deterministic Search**: $O(N + M)$ substring search with zero backtracking using LPS table. |
| **Exact Matching** | **Boyer-Moore-Horspool** | **Sublinear Log Search**: $O(N / M)$ average-case search scanning right-to-left with Bad Character table. |
| **Exact Matching** | **Rabin-Karp Rolling Hash** | **Multi-Pattern Hash Search**: Polynomial rolling hash ($10^9+7$) for fast phrase hashing. |
| **Exact Matching** | **Z-Algorithm** | **Linear Prefix Matcher**: Builds $Z$-array on $S = P + \$ + T$ for linear exact search. |
| **Multi-Pattern** | **Aho-Corasick Automaton** | **Category AI & Threat Escalation**: Auto-tags ticket categories and detects critical hazards (`"spark"`, `"fire"`, `"short circuit"`, `"gas leak"`) to auto-escalate priority to `CRITICAL`. |
| **Fuzzy Matching** | **Damerau-Levenshtein** | **Transposition Duplicate Blocker**: Catches character swaps (e.g. `teh` $\leftrightarrow$ `the`, `wi-fi` $\leftrightarrow$ `wifi`, `lekaage` $\leftrightarrow$ `leakage`) to prevent duplicate tickets. |
| **Fuzzy Matching** | **Wagner-Fischer Levenshtein** | **Typo Auto-Corrector ("Did you mean?")**: Analyzes edit distance to suggest corrected search terms when queries have typos. |
| **Fuzzy Matching** | **Jaro-Winkler Metric** | **Title & Prefix Matcher**: Ranks complaints based on common prefixes and short identifier similarity. |
| **Fuzzy Matching** | **N-Gram (Tri-Gram) Jaccard** | **Unstructured Topic Clustering**: Evaluates sub-word n-gram overlap to cluster complaints describing the same incident with different words. |
| **Phonetic Matching** | **Soundex Phonetic** | **Complainant History Tracking**: Groups complaints by user pronunciation codes ($O(N)$), matching names regardless of spelling differences (`Sharma` $\leftrightarrow$ `Scharma`, `Aarav` $\leftrightarrow$ `Arav`). |
| **Approximate Matching**| **Bitap (Shift-Or)** | **Approximate Fault Signature Scanner**: Bit-parallel search inside text streams with up to $k$ errors. |
| **Greedy Bipartite** | **Skill-Matching Engine** | **Technician Staff Dispatch**: Matches unassigned complaints to available technicians based on specialization and efficiency. |

---

## 🚀 2. Quick Start & Execution

### Option A: Launch GUI in Command Prompt (CMD)
```cmd
cd project
run.bat
```

### Option B: Launch GUI in PowerShell
```powershell
cd project
java -cp bin Main
```

### Option C: Rebuild Project from Source
```cmd
cd project
build.bat
```

### Option D: Run Master Test Suite (13/13 Tests Passed)
```powershell
cd project
java -cp bin tests.ComprehensiveTestSuite
```

---

## 📄 License & Academic Integrity

This project is created for academic evaluation as part of the **Data Structures and Algorithms** course.
