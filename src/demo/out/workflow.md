```
Demo flow started.

Get list of supported currencies.
	GET : http://localhost:8080/currencies/list
	Response: {'status': 'OK', 'body': [{'numCode': 36, 'isoCode': 'AUD'}, {'numCode': 124, 'isoCode': 'CAD'}, {'numCode': 156, 'isoCode': 'CNY'}, {'numCode': 344, 'isoCode': 'HKD'}, {'numCode': 643, 'isoCode': 'RUB'}, {'numCode': 702, 'isoCode': 'SGD'}, {'numCode': 756, 'isoCode': 'CHF'}, {'numCode': 826, 'isoCode': 'GBP'}, {'numCode': 840, 'isoCode': 'USD'}, {'numCode': 978, 'isoCode': 'EUR'}]}

Open USD account.
	POST : http://localhost:8080/accounts/open : {'currency': 840}
	Response: {'status': 'OK', 'body': {'id': 10, 'currencyCode': 840, 'status': 'ACTIVE'}}

Get just created account.
	GET : http://localhost:8080/accounts?id=10
	Response: {'status': 'OK', 'body': {'id': 10, 'currencyCode': 840, 'status': 'ACTIVE'}}

Create another USD account.
	POST : http://localhost:8080/accounts/open : {'currency': 840}
	Response: {'status': 'OK', 'body': {'id': 11, 'currencyCode': 840, 'status': 'ACTIVE'}}

Create RUB account.
	POST : http://localhost:8080/accounts/open : {'currency': 643}
	Response: {'status': 'OK', 'body': {'id': 12, 'currencyCode': 643, 'status': 'ACTIVE'}}

Deposit 10 USD.
	POST : http://localhost:8080/money/deposit : {'accountId': 10, 'amountMicros': 10000000.0}
	Response: {'status': 'OK', 'body': {'type': 'DEPOSIT', 'recipient': 10, 'amount': 10000000, 'timestamp': 1519198496069}}

Transfer 3 USD to another account.
	POST : http://localhost:8080/money/transfer : {'senderAccountId': 10, 'recipientAccountId': 11, 'amountMicros': 3000000.0}
	Response: {'status': 'OK', 'body': {'type': 'TRANSFER', 'sender': 10, 'recipient': 11, 'amount': 3000000, 'timestamp': 1519198496079}}

Check balances.
	GET : http://localhost:8080/money/balance?accountId=10
	Response: {'status': 'OK', 'body': 7000000}
	GET : http://localhost:8080/money/balance?accountId=11
	Response: {'status': 'OK', 'body': 3000000}

Withdraw.
	POST : http://localhost:8080/money/withdraw : {'accountId': 11, 'amountMicros': 3000000.0}
	Response: {'status': 'OK', 'body': {'type': 'WITHDRAWAL', 'sender': 11, 'amount': 3000000, 'timestamp': 1519198496106}}
	GET : http://localhost:8080/money/balance?accountId=11
	Response: {'status': 'OK', 'body': 0}

Close account.
	POST : http://localhost:8080/accounts/close : {'accountId': 11}
	Response: {'status': 'OK', 'body': {'id': 11, 'currencyCode': 840, 'status': 'CLOSED'}}

Success.
```
