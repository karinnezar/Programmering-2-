# PathFinder – Java

Project Description

PathFinder is a Java-based graphical application for creating, managing and exploring maps using graph data structures.

The application allows users to add locations to a map, create connections between them, and find routes between selected locations. Each connection can have a name and a travel time, allowing the application to calculate the total travel time for a route.

Features

Add and manage locations on a map

Create connections between locations with names and travel times

Select two locations and find a path between them

Calculate the total travel time between locations

View and modify connection details

Save and load map data

Export maps as image files

Load different map backgrounds

Technologies & Concepts

Java

JavaFX

Maven

Object-Oriented Programming (OOP)

Graph data structures

Graph traversal and pathfinding

Event handling

File handling and data persistence

Graphical User Interface (GUI)

Project Structure

backend/ – Graph interfaces, graph implementation and edge management

gui/ – Graphical interface and user interaction

assets/ – Project images and resources

Build and Run

The project uses Maven. To build and test the application, run:

./mvnw package

Java 21 is required.
