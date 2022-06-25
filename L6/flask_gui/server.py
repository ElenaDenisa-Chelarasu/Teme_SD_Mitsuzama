
import requests
from requests.exceptions import HTTPError
from flask import Flask, render_template, request


app = Flask(__name__)


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/send-request', methods=['POST'])
def send_request():
    parameters = request.form.to_dict()
    url = None
    query = parameters['termenCautare']
    if 'html' in parameters:
        url = '/print?format=html'
    elif 'json' in parameters:
        url = '/print?format=json'
    elif 'text' in parameters:
        url = '/print?format=raw'
    elif 'autor' in parameters:
        url = '/find?author={}'.format(query.replace(' ', '%20'))
    elif 'titlu' in parameters:
        url = '/find?title={}'.format(query.replace(' ', '%20'))
    elif 'editura' in parameters:
        url = '/find?publisher={}'.format(query.replace(' ', '%20'))
    full_url = "http://localhost:8080" + url
    try:
        response = requests.get(full_url).content.decode('utf-8')
        print(response)
        return render_template('index.html', response=response)
    except HTTPError as http_err:
        print('HTTP error occurred: {}'.format(http_err))
    except Exception as err:
        print('Other error occurred: {}'.format(err))


@app.errorhandler(404)
def page_not_found(error):
    return render_template('404_page.html'), 404


if __name__ == '__main__':
    app.run(host='localhost', port=8081, debug=True)
