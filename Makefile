BUNDLES = \
  quality \
  modelling

IMAGES_PATHS=$(shell dirname $(shell find . -name generate-image))

show:
	echo ${IMAGES_PATHS}

images: $(foreach b, $(IMAGES_PATHS), $(b)/generate_image)

%/generate_image:
	cd $(@D) && ./generate-image $(shell pwd)