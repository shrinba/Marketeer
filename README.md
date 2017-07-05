# Marketeer

#### Marketeer.java is the main file with all additional classes all part of the same Marketeer.java file.

## Various Classes

### Transactions Class
This class is used to model the transactions in the stream.

### FraudulentTransaction Class
This class extends Transaction and is used to model fraudulent transactions that are then written to a file.

### Person Class
The Person class forms the node element of the Graph. Each Person has or maintains their list of transactions.

### Graph Class
This is the main class used to model a Graph data structure. An Adjacency list representation is used.
For computing friends 'X' degrees apart a list lookup mechanism is used. Though a more efficient approach would be to
use matrix factorization. Java isn't a great language to work with Matrices. This class does most of the heavy lifting.

### GraphMath
This class calculates the Mean and Standard Deviation as a standard library where this is done doesn't see to be available 
in Java.

### Marketeer
This is the main class where all the pieces come together and execution happens.


* To run the program *

1. Go to Marketeer folder
2. Run "run.sh" script.
3. Results are written to the log_output/flagged_purchases



