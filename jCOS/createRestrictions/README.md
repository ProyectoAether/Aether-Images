# NAME

createRestrictions

# VERSION

1.0

# AUTHOR

Khaos Research Group

Juan Carlos Ruiz Ruiz (juancaruru@uma.es)

# DATE

20/09/2023

# DESCRIPTION
Generate the semantics JSON file from an OWL ontology for an specific optimization problem.
 
# DOCKER

## Build

```
docker build -t docker.io/create_restrictions -f createRestrictions.dockerfile .
```

## Run

```
docker run -v $(pwd)/data:/usr/local/src/data/ docker.io/create_restrictions <problem-type> <semantics> (optional) <input-file> <clients-number>
```

# Parameters

* problem-type (str) -> The type of problem we want to resolve ('electric', 'knapsack' or 'multiobjective-knapsack').
* semantics (str) -> The ontology (OWL file) we want to insert into our problem input.
* input-file (str) -> The JSON file with the problem information.
* clients-number (int) -> For the electric problem we need the number of clients to extract from <input-file>

# Outputs

* restrictions.json
