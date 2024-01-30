# NAME

restrictions2recipe

# VERSION

1.0

# AUTHOR

Khaos Research Group

Juan Carlos Ruiz Ruiz (juancaruru@uma.es)

# DATE

20/09/2023

# DESCRIPTION
Receives the restrictions given in the Restrictions json file from `createRestrictions` components and transform it into a recipe suitable for `solveWithFABIOLA` component.
 
# DOCKER

## Build

```
docker build -t docker.io/restrictions2recipe -f restrictions2recipe.dockerfile .
```

## Run

```
docker run -v $(pwd)/data:/usr/local/src/data/ docker.io/restrictions2recipe --help
```

Example:

```
docker run -v $(pwd)/data:/usr/local/src/data/ docker.io/restrictions2recipe --restrictions-path /usr/local/src/data/restrictions.json --input-data-path /usr/local/src/conquense_output_split.json --output-path /usr/local/src/data/ --prices "10,20,30"
```

# Outputs

* conquenseRecipe.json
