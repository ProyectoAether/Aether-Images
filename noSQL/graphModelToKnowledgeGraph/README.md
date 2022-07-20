# NAME

graphModelToKnowledgeGraph

# VERSION

1.0

# AUTHOR

Khaos Research Group

Manuel Paneque Romero (mpaneque@uma.es)

# DATE

18-03-2022

# DESCRIPTION
Convert Model of security graph database to knowledge graph.
 
# DOCKER

## Build

```
docker build -t docker.io/graph_model_to_knowledge_graph -f graph_model_to_knowledge_graph.dockerfile .
```

## Run

```
docker run -v $(pwd)/data:/usr/local/src/data/ docker.io/graph_model_to_knowledge_graph --filepath "Hospital.model"
```

# Parameters
* filepath (str) -> The name of the model file.

# Outputs
* graph.nt
* graph.pdf
