## NAME

FABIOLA

## TAG

1.0.0

## AUTHOR

Adrián Segura Ortiz

## DATE

01-02-2022 10:50

## DESCRIPTION

FAst BIg cOstraint LAboratory (FABIOLA)

# DOCKER

## Build

```
docker build -t docker.io/fabiola .
```

## Run

```
docker run -v $(pwd):/usr/local/src docker.io/fabiola --input-file input/conquense_output_split.json --model-input-variables consumos,precio_potencia --model-output-variables TPTotal,pc1,pc2,pc3 --output-folder output
```

## Submit

```
docker ... --tag 1.0.0
```