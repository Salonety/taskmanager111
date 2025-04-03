# Interactive Task Manager with Jetpack Compose

## Objective
This project is an **Interactive Task Manager** built with **Jetpack Compose** to demonstrate advanced UI/UX skills in Android development. The app follows **Material Design 3** principles and leverages modern Android tools such as:
- **Android SDK 34/35**
- **Kotlin 2.x**
- **Jetpack Compose 1.6.x**

The app features smooth animations, intuitive navigation, and accessibility support while maintaining high performance and responsiveness across various screen sizes.

---

## Features
### 1. Core Functionality
- **Task Creation**: Users can add tasks with the following details:
  - Title (required)
  - Description (optional)
  - Priority (Low, Medium, High)
  - Due date (via a date picker)
- **Task List**:
  - Displays tasks dynamically with sorting options (priority, due date, alphabetical order)
  - Filtering by status (All, Completed, Pending)
- **Task Details**:
  - View task details
  - Mark tasks as completed
  - Delete tasks
- **Persistent Storage**:
  - Uses **Room Database** to store tasks persistently

### 2. UI/UX Design
- **Built entirely using Jetpack Compose**
- **Material Design 3 components**: TopAppBar, Cards, Floating Action Button (FAB), etc.
- **Responsive Layout**: Adapts to phone and tablet screens
- **Theming**:
  - Light and Dark mode support with **Material You** dynamic theming
  - Customizable primary color via **settings screen**
- **Animations**:
  - Slide-in/slide-out effects for task addition/removal
  - Circular reveal animation for the task details screen
  - Subtle bounce effect on FAB click
- **Navigation**:
  - **Jetpack Navigation Component**
  - Screens: Home, Task Creation, Task Details, Settings

### 3. Advanced UI Features
- **Drag-and-Drop**: Reordering tasks with haptic feedback
- **Swipe Gestures**:
  - Swipe-to-delete with undo option
  - Swipe-to-complete
- **Custom Progress Indicator**: Animated circular progress bar for completed tasks
- **Empty State UI**: Illustration + motivational message when no tasks exist

### 4. Accessibility
- Proper **content descriptions** for screen readers
- Support for **large text scaling** and **high-contrast mode**
- Keyboard navigation for task creation & list interaction

### 5. Performance Optimizations
- **Optimized LazyColumn** rendering for smooth scrolling with 100+ tasks
- **Efficient recomposition** with `remember` & `LaunchedEffect`
- **Shimmer loading effect** while Room data loads

### 6. Testing
- **Compose UI Tests**:
  - Task creation flow
  - Sorting & filtering
  - Animation verification
- **Screenshot Tests**:
  - Validate UI in light & dark mode using Robolectric

---

## Setup Instructions
### Prerequisites
- Android Studio **Giraffe | Hedgehog+**
- **Gradle 8.x**
- **Kotlin 2.x**

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/TaskManagerApp.git
   cd TaskManagerApp
   ```
2. Open the project in **Android Studio**
3. Sync Gradle and build the project
4. Run the app on an emulator or physical device

---

## Deliverables
- **Full Source Code** (Jetpack Compose, Android SDK 35/34, Kotlin 2.x)
- **README.md** (this file)
- **APK file** for testing

## Download APK
You can download the latest APK from [GitHub Releases](https://github.com/Salonety/taskmanager111/blob/master/TaskManager.apk).

---

## Evaluation Criteria
- **UI Quality**: Visually appealing, intuitive, and follows Material Design
- **UX**: Smooth, logical, and user-friendly interactions
- **Animations**: Polished and purposeful
- **Accessibility**: Adheres to Android accessibility standards
- **Code Quality**: Modular, efficient, well-documented with modern architecture
- **Testing**: Comprehensive UI tests

---

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Author:** Saloni Tyagi

