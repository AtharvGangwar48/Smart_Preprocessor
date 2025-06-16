# Smart Data Pre-processor

## Overview
The **Smart Data Pre-processor** is a web-based tool designed to simplify the process of cleaning and visualizing datasets for Machine Learning (ML) and Deep Learning (DL) models. This application allows users to upload CSV files, and automate data cleaning without requiring any programming skills.

## Table of Contents
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

## Features
- **No-Code Interface**: Users can clean and visualize datasets without writing any code.
- **Data Cleaning**: Automated handling of missing values, outlier removal, and data normalization.
- **CSV Upload Support**: Users can easily upload CSV files for processing.

## Technology Stack
- **Frontend**: HTML & CSS
- **Backend**: Java (Spring Boot)
- **Database**: MongoDB
- **Processing Libraries**: Apache Commons CSV

## Installation
To set up the project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/atharvgangwar48/smart-data-preprocessor.git
   cd smart-data-preprocessor
   ```
   
2. **Install dependencies (if using Maven)**:
   ```bash
     mvn install
     ```

4. **Run the Application**:
   - Start the backend server:
     ```bash
     mvn spring-boot:run
     ```

## Usage
1. Open your web browser and navigate to `http://localhost:8080`.
2. Upload a CSV file using the provided interface.
3. Select the desired data cleaning options.
4. Visualize the cleaned data using interactive charts.

## Project Structure
```
smart-data-preprocessor/
├── backend/                 # Java Spring Boot backend
│   ├── src/                 # Source files
|   |── resources/           # HTML Templates
│   └── pom.xml              # Backend dependencies
└── README.md                # Project documentation
```

## Contributing
We welcome contributions! Please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.

---

Thank you for checking out the Smart Data Pre-processor! We hope it makes your data cleaning and visualization tasks easier. If you have any questions or feedback, feel free to reach out!
