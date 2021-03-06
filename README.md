# TSAFE

[![Build Status](https://secure.travis-ci.org/enpm611green/tsafe.png?branch=master)](https://travis-ci.org/enpm611green/tsafe)
Building a trusted computing base for air traffic control software.


## What You Need

* [Java SE Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads)


## Contents of the Archive

 *  `datafiles/`
    Some data needed to use TSAFE. (navigation files, the map, sample config..)

 *  `lib/`
    Libraries needed to run TSAFE.

 *  `src/`
    The source code for TSAFE.

 *  `src/tsafe/common_datastructures/_resources/`
    Some essential files to start TSAFE. (splash screen, icon..)

 *  `.classpath, .project, build.xml`
    Eclipse project files and ant tasks.

 *  `default_tsafe.properties, tsafe.properties`
    Configuration files.


## Installing TSAFE

Clone this repository to a directory of your choice.
Importing the Project in Eclipse:

 1.  Start Eclipse
 2.  File -> Import
 3.  Choose `General` -> `Existing Projects into Workspace` and click on `Next`
 4.  Enter the path to the cloned repository and click on `Finish`
 5.  The project will appear in the Package Explorer under the name `TSafe`


## Running TSAFE from the CLI

Build it and run it.

    ant

Wasn't that easy?
