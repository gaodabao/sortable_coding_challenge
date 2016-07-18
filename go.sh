#!/bin/bash
javac -cp ./json-20160212.jar Matching.java
java -cp ./json-20160212.jar:. Matching ./products.txt ./listings.txt ./result.txt


