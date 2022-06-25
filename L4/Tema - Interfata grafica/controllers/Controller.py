from json import JSONDecoder

import requests
from flask import Flask, make_response, render_template, request, redirect
from functional import seq

app = Flask(__name__)

rest = 'http://127.0.0.1:5000'


def __check_login():
    session_id = request.cookies.get('session_id')
    if session_id is None:
        return None
    elif requests.get(f'{rest}/check-login', json={'session_id': session_id}).json()['logged'] == 'false':
        return None
    else:
        return session_id


@app.route('/')
def index():
    session_id = __check_login()
    if session_id is None:
        return render_template('not_logged.html')
    else:
        rest_response = requests.get(f'{rest}/statistici-personale', json={'session_id': session_id})
        if rest_response.status_code == 200:
            json_decoder = JSONDecoder()
            return render_template('acasa.html', info=json_decoder.decode(rest_response.text))
        else:
            return render_template('not_logged.html')


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'GET':
        session_id = __check_login()
        if session_id is not None:
            return render_template('operation_result.html', title='Eroare!', message='Sunteti deja logat!')
        return render_template('login.html')
    elif request.method == 'POST':
        user_name = request.form['user_name']
        password = request.form['password']
        rest_response = requests.post(f'{rest}/login', json={'user_name': user_name, 'password': password})
        if rest_response.status_code == 200:
            response = make_response(redirect('/'))
            response.set_cookie('session_id', rest_response.json()['session_id'])
            return response
        else:
            return render_template('operation_result.html', title='Eroare!', message=rest_response.json()['info'])


@app.route('/actualizeaza-date', methods=['GET', 'POST'])
def actualizeaza():
    if request.method == 'GET':
        session_id = __check_login()
        if session_id is None:
            return render_template('not_logged.html')

        rest_response = requests.get(f'{rest}/statistici-personale', json={'session_id': session_id})
        info = rest_response.json()
        return render_template('actualizeaza_date.html', info=info)
    elif request.method == 'POST':
        session_id = __check_login()
        if session_id is None:
            return render_template('not_logged.html')
        keys = ['nume', 'prenume', 'nume_familie', 'mancare', 'intretinere', 'distractie', 'scoala', 'personale']
        request_body = {}
        for key in keys:
            request_body[key] = request.form[key]
        request_body['session_id'] = session_id
        rest_response = requests.put(f'{rest}/actualizeaza-date', json=request_body)
        if rest_response.status_code == 202:
            return render_template('operation_result.html', title='Succes!',
                                   message='Actualizarea a avut loc cu succes!')
        else:
            return render_template('operation_result.html', title='Eroare!', message=rest_response.json()['info'])


@app.route('/logout')
def logout():
    session_id = __check_login()
    if session_id is None:
        return render_template('not_logged.html')
    response = make_response(
        render_template('operation_result.html', title='Succes!', message='Ati fost deconectat cu succes!'))
    response.set_cookie('session_id', '', expires=0)
    return response


@app.route('/statistici-familie')
def statistici():
    session_id = __check_login()
    if session_id is None:
        return render_template('not_logged.html')
    rest_response = requests.get(f'{rest}/statistici-familie', json={'session_id': session_id})
    if rest_response.status_code==200:
        response_body=rest_response.json()
        cheltuieli_totale=seq(response_body.items()).filter(lambda it: isinstance(it[1], int)).to_dict()
        infos=response_body['membri']
        return render_template('statistici.html', infos=infos, cheltuieli_totale=cheltuieli_totale)
    else:
        return render_template('operation_result.html', title='Eroare!', message=rest_response.json()['info'])
