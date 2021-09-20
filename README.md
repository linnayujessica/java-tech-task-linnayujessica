# Lunch Microservice

The service provides an endpoint that will determine, from a set of recipes, what I can have for lunch at a given date, based on my fridge ingredient's expiry date, so that I can quickly decide what I’ll be having to eat, and the ingredients required to prepare the meal.

### Prerequisites

* [Java 11 or 14 Runtime](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (except version 11.0.11 and above)
* [Docker](https://docs.docker.com/get-docker/) & [Docker-Compose](https://docs.docker.com/compose/install/)

### Run

1. Start database:

    ```
    docker-compose up -d
    ```
   
2. Add test data from  `sql/lunch-data.sql` to the database. Here's a helper script if you prefer:


    ```
    CONTAINER_ID=$(docker inspect --format="{{.Id}}" lunch-db)
    ```
    
    ```
    docker cp sql/lunch-data.sql $CONTAINER_ID:/lunch-data.sql
    ```
    
    ```
    docker exec $CONTAINER_ID /bin/sh -c 'mysql -u root -prezdytechtask lunch </lunch-data.sql'
    ```
    
3. Run Springboot LunchApplication

### API specification

1. `GET`  /lunch?date={format:yyyy-mm-dd}

```
curl -v http://localhost:8080/lunch?date=2021-01-01
```

2. `GET`  /recipe?title={recipe_title}

```
curl -v http://localhost:8080/recipe?title=Salad
```

3. `POST`  /exclude

```
curl -d '[{"title": "Beetroot","bestBefore": "2030-12-31","useBy": "2030-01-01"},{"title": "Cucumber","bestBefore": "2030-12-31","useBy": "2030-01-01"},{"title": "Lettuce","bestBefore": "2030-12-31","useBy": "2030-01-01"},{"title": "Salad Dressing","bestBefore": "2030-12-31","useBy": "1999-01-01"},{"title": "Tomato","bestBefore": "2030-12-31","useBy": "2030-01-01"}]' -H 'Content-Type: application/json' http://localhost:8080/exclude
```
