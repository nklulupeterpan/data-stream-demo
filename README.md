# data-stream-demo

This programme has two main functions
1. generat dummy data to /resources/file.txt as programme input
2. read the input file and pair the two events. Then the data after transforming will be stored in database (HSQLDB).

After application starting, it will start a web server that providing two APIs and a HSQLDB server. 
Steps:
1. go to: http://localhost:8080/api/generate to generate the input file
2. Then visit link: http://localhost:8080/api/stream for processing data.

To view the data in DB, please find a DB tool such as DBVisulise by URL: jdbc:hsqldb:hsql://localhost:9001/testdb
user name: sa
please leave the password empty

Please do not repeat run stream API to avoid insert dupilicat primary key into database.

