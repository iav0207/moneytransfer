# -*- coding: utf-8 -*-

import logging
import urllib.parse
from collections import defaultdict

import requests

base_url = 'http://localhost:8080'

log = logging.getLogger('api-demo-logger')
log.addHandler(logging.StreamHandler())
log.setLevel('INFO')

errors = []


def log_step(msg):
    log.info('\n' + msg)


def uri(path):
    return urllib.parse.urljoin(base_url, path)


def assert_that(msg, condition):
    if not condition:
        errors.append(msg)


def post(url, json):
    log.info(f'\tPOST : {url} : {json}')
    resp = requests.post(url, json=json)
    log.info(f'\tResponse: {resp.json()}')
    return resp


def get(url):
    log.info(f'\tGET : {url}')
    resp = requests.get(url=url)
    log.info(f'\tResponse: {resp.json()}')
    return resp


def body(resp):
    return resp.json()['body']


def status(resp):
    return resp.json()['status']


def micro(money):
    return money * 1e6


def run():
    log_step('Demo flow started.')

    accounts = defaultdict(list)
    usd = 840
    rub = 643

    log_step('Get list of supported currencies.')
    get(uri('currencies/list'))

    log_step('Open USD account.')
    resp = post(uri('accounts/open'), json={'currency': usd})
    accounts[usd].append(body(resp))

    log_step('Get just created account.')
    resp = get(uri(f'accounts?id={accounts[usd][0]["id"]}'))
    assert_that('Received account currency is USD', body(resp)['currencyCode'] == usd)

    log_step('Create another USD account.')
    resp = post(uri('accounts/open'), json={'currency': usd})
    assert_that('Account has different id', body(resp)['id'] != accounts[usd][0]['id'])
    accounts[usd].append(body(resp))

    log_step('Create RUB account.')
    resp = post(uri('accounts/open'), json={'currency': rub})
    assert_that('Received account currency is RUB', body(resp)['currencyCode'] == rub)
    accounts[rub].append(body(resp))

    log_step('Deposit 10 USD.')
    post(uri('money/deposit'), json={
        'accountId': accounts[usd][0]['id'],
        'amountMicros': micro(10)
    })

    log_step('Transfer 3 USD to another account.')
    resp = post(uri('money/transfer'), json={
        'senderAccountId': accounts[usd][0]['id'],
        'recipientAccountId': accounts[usd][1]['id'],
        'amountMicros': micro(3)
    })
    assert_that('Transfer response status was OK', status(resp) == 'OK')

    log_step('Check balances.')
    resp = get(uri(f'money/balance?accountId={accounts[usd][0]["id"]}'))
    assert_that('First account balance is 7 USD', body(resp) == micro(7))
    resp = get(uri(f'money/balance?accountId={accounts[usd][1]["id"]}'))
    assert_that('Second account balance is 3 USD', body(resp) == micro(3))

    log_step('Withdraw.')
    resp = post(uri('money/withdraw'), json={
        'accountId': accounts[usd][1]['id'],
        'amountMicros': micro(3)
    })
    assert_that('Withdraw response status was OK', status(resp) == 'OK')
    resp = get(uri(f'money/balance?accountId={accounts[usd][1]["id"]}'))
    assert_that('Balance changed to zero', body(resp) == 0)

    log_step('Close account.')
    resp = post(uri('accounts/close'), json={'accountId': accounts[usd][1]['id']})
    assert_that('Response status is OK', status(resp) == 'OK')

    assert not errors, 'Failed on assertions:\n\n' + '\n'.join(errors)

    log_step('Success.')


if __name__ == '__main__':
    run()

