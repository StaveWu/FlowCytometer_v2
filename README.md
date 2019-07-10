# FlowCytometer_v2

FlowCytometer is a PC software for flow cytometry, implemented by `JavaFX` and `SpringBoot`.

## Introduction

What is a flow cytometry?

Flow cytometry is a kind of cell analysis instrument, which can precisely control cells to pass through a very fine pipe one by one. There is a laser beam in the middle of the pipe. When the cells flow through the pipe, the molecular structure in the cell is excited by light and scattered. A variety of spectra are captured by the optical path system and returned in multiple channels. At this time, the circuitry of the cytometer will collect the spectral data of these channels and send them to the host computer software.

FlowCytometer is a PC software for flow cytometry. It needs to complete the task of capturing peak information from the acquired spectral data stream, converting it into cell features, and grouping these features through charts and circle gates to analyze the cellular components.

## Overview
![FlowCytometer's main page](https://github.com/StaveWu/images/blob/master/FlowCytometer_v2/main_page.png)

## Features
The software can be roughly divided into 5 parts:
 - starter: Imitate the design of the `idea` software, which is the entrance to the project.
 - Project tree: Manage project files;
 - Dashboard: Control the sampling setup of the underlying circuit, the flow system, and monitor its status;
 - Channel: Real-time storage and display of channel data, and capture spectral features, support calculation strategies for peak width, peak height, and peak area;
 - Worksheet: Clustering cell features, rendered by scatter plots and histograms, filtered by rectangular and polygonal circle gates.

## Get Started
If you **want to play** with the FlowCytometer application, clone the repository and run:

`./gradlew run`

Since the host computer is working with the board, in order to facilitate the test results, I have provided a simulation option in the connected device ComboBox. This option can be used away from the board, and the data source is noise.

