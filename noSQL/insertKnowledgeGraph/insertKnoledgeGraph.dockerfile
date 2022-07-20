FROM python:3.9.7-bullseye

LABEL Khaos Research Group <khaos.uma.es>

RUN apt-get update && apt-get install -y python3-pip

RUN pip3 install \
    pydantic[dotenv] \
    requests \
    typer

WORKDIR /usr/local/src/
COPY . /usr/local/src/

ENTRYPOINT ["python", "insertKnoledgeGraph.py"]