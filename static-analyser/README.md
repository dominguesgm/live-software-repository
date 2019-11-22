# tese-static
Repository for the java static analyzer used in the dissertation "A Software Repository for Live Software Development"

## Description

This is one of the three components necessary for the correct functioning of the framework
developed in "A Software Repository for Live Software Development". This component is responsible
for the extraction of a structural representation of the Java projects in the workspace. It was developed as
a Eclipse Plugin and be installed in any Eclipse IDE with the Java Development Tools (JDT) installed.

This component is intended for communication with the repository in [Repository](https://github.com/dominguesgm/tese-repository) 
and its information complemented by the tool in [Execution Analyzer](https://github.com/dominguesgm/tese-runtime).

## Installation

These are the steps to run/install this tool correctly.

1. Install the repository as instructed in [Repository](https://github.com/dominguesgm/tese-repository).

2. Set a system environment variable for LIVESD_SERVER with the server url. This is done by modifying the _/etc/environment_ file,
adding a line like the following:

`LIVESD_SERVER="http://0.0.0.0/"`

3. Copy the _.jar_ file in _exports/plugin_ to the _dropins_ folder in your eclipse root folder.

## Usage

The plugin builds the projects' representations when the Eclipse IDE starts, and detects changes in the workspace automatically,
sending them to the repository as they occur. That being said, if there are
any issues with the representation of the projects in the repository noticed by the user, 
use the button _Sample Menu → Process Source_ to flush the structure of the whole project into the repository.

