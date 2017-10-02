Sample Apache Storm pipeline for discovering phone number elements and write them into a Cassandra table, to be used for demonstration purposes.

### Requirements

The example was designed for minimal requirements of software dependencies, so only Docker is necessary for running it.

### Starting the cluster

The minimal storm cluster is composed by:

- Zookeeper: Coordination of the cluster
- Supervisor: Worker node that holds the worker processes
- Nimbus: Orchestration of work amongst supervisors and resource management
- Storm UI: Web console for monitoring and managing the cluster

To start the Storm cluster use the following command:

    docker-compose up -d

### Building the image

When everything is started up, we will build the image of our topology:

    docker build . -t stormsample_uploader

### Uploading the topology

Running this image means uploading the topology into the previously created Storm cluster:

    docker run --rm --network stormsample_default stormsample_uploader

### Managing the cluster

Browse http://localhost:8080 for managing the cluster using Storm UI.

### Sending a message

To send phone numbers to be processed by the topology:

    docker run --rm appropriate/nc echo "+351911234567;+351221234567" | nc localhost 9999

### Querying the database

To query the database for results:

    docker exec cassandra cqlsh -e "select * from storm_sample_ks.phones"

The following results are expected for the sample message input:

     country_calling_code | national_number | number_type | country_code
    ----------------------+-----------------+-------------+--------------
                      351 |       911234567 |      MOBILE |           PT
                      351 |       221234567 |  FIXED_LINE |           PT

