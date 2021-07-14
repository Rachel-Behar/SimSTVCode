# SimSTVCode
Packages
=============
* AttributeRanker - Containd interface and classes for rankers udes by the Threshold algoritm.
* Data - Contains class for holding static data structues that loads and hold the dataset being proccesed.
* Experiments - Contains code for running the experiments presented in the paper.
* Globals - Contains static global variables.
* Greedy - Contains topK class that runs the greedy topK baseline algorithm.
* Item - Contains calss item for managing voters and candidates, column rankers and Threshold algorim classes to manage votes.
* Similarity - Contains SimilarityMeasures class that is in charge of all similarity calculations.
* STV - Contains SimSTV and axillary calsses to run the SimSTV algorithm.
* main.java - simply runs the experiments.

Datasets
=============
Datasets used in the Experiments can be found in:

* www.kaggle.com/shrutimehta/zomato-restaurants-data
* www.kaggle.com/drintoul/bank-locations-by-city-state-zip-and-lat-long
* www.kaggle.com/sobhanmoosavi/us-accidents

Files sould be preproccessed to remove all commas from text fields so the code can properly read the csv format. 
Then put under a folder called Datasets.
