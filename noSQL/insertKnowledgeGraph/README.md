# NAME

insertToKnowledgeGraph

# VERSION

1.0

# AUTHOR

Khaos Research Group

Manuel Paneque Romero (mpaneque@uma.es)

# DATE

18-03-2022

# DESCRIPTION
Insert data into knowledge graph.
 
# DOCKER

## Build

```
docker build -t docker.io/insert_knowledge_graph -f insertKnoledgeGraph.dockerfile .
```

## Run

```
docker run -v $(pwd)/data:/usr/local/src/data/ docker.io/insert_knowledge_graph --filepath "graph.nt"
```

# Parameters
* filepath (str) -> The name of the nt file.

# Outputs
* database.json
