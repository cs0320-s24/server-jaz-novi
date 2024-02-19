> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
Project name: Server Sprint
Team Members:
Jazlyn(jlin223): User Story 2 and User Story 2 testing 
Zhinuo(zwang571): User Story 1&3 and corresponding testing
Check out [GitHub](https://github.com/cs0320-s24/server-jaz-novi) for more information.
# Design Choices
The server has multiple handlers for different routes. For loadcsv,viewcsv,searchcsv, there is a CSV Shared Var to make the updated csv file status and related things the same to all handlers. For Caching broadband, there is a ACS Query class to hold the query parameter. The ACS Searcher defines the default search method(making API calls) and the CachedACSInfo create cache for each unique ACS Query.
# Errors/Bugs
Nothing I know of 
# Tests
Test Cache suite tests if the query really hits cache and if there is cache evition when it reaches customized cache limit.
TestCSVHandlers suite checks if each csv handler handles error input and valid input.
# How to
## loadcsv, which loads a CSV file if one is located at the specified path. 
Requires a filepath parameter and an optional headerFlag parameter:
http://localhost:3232/loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true

## viewcsv, which sends back the entire CSV file's contents as a Json 2-dimensional array.
http://localhost:3232/viewcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true

## searchcsv, which sends back rows matching the given search criteria.
Requires val(targeted search value),optional col (potential search collum) if the multiFlag Provided is false
Requires queries formatted as `Or(and(val_col),and(val1_col1))`

http://localhost:3232/searchcsv?col=City/Town&val=Providence

## broadband, which sends back the broadband data from the ACS described above.
Requires statename and county names and at least one variablename in the parameter called variablenames

http://localhost:3232/broadband?county=Providence%20County&state=Rhode%20Island&variables=SUMLEVEL

## Error Handling
- error_bad_request (missing county)
http://localhost:3232/broadband?state=Rhode%20Island

- error_datasource
1. The requested variable doesn't exist
http://localhost:3232/broadband?county=Providence%20County&state=Rhode%20Island&variables=DP02_0126E

2. The file to be loaded doesn't exist
http://localhost:3232/loadcsv?filepath=data/server-data/city.csv&headerFlag=true

