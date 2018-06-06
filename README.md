# OrVisual

## Introduction

Out of all the five senses, your vision seems the most important. Humans are fairly unique in their reliance on sight
as the dominant sense and this is reflected in how complicated our eyes are relative to other creatures. Therefore
visual aids in human communication very important. **OrVisual** provides opportunity for clients and service maintainer
to communicate, using visual aids.

For an example, let’s take car painting service, when customer makes order, he may need to attach addition info.
Of course, he may add just textual data to order or maybe talk with service provider using phone, but much easier to
add some pictures which describes order details.

For this **OrVisual** provides API, which allow publish customer contact data and additionally some pictures which
describes order.

## Service API

OrVisual service using **Spring Boot**, built up as RESTful service with [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS).
Additionally there is a Spring MVC controller, which provides access to image files. HATEOAS implemented using
[Spring Data Rest](https://docs.spring.io/spring-data/rest/docs/current/reference/html/).

At present service have two _resources_: `order` and `picture`. Order represents order summary object.
Picture represents image file metadata. All resources represents in HAL format, see [Internet Draft](https://tools.ietf.org/html/draft-kelly-json-hal-08)
for HAL JSON format.

### Service metadata

Description for all endpoints available on `/profile`. For example:

```
GET http://localhost:8080/profile
Accept: */*
```

The response will be in a HAL format, at path `$._links` will find **JSON object**, which will contain links to
profiles for all endpoint, and also link to itself.

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/profile"
    },
    "orders": {
      "href": "http://localhost:8080/profile/orders"
    },
    "pictures": {
      "href": "http://localhost:8080/profile/pictures"
    }
  }
}
```

Thus each resource have description, which available by requesting `/profile/{resource}` endpoints. The response will
be in a [ALPS](http://alps.io/) format. This format provide descriptions of application-level semantics, see
[Internet Draft](https://tools.ietf.org/html/draft-amundsen-richardson-foster-alps-02).

Obtained response will contain list of descriptors at path `$.alps.descriptors`:

1. Representation of resource itself, which contains identifier of resource and list of `descriptors`, for each field
    of resource domain object

2. Descriptors for all available operations, which contents return type of operation - `rt` and type of operation, see
    details in [draft](https://tools.ietf.org/html/draft-amundsen-richardson-foster-alps-02#section-2.2.12)


For each resource, may be obtained [JSON Schema](http://json-schema.org/), which describes data format, provides
clear, human- and machine-readable documentation and complete structural validation, useful for automated testing and
validating client-submitted data. To retrieve JSON Schema you invoke them with Accept header `application/schema+json`.

```
GET http://localhost:8080/profile/pictures
Accept: application/schema+json
```
