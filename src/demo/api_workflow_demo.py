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


def uri(path):
    return urllib.parse.urljoin(base_url, path)


def assert_that(msg, condition):
    if not condition:
        errors.append(msg)


def post(url, json):
    log.info(f'\tPOST : {url} : {json}')
    resp = requests.post(url, json=json)
    log.info(f'\tResponse: {resp.json()}')
    assert_that('Response received', resp.json())
    return resp


def get(url):
    log.info(f'\tGET : {url}')
    resp = requests.get(url=url)
    log.info(f'\tResponse: {resp.json()}')
    assert_that('Response received', resp)
    return resp


def body(resp):
    return resp.json()['body']


def status(resp):
    return resp.json()['status']


def micro(money):
    return money * 1e6


def run():
    log.info('\nDemo flow started.\n')

    accounts = defaultdict(list)
    usd = 840
    rub = 643

    log.info('Get list of supported currencies.')

    get(uri('currencies/list'))

    log.info('Open USD account.')

    resp = post(uri('accounts/open'), json={'currency': usd})
    accounts[usd].append(body(resp))

    log.info('Get just created account.')

    resp = get(uri(f'accounts?id={accounts[usd][0]["id"]}'))
    assert_that('Received account currency is USD', body(resp)['currencyCode'] == usd)

    log.info('Create another USD account.')

    resp = post(uri('accounts/open'), json={'currency': usd})
    assert_that('Account has different id', body(resp)['id'] != accounts[usd][0]['id'])
    accounts[usd].append(body(resp))

    log.info('Create RUB account.')

    resp = post(uri('accounts/open'), json={'currency': rub})
    assert_that('Received account currency is RUB', body(resp)['currencyCode'] == rub)
    accounts[rub].append(body(resp))

    log.info('Deposit 10 USD.')

    post(uri('money/deposit'), json={
        'accountId': accounts[usd][0]['id'],
        'amountMicros': micro(10)
    })

    log.info('Transfer 3 USD to another account.')

    resp = post(uri('money/transfer'), json={
        'senderAccountId': accounts[usd][0]['id'],
        'recipientAccountId': accounts[usd][1]['id'],
        'amountMicros': micro(3)
    })
    assert_that('Transfer response status was OK', status(resp) == 'OK')

    log.info('Check balances.')

    resp = get(uri(f'money/balance?accountId={accounts[usd][0]["id"]}'))
    assert_that('First account balance is 7 USD', body(resp) == micro(7))

    resp = get(uri(f'money/balance?accountId={accounts[usd][1]["id"]}'))
    assert_that('Second account balance is 3 USD', body(resp) == micro(3))

    log.info('Withdraw.')

    resp = post(uri('money/withdraw'), json={
        'accountId': accounts[usd][1]['id'],
        'amountMicros': micro(3)
    })
    assert_that('Withdraw response status was OK', status(resp) == 'OK')
    resp = get(uri(f'money/balance?accountId={accounts[usd][1]["id"]}'))
    assert_that('Balance changed to zero', body(resp) == 0)

    assert not errors, 'Failed on assertions:\n\n' + '\n'.join(errors)

    log.info('\nSuccess.')


if __name__ == '__main__':
    run()

