# Type: Dockerfile
# Description: Dockerfile for the jCOS restrictions2recipe component

# Create the build image

FROM python:3.9.7-bullseye

LABEL Khaos Research Group <khaos.uma.es>

WORKDIR /usr/local/src/
COPY . /usr/local/src/

RUN pip install -r requirements.txt

ENTRYPOINT ["python", "script/restrictions2recipe.py"]