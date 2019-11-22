# tese-repository
Repository for java projects' structural and execution data.

## Description

This is one of the three components necessary for the correct functioning
of the framework developed in "A Software Repository for Live Software Development".
This component is responsible for aggregating and distributing the information recovered
by the two other analysis tools. It is composed of a Rails server, along with a PostgreSQL database and nginx.

The two other analysis tools are the [structural analyzer](https://github.com/dominguesgm/tese-static) and
the [execution analyzer](https://github.com/dominguesgm/tese-runtime).

## Write Access

Write access is provided for every component of the structure and execution data.
Writing structural data to the repository assumes the existence of the nested attributes. For example,
when inserting a new _i\_class_, it expects the class' attributes and methods.

## Read Access
### Database

The repository provides read access to every element (structural or execution) in the database, as well as
the possibility of listing all the elements of that type.
In the specific case of project, there are two possibilities: either retreiving the shallow project metadata
or, using the `deep=true` GET field, retreiving the full information of the project, with all nested attributes included.

For the events, there is a `from` and a `to` field that can be set as two timestamps to retreive all execution logs
between those two points in time.

### WebSocket

The repository provides access to live data through a websocket. In order to connect to it, the interface
has to create a websocket and send the following subscrtiption message:

`{ "command": "subscribe", "identifier": "{\"channel\": \"ControlChannel\"}" }`

If the structural and execution analyzers are correctly configured and installed, you should be able to receive
two types of messages through the websocket.

1. Events

```
{
  events: [
    {
      "this": string,
      "target": string,
      "kind": string,
      "kind": string,
      "signature": string,
      "class": string,
      "sourceLocation": string,
      "originClass": string,
      "destinationClass": string,
      "originHash": string,
      "destinationHash": string,
      "projectName": string,
      "projectId": integer,
      "arguments": [
        {
          "value": string,
          "type": string
        }
      ]
      "timestamp": string (in timestamp format)
    }
  ]
}
```

2. Structural change

```
{
  "fetch_structure": string,
  "operation": string,
  "project_id": integer,
  "package_id": integer,
  "class_id": integer
}
```

|Field|Value|
|-|:-:|
|fetch_structure|_project_/_package_/_class_|
|operation|_delete_/_change_|
|project_id|Always present|
|package_id|Present when change is on package or class|
|class_id|Present when change is on class|


## Installation

The following steps will setup the repository.

1. Clone the repository to the intended server machine.

2. `cd tese-repository`

3. Run the following sequence of commands.

```
docker-compose build

docker-compose run app rails db:create RAILS_ENV=production

docker-compose run app rails db:migrate RAILS_ENV=production

docker-compose up -d

```
