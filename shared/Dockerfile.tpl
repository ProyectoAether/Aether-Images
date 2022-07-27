FROM {{BASE_IMAGE}}

LABEL Khaos Research Group <khaos.uma.es>

RUN apt-get update && apt-get install -y \
   curl \
   libssl-dev \
   libcurl4-openssl-dev \
   libxml2-dev

# BEGIN IMAGE CUSTOMIZATIONS
# END IMAGE CUSTOMIZATIONS