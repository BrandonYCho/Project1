## Description
This project fetches the user's music data from Spotify's API when they log in. It stores their data into a Hive database and uses a command-line interface to interact and perform queries. The application is written in Scala with a fully function user management access system for both basic and admin users.

## Technologies Used
- Java - version 1.8.0_311
- Scala - version 2.12.15
- Spark - version 3.1.2
- Spark SQL - version 3.1.2
- Hive - version 3.1.2
- HDFS - version 3.3.0
- Git + GitHub

## Features
- Login and Signup
- Different access modes/menus for basic and admin users
- Admins can change admin status of basic users and delete basic users
- Users can change their username and password
- Passwords encrypted and verified using BCrypt
- Users can perform various queries on the data

## Usage
- You must have your own client key & client secret to interact with the Spotify API from https://developer.spotify.com
- I included a default database in this version to test queries
- A default admin account is created on intialization
  - Username: admin, Password: admin
- Log in using default admin account or create a new basic account
  - ![Login Menu](/images/login.png?!raw=true)  or  ![Create Basic User](/images/userCreation.png?!raw=true)
- The user will have a different menu depending on whether or not they have admin privileges
  - ![Admin Menu](/images/adminMenu.png?!raw=true)  or  ![Basic Menu](/images/basicMenu.png?!raw=true)
- Both types of users can change their username or password
  - ![Change Username](/images/changeUsername.png?!raw=true) or ![Change Password](/images/changePassword.png?!raw=true)
- Admin User Management Menu:
  - ![User Management](/images/userManagement.png?!raw=true)
- Admin can choose to delete a basic user
  - ![Delete User 1](/images/deleteUser_1.png?!raw=true)
  - ![Delete User 2](/images/deleteUser_2.png?!raw=true)
- Admin can also give a basic user admin privileges
  - ![Give Admin](/images/giveAdmin.png?!raw=true)
- Both types of users can access the query menu
  - ![Query Menu](/images/queryMenu.png?!raw=true)
- Query 1
  - ![Query 1](/images/query1.png?!raw=true)
- Query 2
  - ![Query 2](/images/query2.png?!raw=true)
- Query 3
  - ![Query 3](/images/query3.png?!raw=true)
- Query 4
  - ![Query 4 1](/images/query4_1.png?!raw=true)
  - ![Query 4 2](/images/query4_2.png?!raw=true)
- Query 5
  - ![Query 5](/images/query5.png?!raw=true)
- Query 6
  - ![Query 6](/images/query6.png?!raw=true)

# Original Project Guidelines
## Project 1
- Project 1 will be a Scala console application that is retrieving data using Hive or MapReduce. Your job is to build a real-time news analyzer. This application should allow users to view the trending topics (e.g. all trending topics for news related to "politics", "tv shows", "movies", "video games", or "sports" only [choose one topic for project]). Or it can analyze data on any topic of your choice.
- You must present a project proposal to trainer and be approved before proceeding with project. 

### MVP:
- ALL user interaction must come purely from the console application
- scrape data from datasets from an API based on your topic of choice
- Your console application must:
    - query data to answer at least 6 analysis questions of your choice
    - have a login system for all users with passwords
        - 2 types of users: BASIC and ADMIN
        - Users should also be able to update username and password
- implement all CRUD operations
- implement bucketing, and partitioning
- can use hive with screenshots but make as program in 
    IntelliJ or VSCode, too with appropriate dependencies

### Stretch Goals:
- Passwords must be encrypted
- Export all results into a JSON file
- find a trend

### Presentations
- You will be asked to run an analysis using the console application on the day of the presentation, so be prepared to do so.
- We'll have 5-10 minutes a piece, so make sure your presentation can be covered in that time, focusing on the parts of your analysis you find most interesting.

### Technologies
- Hadoop MapReduce (optional)
- YARN 
- HDFS
- Scala 2.12
- Hive
- Git + GitHub

### Due Date
- Presentations will take place on Wednesday, 3/30.
