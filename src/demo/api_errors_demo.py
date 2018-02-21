# -*- coding: utf-8 -*-

import logging
import urllib.parse
from collections import defaultdict

import requests

base_url = 'http://localhost:8080'

log = logging.getLogger('api-err-demo-logger')
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


def body(resp):
    return resp.json()['body']


def status(resp):
    return resp.json()['status']


def assert_error(resp):
    if status(resp) != 'ERROR':
        errors.append('Error expected, but received: ' + resp.json())


def assert_bad_request(resp):
    if resp.status_code != 400:
        errors.append('Expected response code 400, got ' + resp.status_code)


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


def micro(money):
    return money * 1e6


def run():
    log_step('Errors demo started.')

    accounts = defaultdict(list)
    usd = 840
    rub = 643

    log_step('Try to open account in an unknown currency.')
    assert_error(post(uri('accounts/open'), json={'currency': 1010}))

    log_step('Request schema violation.')
    resp = post(uri('accounts/open'), json={'code': usd})
    assert_that('Bad request code received', resp.status_code == 400)

    log_step('Open USD account.')

    resp = post(uri('accounts/open'), json={'currency': usd})
    accounts[usd].append(body(resp))

    log_step('Open another USD account.')
    resp = post(uri('accounts/open'), json={'currency': usd})
    assert_that('Account has different id', body(resp)['id'] != accounts[usd][0]['id'])
    accounts[usd].append(body(resp))

    log_step('Open RUB account.')

    resp = post(uri('accounts/open'), json={'currency': rub})
    assert_that('Received account currency is RUB', body(resp)['currencyCode'] == rub)
    accounts[rub].append(body(resp))

    log_step('Try to deposit negative value.')
    post(uri('money/deposit'), json={
        'accountId': accounts[usd][0]['id'],
        'amountMicros': micro(-10)
    })

    log_step('Deposit 10 USD.')
    post(uri('money/deposit'), json={
        'accountId': accounts[usd][0]['id'],
        'amountMicros': micro(10)
    })

    log_step('Try to send more that one has got.')
    assert_error(post(uri('money/transfer'), json={
        'senderAccountId': accounts[usd][0]['id'],
        'recipientAccountId': accounts[usd][1]['id'],
        'amountMicros': micro(30)
    }))

    log_step('Try to send money to an account of another currency.')
    assert_error(post(uri('money/transfer'), json={
        'senderAccountId': accounts[usd][0]['id'],
        'recipientAccountId': accounts[rub][0]['id'],
        'amountMicros': micro(5)
    }))

    log_step('Try to close account when its balance is non-zero')
    assert_error(post(uri('accounts/close'), json={'accountId': accounts[usd][0]['id']}))

    log_step('Try to withdraw more than one has got.')
    assert_error(post(uri('money/withdraw'), json={
        'accountId': accounts[usd][0]['id'],
        'amountMicros': micro(11)
    }))

    log_step('Try to send money to a closed account.')
    post(uri('accounts/close'), json={'accountId': accounts[usd][1]['id']})
    assert_error(post(uri('money/transfer'), json={
        'senderAccountId': accounts[usd][0]['id'],
        'recipientAccountId': accounts[usd][1]['id'],
        'amountMicros': micro(5)
    }))

    assert not errors, 'Failed on assertions:\n\n' + '\n'.join(errors)

    log_step('Success.')


if __name__ == '__main__':
    run()

