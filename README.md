# Table of Contents
=================

1. [Overview](#overview)
2. [Features](#features)
3. [Pre-requisites](#pre-requisites)
4. [External Dependencies](#external-dependencies)
5. [Running Application](#running-application)
6. [Contributors](#contributors)


# Overview

The project includes automated expense tracking, where group expenses are seamlessly integrated into personal finances, ensuring that users have an accurate overview of their financial standing. It also features a task management system, which simplifies task delegation and tracking within groups, making collaboration more efficient. Additionally, the platform provides real-time updates, giving users instant notifications on their finances and task progress. A centralized overview is available through a unified dashboard, allowing users to manage both group and personal finances, along with tasks, in one convenient location.

# Features
- Add expenses with details like payer, amount, and purpose.
- Automatically calculate individual shares.
- View each member's contributions and balances.

# Pre-requisites

For building and running the application locally, the project requires:
- Java [17.0.0](https://www.oracle.com/java/technologies/downloads/#java17)
- Apache Maven [3.9.7](https://dlcdn.apache.org/maven/maven-3/3.9.7/binaries/apache-maven-3.9.7-bin.zip)
- MySQL [8.0.29](https://dev.mysql.com/downloads/installer/)
- Neo4j [5.24.0](https://neo4j.com/download/)
- H2 Database [2.1.214](https://h2database.com/html/main.html)
- Spring Boot [3.3.4](https://spring.io/projects/spring-boot)
- ModelMapper [3.1.1](https://mvnrepository.com/artifact/org.modelmapper/modelmapper/3.1.1)
- JWT [0.11.5](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api/0.11.5)
- Lombok [1.18.24](https://projectlombok.org/)

# External Dependencies 

| Dependency Name               | Version  | Description                                                                                     |
|-------------------------------|----------|-------------------------------------------------------------------------------------------------|
| spring-boot-starter-data-jpa  | 3.3.4    | Starter for using Spring Data JPA with Hibernate                                                |
| spring-boot-starter-security  | 3.3.4    | Starter for using Spring Security                                                               |
| spring-boot-starter-web       | 3.3.4    | Starter for building web applications, including RESTful APIs using Spring MVC.                |
| mysql-connector-j             | 8.0.29   | MySQL Connector/J is a JDBC Type 4 driver that uses pure Java to access MySQL databases         |
| lombok                        | 1.18.24  | Lombok is a Java library that helps to reduce boilerplate code by using annotations              |
| spring-boot-starter-test      | 3.3.4    | Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest, and Mockito |
| spring-security-test          | 5.8.2    | Security related test utilities                                                                 |
| jjwt-api                      | 0.11.5   | JWT (JSON Web Token) API                                                                        |
| jjwt-impl                     | 0.11.5   | JWT (JSON Web Token) implementation for Java                                                    |
| jjwt-jackson                  | 0.11.5   | JWT (JSON Web Token) Jackson module for Java                                                    |
| spring-boot-starter-mail      | 3.3.4    | Starter for using Spring Framework's MailSender, which is used for sending email               |
| h2                            | 2.1.214  | H2 Database engine for testing in local environments                                           |

# Running Application

## Remotely
Prerequisite: Connect to dal wifi or use dal vpn.  
**URL**: http://csci5308-vm5.research.cs.dal.ca:81/ 

## Locally
- Clone Repository to your local machine
- Create a Database called “fundfusion” using MySQL workbench
- Edit the following variables in `backend/src/main/resources/application.yml`


## Setting up Back-end
 Using in-memory H2 database:
To start the backend with an in-memory H2 database, run the following command:
mvn spring-boot:run -Dspring-boot.run.profiles=local-mysql

## Setting up front-end
Run below command to run the front end.
  npm run dev -- --port 80
If this doesn’t work try using with sudo(for linux/Mac) 
sudo  npm run dev -- --port 80

## Contributors
- Devkumar Patel: dv943844@dal.ca 
- Malav Shah: ml677231@dal.ca
- Dhruvgiri goswami: dh890776@dal.ca
- Anupam chopra: an477120@dal.ca
- Sivarajesh Balamurali: sv997262@dal.ca
