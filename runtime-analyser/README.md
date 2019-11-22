# tese-runtime
Runtime metadata extractor for java projects

## Description

This is one of the three components necessary for the correct functioning of the framework
developed in "A Software Repository for Live Software Development". This component is responsible
for the extraction of the execution logs of a Java project. It was developed as an _AspectJ_ project to
be linked to any project to analyze.

This component is intended for communication with the repository in [Repository](https://github.com/dominguesgm/tese-repository) 
and its information complemented by the tool in [Structural Analyzer](https://github.com/dominguesgm/tese-static).

## Installation

These are the steps to run/install this tool correctly.

1. Install the repository as instructed in [Repository](https://github.com/dominguesgm/tese-repository).

2. Set a system environment variable for LIVESD_SERVER with the server url. This is done by modifying the _/etc/environment_ file,
adding a line like the following:

`LIVESD_SERVER="http://0.0.0.0/"`

3. Install _AspectJ_ into your Eclipse IDE.

4. Import this project to the workspace.

## Usage

In order to use this to analyze a given project, follow these steps.

1 Add _AspectJ_ capabilities to the project to analyze (Right click in project to be analyzed in Project Explorer → Configure → Convert to AspectJ Project).

2. Include analyzer project in the other project's aspect path (Right click in project to be analyzed in Project Explorer → Properties → AspectJ Build → Aspect Path tab → Add Project and select analyzer).

3. Run project to analyze.

It is also possible for a user to specify which classes/packages he wants to analyze. For this, the user should modify
the MethodInvocation.aj file and insert the following snippet to the end of the pointcut:

` && (<INSERT_WITHINS>)`

With `<INSERT_WITHINS>` being `within(<PACKAGE>.<CLASS>)` pointcuts, separated by `||` operators.

Example of possible added pointcuts: ` && (within(maze.cli.CLInterface) || within(maze.logic))`
