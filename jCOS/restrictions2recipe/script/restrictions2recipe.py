"""
This is a Python script that converts restrictions json to a json recipe for its usage with Fabiola.
Author: Juan Carlos Ruiz Ruiz
Date: 30-01-2024
"""

import json
from pathlib import Path
from argparse import ArgumentParser
import re


def main(
    restrictions_path: str,
    input_data_path: str,
    output_path: str,
    prices: list[int],
):
    """
    Main function of the script
    """
    # Read restrictions json
    with open(restrictions_path) as restrictions_file:
        restrictions = json.load(restrictions_file)

    # Check restrictions json format
    if (
        not restrictions
        or not restrictions[0].get("contract", [])
        or not restrictions[0]["contract"][0].get("constraintList", [])
    ):
        raise ValueError("Invalid restrictions file format")

    # Get percentages
    constraint_list = restrictions[0]["contract"][0]["constraintList"]
    percentages = []
    for constraint in constraint_list:
        if constraint.get("constraintType") == "between":
            percentages = [int(constraint["smaller"]), int(constraint["greater"])]
            break

    # Read input data json
    input_data = Path(input_data_path)

    # Set up prices for recipe
    precios = f"array({', '.join([str(price) for price in prices])})"

    # Create recipe
    recipe = {
        "datasetPath": f"/mnt/shared/{input_data.name}",
        "outDatasetPath": "/mnt/shared/results",
        "copPath": "./script/solver/generic_titan.scala",
        "in": ["C", "PRECIO", "PERCENTAGES"],
        "out": ["TPTotal", "pc1", "pc2", "pc3"],
        "other": ["CUPS"],
        "preCopTransformations": [
            {
                "withColumn": "C",
                "expr": "transform(consumos, x -> array(x.potencia_maxima_p1, x.potencia_maxima_p2, x.potencia_maxima_p3) )",
            },
            {"withColumn": "C", "expr": "slice(C, 1, 12)"},
            {
                "withColumn": "C",
                "expr": "transform(C, x -> transform(x, y -> cast( (y * 100) as integer) ))",
            },
            {"withColumn": "PRECIO", "expr": precios},
            {"withColumn": "CUPS", "expr": "localizacion.cups"},
            {"withColumn": "PERCENTAGES", "expr": percentages},
        ],
    }

    # Write recipe json
    with open(f"{output_path}conquenseRecipe.json", "w") as recipe_file:
        json.dump(recipe, recipe_file, indent=4)


if __name__ == "__main__":

    def __parse_ints(int_str: str) -> list[int]:
        """
        Parses the ints string to a list of integers
        """
        int_list = re.findall(r"\d+", int_str)
        return [int(i) for i in int_list]

    parser = ArgumentParser(
        description="Converts restrictions json to a json recipe for its usage with Fabiola."
    )
    parser.add_argument(
        "--restrictions-path",
        type=str,
        help="Path to the restrictions json file.",
        required=True,
        dest="restrictions_path",
    )
    parser.add_argument(
        "--input-data-path",
        type=str,
        help="Path to the input data json file.",
        required=True,
        dest="input_data_path",
    )
    parser.add_argument(
        "--prices",
        type=__parse_ints,
        help="Prices list for the recipe.",
        required=True,
        default=[25, 17, 30],
        dest="prices",
    )
    parser.add_argument(
        "--output-path",
        type=str,
        help="Path to the output json file.",
        required=True,
        dest="output_path",
    )

    args = parser.parse_args()

    main(
        restrictions_path=args.restrictions_path,
        input_data_path=args.input_data_path,
        prices=args.prices,
        output_path=args.output_path,
    )
