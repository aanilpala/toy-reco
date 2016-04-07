# toy-reco

toy-reco is a toy recommender engine which reads the training data from a text file with triples in the form of <userId>;<itemId>
and after training returns 'false' or 'true' given a <userId><itemId>

there is also an accuracy testing unit is implemented. It reads a .jpg file and treats its pixel rows as user vectors and pixel columns as item vectors.
Based on this, it creates a recommendation scenerio with test and training sets. The schema of the test set is <userId>;<itemId>;<boolean>.
It finally returns accuracy, false positive and false negative rates of the recommender on the test set generated via the image.