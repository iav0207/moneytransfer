# -*- coding: utf-8 -*-

import logging
import os
import urllib.parse
from collections import defaultdict

import requests

config = {
    'base_url': 'http://localhost:8080'
}

log = logging.getLogger('test-api-logger')
log.addHandler(logging.StreamHandler())
log.setLevel('INFO')

log.info(f'pwd: {os.getcwd()}')

errors = []


def uri(path):
    return urllib.parse.urljoin(config['base_url'], path)


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


def run():
    log.info('\nDemo flow started.\n')

    accounts = defaultdict(list)
    usd = 840
    rub = 643

    log.info('Getting list of supported currencies...')

    get(uri('currencies/list'))

    log.info('Opening USD account...')

    resp = post(uri('accounts/open'), json={'currency': usd})
    accounts[usd].append(resp.json()['body'])

    log.info('Getting just created account...')

    resp = get(uri(f'accounts?id={accounts[usd][0]["id"]}'))
    assert_that('Received account currency is USD', resp.json()['body']['currencyCode'] == usd)

    log.info('Creating another USD account...')

    resp = post(uri('accounts/open'), json={'currency': usd})
    assert_that('Account has different id', resp.json()['body']['id'] != accounts[usd][0]['id'])
    accounts[usd].append(resp.json())

    log.info('Creating RUB account...')

    resp = post(uri('accounts/open'), json={'currency': rub})
    assert_that('Received account currency is RUB', resp.json()['body']['currencyCode'] == rub)
    accounts[rub].append(resp.json()['body'])

    assert not errors, '\n'.join(errors)

    log.info('\nSuccess.')


if __name__ == '__main__':
    run()

