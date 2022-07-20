import json
import uuid
import os

import requests
import typer

from config import settings


def create_database(filename: str, endpoint: str, username: str, password: str):
    session = requests.Session()
    session.auth = (username, password)

    params = []

    meta = {
        'dbname': f"NoSQL-{uuid.uuid4()}",
        'options': {"search.enabled": "true"},
        'files': [{"filename": filename}]
    }
    params.append(('root', (None, json.dumps(meta), 'application/json')))
    params.append((filename, (filename, open(filename), 'application/rdf+xml', {'Content-Encoding': None})))

    r = session.post(endpoint + '/admin/databases', files=params)
    print(r.json()["message"])

    if r.status_code == 201:
        with open('database.json', 'w') as outfile:
            json.dump(meta, outfile)


def insert_data(filepath: str = typer.Option(..., help="Input filename.")):
    create_database(filename=filepath, endpoint=settings.STARDOG_ENDPOINT, username=settings.STARDOG_USERNAME,
                    password=settings.STARDOG_PASSWORD)


if __name__ == '__main__':
    os.chdir("data")
    typer.run(insert_data)
