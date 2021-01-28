# AnomalyDetector
 Second year, computer science project.
 
 This project implements an anomaly detector for flights.
 
 There are several milestones.
 
 The first milestone is to calculated statistic functions.
The classes that implement in this milestone are "Line","Point" and "StatLib".

The second milestone detected exceptions that were detected during the flight file.
The flight file is a table that each line is a time step dedicated by the system.
To analyze an error, there is a "train" file we run over so we can set the limits in each detail (this train file represents a proper flight).
After that, the real flight details are analyzed according to the train results and write a report about this specific flight.
The classes that implement in this milestone are "AnomalyReport" , "CorrelatedFeatures", "SimpleAnomalyDetector", "TimeSeries" , "TimeSeriesAnomalyDetector".