# Part 3: Partial Order Planner

This directory contains a Jupyter Notebook implementation of a Partial Order Planner algorithm.

## Requirements

To properly run and view this notebook, you need:

1. Python 3.6 or later
2. Jupyter Notebook or JupyterLab
3. Required Python packages:
   - numpy
   - matplotlib
   - jupyter

## Installation

If you don't have Jupyter installed, you can install it with:

```bash
pip install jupyter notebook
```

## Running the Notebook

To start Jupyter Notebook and open the file:

```bash
cd part3
jupyter notebook planning_partial_order_planner.ipynb
```

This will open the notebook in your default web browser without the white space and formatting issues that can sometimes appear when viewing the raw file.

## Alternative: Convert to Python Script

If you prefer working with a standard Python script rather than a notebook, you can convert it using:

```bash
jupyter nbconvert --to python planning_partial_order_planner.ipynb
```

This will create a `planning_partial_order_planner.py` file that you can run directly.

## Note on Jupyter Display Issues

If you're still experiencing display issues with Jupyter:

1. Try updating your Jupyter installation:

   ```bash
   pip install --upgrade jupyter notebook
   ```

2. For VSCode users, install the Jupyter extension from the marketplace

3. Consider using JupyterLab instead of Jupyter Notebook:
   ```bash
   pip install jupyterlab
   jupyter lab
   ```
