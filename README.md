# MoneyApp
This is a solution to coding task, not a real app.

Description
---
Implementation of RESTful API for money transfers between accounts.

- Simplicity and optimization for speed of development were at the highest priority here.
- No authentication mechanism provided. Assuming it's an internal API.

Technical info
---
- The project is written in Dropwizard framework, which uses embedded
Jetty server, Jersey HTTP client, Jackson for serialization.
- No ORM framework was used. Data access implemented via JDBI / SQL Object interface.
- DBMS: in-memory H2 DB.
- Testing: TestNG + Mockito.

Short API description
---
Supported methods:
```
GET     /accounts           - get account info by id
POST    /accounts/open      - open a new account in specified currency
GET     /currencies/list    - get list of supported currencies (numeric and ISO codes)
GET     /money/balance      - get account balance
POST    /money/deposit      - deposit funds into account
POST    /money/transfer     - transfer money to another account
POST    /money/withdraw     - withdraw funds from account
```
For money representation in the API 'micro-money' is used,
e.g. `1.50` USD is represented by number `1_500_000` for USD account.

Run it!
---
1. Run `mvn clean install` to build the application
1. Start application with `java -jar target/money-transfer-1.0-SNAPSHOT.jar server config.yml`
1. To see an API workflow demo run `python3 src/demo/api_workflow_demo.py`.
Or see [script](src/demo/api_workflow_demo.py) and its [output example](https://paste.ee/p/zuUJm)
