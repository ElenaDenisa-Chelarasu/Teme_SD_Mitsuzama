from flask import Flask, request, json

from data_classes.PersonalInformation import PersonalInformation
from services.SessionManager import SessionManagerInstance as ses_man
from services.EncryptedDBManager import EncryptedDBManager
from functional import seq

app = Flask(__name__)
dbManager = EncryptedDBManager()


def __check_login():
    try:
        session_id = request.json['session_id']
    except:
        return None, None
    if session_id is None:
        return None, None
    else:
        id_persoana = ses_man.check_session(session_id)
        if id_persoana == -1:
            return None, None
        else:
            ses_man.refresh_session(session_id)
            return session_id, id_persoana


def __to_json(x):
    return json.dumps(x, default=lambda it: it.__dict__)


@app.route('/login', methods=['POST'])
def login():
    try:
        user_name = request.json['user_name']
        password = request.json['password']
    except:
        return app.response_class(__to_json({'info': 'Cerere in format invalid!'}), status=401,
                                  mimetype='application/json')
    id = dbManager.log_in(user_name, password)
    if id == -1:
        return app.response_class(__to_json({'info': 'Date de conectare invalide!'}), status=401,
                                  mimetype='application/json')
    else:
        session_id = ses_man.create_session(id)
        return app.response_class(__to_json({'session_id': session_id}), status=200, mimetype='application/json')


@app.route('/statistici-personale', methods=['GET'])
def statistici_personale():
    session_id, id_persoana = __check_login()
    if session_id is None:
        return app.response_class(__to_json({'info': 'Nu sunteti logat!'}), status=401, mimetype='application/json')
    ses_man.refresh_session(session_id)
    info = dbManager.get_person_informations(id_persoana)
    return app.response_class(__to_json(info), status=200, mimetype='application/json')


@app.route('/actualizeaza-date', methods=['PUT'])
def actualizeaza():
    session_id, id_persoana = __check_login()
    if session_id is None:
        return app.response_class(__to_json({'info': 'Nu sunteti logat!'}), status=401, mimetype='application/json')
    try:
        keys = ['nume', 'prenume', 'nume_familie', 'mancare', 'intretinere', 'distractie', 'scoala', 'personale']
        aux=seq(request.json.items()).filter(lambda it: it[0] in keys).to_dict()
    except:
        return app.response_class(__to_json({'info': 'Cerere in format invalid!'}), status=401,
                                  mimetype='application/json')
    info = PersonalInformation(**aux, id_persoana=id_persoana)
    dbManager.update_person_informations(info)
    return app.response_class(None, status=202, mimetype='application/json')


@app.route('/check-login')
def check_login():
    session_id, id_persoana = __check_login()
    if session_id is None:
        return app.response_class(__to_json({'logged': 'false'}), status=200, mimetype='application/json')
    else:
        return app.response_class(__to_json({'logged': 'true'}), status=200, mimetype='application/json')


@app.route('/statistici-familie')
def statistici():
    session_id, id_persoana = __check_login()
    if session_id is None:
        return app.response_class(__to_json({'info': 'Nu sunteti logat!'}), status=401, mimetype='application/json')
    infos = dbManager.get_family_informations(id_persoana)
    response = {}
    response['mancare'] = seq(infos).map(lambda it: it.mancare).sum()
    response['intretinere'] = seq(infos).map(lambda it: it.intretinere).sum()
    response['distractie'] = seq(infos).map(lambda it: it.distractie).sum()
    response['scoala'] = seq(infos).map(lambda it: it.scoala).sum()
    response['personale'] = seq(infos).map(lambda it: it.personale).sum()
    response['total'] = seq(response.items()).map(lambda it: it[1]).sum()
    response['membri'] = seq(infos).map(lambda it: it.__dict__).to_list()
    return app.response_class(__to_json(response), status=200, mimetype='application/json')
