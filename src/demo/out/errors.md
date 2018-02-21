```
Errors demo started.

Try to open account in an unknown currency.
	POST : http://localhost:8080/accounts/open : {'currency': 1010}
	Response: {'status': 'ERROR', 'errorCode': 8000, 'message': 'Currency (code=1010) is not supported.'}

Request schema violation.
	POST : http://localhost:8080/accounts/open : {'code': 840}
	Response: {'code': 400, 'message': 'Unable to process JSON'}

Open USD account.
	POST : http://localhost:8080/accounts/open : {'currency': 840}
	Response: {'status': 'OK', 'body': {'id': 19, 'currencyCode': 840, 'status': 'ACTIVE'}}

Open another USD account.
	POST : http://localhost:8080/accounts/open : {'currency': 840}
	Response: {'status': 'OK', 'body': {'id': 20, 'currencyCode': 840, 'status': 'ACTIVE'}}

Open RUB account.
	POST : http://localhost:8080/accounts/open : {'currency': 643}
	Response: {'status': 'OK', 'body': {'id': 21, 'currencyCode': 643, 'status': 'ACTIVE'}}

Try to deposit negative value.
	POST : http://localhost:8080/money/deposit : {'accountId': 19, 'amountMicros': -10000000.0}
	Response: {'errors': ['amountMicros must be greater than or equal to 1']}

Deposit 10 USD.
	POST : http://localhost:8080/money/deposit : {'accountId': 19, 'amountMicros': 10000000.0}
	Response: {'status': 'OK', 'body': {'type': 'DEPOSIT', 'recipient': 19, 'amount': 10000000, 'timestamp': 1519200776202}}

Try to send more that one has got.
	POST : http://localhost:8080/money/transfer : {'senderAccountId': 19, 'recipientAccountId': 20, 'amountMicros': 30000000.0}
	Response: {'status': 'ERROR', 'errorCode': 6000, 'message': 'Not enough funds to withdraw 30000000 micros from account 19.'}

Try to send money to an account of another currency.
	POST : http://localhost:8080/money/transfer : {'senderAccountId': 19, 'recipientAccountId': 21, 'amountMicros': 5000000.0}
	Response: {'status': 'ERROR', 'errorCode': 6002, 'message': 'Accounts have different currencies'}

Try to close account when its balance is non-zero
	POST : http://localhost:8080/accounts/close : {'accountId': 19}
	Response: {'status': 'ERROR', 'errorCode': 6002, 'message': 'Account balance is non-zero. Withdraw first.'}

Try to withdraw more than one has got.
	POST : http://localhost:8080/money/withdraw : {'accountId': 19, 'amountMicros': 11000000.0}
	Response: {'status': 'ERROR', 'errorCode': 6000, 'message': 'Not enough funds to withdraw 11000000 micros from account 19.'}

Try to send money to a closed account.
	POST : http://localhost:8080/accounts/close : {'accountId': 20}
	Response: {'status': 'OK', 'body': {'id': 20, 'currencyCode': 840, 'status': 'CLOSED'}}
	POST : http://localhost:8080/money/transfer : {'senderAccountId': 19, 'recipientAccountId': 20, 'amountMicros': 5000000.0}
	Response: {'status': 'ERROR', 'errorCode': 6002, 'message': 'Account 20 is CLOSED.'}

Success.
```
