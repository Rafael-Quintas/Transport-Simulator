# 🗺️ Transport-Simulator

An interactive Java-based application designed to represent and explore integrated transport systems. Using a graph-based data model, the simulator allows users to visualize urban networks, analyze metrics, and find optimized paths between stops based on distance, duration, or environmental sustainability.

## 👥 The Team
This project was developed as part of the **Advanced Programming (PA)** course by:
* Rafael Quintas 
* Rafael Pato 
* Guilherme Pereira

## 📦 Technologies

* **Java** (Core logic and OOP) 
* **JavaFX** (User Interface) 
* **JavaFXSmartGraph** (Interactive graph visualization)
* **JUnit** (Unit testing for model validation) 
* **Maven** (Project management and build system)

## 🧩 Features

Here's what you can do with Transport-Simulator:

* **Interactive Network Map:** Visualize stops as vertices and routes as edges using an interactive graphical interface.
* **Advanced Pathfinding:** Calculate the "Least Cost Path" between two stops using criteria like Distance, Duration, or Sustainability.
* **Multi-Modal Transport:** Filter and analyze routes for different transport types, including Bus, Train, Boat, Walk, and Bicycle.
* **Real-Time Metrics:** Access live data about the network, such as the total number of stops, isolated stops, and available routes by transport type.
* **Dynamic Route Management:** Deactivate specific routes or transport means to see how it affects the overall network connectivity.
* **Undo System:** Revert changes made to the network, such as route deactivations or duration modifications, using the Memento pattern.

## 🖱️ User Interactions

Speed up your interaction with these controls:

* **Stop Double-Click:** View detailed information about a specific Stop, including its unique code, name, and GPS coordinates.
* **Route Double-Click:** Open a detailed view of all transport means between two stops to manage their state or update bicycle durations.
* **Custom Path Mode:** Manually select a sequence of stops to calculate total costs in real-time for a personalized journey.
* **Top 5 Centrality:** Generate a bar chart showing the most connected stops in the network.


## 🧛 The Process

We started by establishing the core data structure using a **Graph ADT**, where we modeled transport hubs as vertices and their connections as edges. Once the logic for importing data from the dataset was solid, we focused on the visual representation using the **JavaFXSmartGraph** library.

Next, we implemented the **Model-View-Controller (MVC)** pattern to ensure a clean separation between the business logic (`TransportMap`) and the interactive UI (`MapView`). This was crucial for maintaining code clarity and ensuring that the View only consulted the Model without manipulating it directly.

To make the simulator more robust, we integrated the **Bellman-Ford algorithm** for pathfinding. This choice was essential to handle different optimization criteria, especially sustainability costs, which can have negative values (beneficial to the environment).

Finally, we added the **Memento Pattern** to handle the state of the network. This feature allows the system to save and restore the graph state, providing a functional "Undo" for user actions like disabling routes.


## 🎓 What I Learned

During this project, I've picked up important skills and a better understanding of complex ideas, which improved my logical thinking.

### 🧠 Design Patterns:
* **MVC Pattern:** Learning to decouple the data from the interface was a game-changer for organizing the project and allowing the Controller to sync user actions with Model changes.
* **Strategy Pattern:** I used this to encapsulate different cost calculation algorithms, making the pathfinding criteria (Distance, Duration, Sustainability) interchangeable and easy to read.
* **Memento Pattern:** Implementing a "deep copy" of the graph state taught me how to manage application state effectively without violating encapsulation.

### 🛠️ Refactoring & Clean Code:
* **Magic Numbers:** I learned to replace hardcoded values with symbolic constants, especially during CSV parsing, which made the code much more maintainable.
* **Long Method Extraction:** Refactoring complex algorithms into smaller, atomic methods improved the overall readability and testability of the logic.

### 🧮 Algorithms & Data Structures:
* **Graph Theory:** Deepened my knowledge of incident edges, vertex degrees (centrality), and BFS-based searches for stops at specific distances.
* **Shortest Path Challenges:** Working with Bellman-Ford helped me understand how to handle edge weights and detect negative cycles in a real-world context.

## 📈 Overall Growth

Each part of this project helped me understand more about building professional Java applications, managing complex information, and improving user experience. It was more than just making a tool; it was about solving architectural problems and learning how to apply theoretical software patterns to a functional, interactive system.

## 🚀 How to Run

### Prerequisites
* **Java JDK 17** or higher.
* **Maven** installed.

### Steps
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/your-username/Transport-Simulator.git](https://github.com/your-username/Transport-Simulator.git)

2. **Navigate to the project folder:**
   ```bash
   cd Transport-Simulator

3. **Build the project:**
   ```bash
   mvn clean install

4. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="pt.pa.Main"