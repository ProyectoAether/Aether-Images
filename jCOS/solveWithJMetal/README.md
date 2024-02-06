# NAME

solveWithJMetal

# VERSION

1.0

# AUTHOR

Khaos Research Group

Juan Carlos Ruiz Ruiz (juancaruru@uma.es)

# DATE

27/09/2023

# DESCRIPTION
Generate a CSV with the solutions for an especific problem.
 
# DOCKER

## Build

```
docker build -t docker.io/solve_with_jmetal -f solveWithJMetal.dockerfile .
```

## Run

```
docker run -v $(pwd)/data:/usr/local/src/data/ docker.io/solve_with_jmetal <problem-type> <restrictions-file.json> <problem-file> (optional) <variable1> <variable2>
```
> **NOTE**: Depending on the problem, the variables 1 and 2 will be of different type. Watch out [Main.java](./script/src/main/java/Main.java) to know how to implement them.
# Parameters

* **`problem-type` (str)** -> The type of problem we want to resolve ('electric', 'knapsack' or 'multiobjective-knapsack').
* **`restrictions-file` (str)** -> The restrictions JSON file obtained in the previous component. 
* **`problem-file` (str)** -> A JSON or KP file that has the information of the problem to solve.
* **`variable1` (int)** -> For the **electric problem** it will be used as the `clients-number`. For **knapsack problems** it will be used as the `populationSize`. Both as Integers.
* **`variable2` (str | int)** -> For the **electric problem** it will be used as the `prices-list`. For **knapsack problems** it will be used as the `maxEvaluations`. `prices-list` is a String with the prices list inside of the form "{1, 2, 3}". `maxEvaluations` is an Integer.

# Outputs

* results.csv
