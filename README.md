# OrVisual

## Introduction

Out of all the five senses, your vision seems the most important. Humans are fairly unique in their reliance on sight
as the dominant sense and this is reflected in how complicated our eyes are relative to other creatures. Therefore
visual aids in human communication very important. **OrVisual** provides an opportunity for clients and service 
maintainer to communicate, using visual aids.

For an example, let’s take car painting service when a customer makes an order, he may need to attach addition info.
Of course, he may add just textual data to order or maybe talk with a service provider using a phone, but much easier 
to add some pictures which describe order details.

For this **OrVisual** provides API, which allows to publish customer contact data and add some pictures which
describes order.

## Service API

OrVisual service using **Spring Boot**, built up as RESTful service with 
[HATEOAS](https://en.wikipedia.org/wiki/HATEOAS). Additionally, there is a Spring MVC controller, which provides access
to image files. HATEOAS implemented using 
[Spring Data Rest](https://docs.spring.io/spring-data/rest/docs/current/reference/html/).

At present service have two _resources_: `order` and `picture`. The Order represents an *order summary* object.
The Picture represents *image file metadata*. All resources represent in HAL format, see 
[Internet Draft](https://tools.ietf.org/html/draft-kelly-json-hal-08) for HAL JSON format.

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

Thus each resource has a description, which available by requesting `/profile/{resource}` endpoints. The response will
be in an [ALPS](http://alps.io/) format. This format provides descriptions of application-level semantics, see
[Internet Draft](https://tools.ietf.org/html/draft-amundsen-richardson-foster-alps-02).

The obtained response will contain a list of descriptors at path `$.alps.descriptors`:

1. Representation of resource itself, which contains an identifier of resource and list of `descriptors`, for each field
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

### Rest resources

#### Picture resource

Picture resource provides metadata for uploaded file, supply `GET`, `DELETE` methods. All `GET` operations returns
`200` status code on success. When *delete* operation completes successfully, `204` (*No content*) status will
be returned.

Picture resource contains a link to picture file by path `$._links.imageFile.href`.

Delete operation except that removes `Picture` record, also removes picture file from physical storage.

#### Order resource

Order resource provides order data like client's contacts, order execution status and related pictures. Client's 
contact attributes has constraints:

| Attribute | Constraint | Optional |
|-----------|------------|----------|
| `clientName` | not empty or whitespace | no |
| `clientPhone` | must match `\+?\d+` | no |
| `clientEmail` | must match `.{2,}[@].{2,}` | yes |
| `description` | must have length less than 4000 characters | yes |

All these attributes are strings. Optional attributes may not present in the model, but if they present must be valid. 
Validation applies to `POST`, `PUT` and `PATCH` operations.

Order's status is described by `acceptedAt` and `completedAt` attributes:

| Accepted at | Completed at | Status | Description |
|-------------|--------------|--------|-------------|
| `null` | `null` | New | Just created Order |
| not `null` | `null` | In progress | Order accepted to work |
| not `null` | not `null` | Completed | Works at Order has been completed |
| `null` | not `null` | Closed | Order closed without execution |

All new Order has status _new_ i.e. both `acceptedAt` and `completedAt` is `null`.

When an Order removes, all related Pictures will be removed, except Pictures which related to another Order.

### File resource

This resource has non HATEOAS syntax. Allows exchange image files between service and client. Supported operations:

| Resource mapping | HTTP Method | Status | Response type | Description |
| :--------------- | :---------- | :----- | :---------- | :---- |
| `/files`         | `POST`      | Created (`201`) | `Picture` metadata object | Saves image and metadata object |
| `/files/{checksum}` | `GET` | OK (`200`) | image binary data | Find picture by checksum |

#### Upload file

Files should be uploaded as `multipart/form-data`, file data should be attached to part with the name `image`. File's 
MIME type should present in part header. Example:

```
POST http://localhost:8080/files
Content-Type: multipart/form-data; boundary=Asrf456BGe4h


--Asrf456BGe4h
Content-Disposition: form-data; name="image"; filename="foo.jpg"
Content-Type: image/jpeg

[image file binary data]
--Asrf456BGe4h--
```

Constraints for uploaded files:

 * files with MIME type out of this types: `image/jpeg`, `image/png`, `image/gif`, `image/bmp`, will be rejected
 * files, which size greater than **10Mb** will be rejected
 * files with no content will be rejected too
 
Response from a server will be in form of **HATEOAS** Picture REST resource:

```json
{
  "fileName" : "54ebb28eac3bca48cedfee0efd180c6d7264249de4e77fc389cf6008db87babb.jpg",
  "mimeType" : "image/jpeg",
  "directory" : "54eb",
  "loadedAt" : "2018-07-02T13:45:52.378Z",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/pictures/54ebb28eac3bca48cedfee0efd180c6d7264249de4e77fc389cf6008db87babb"
    },
    "picture" : {
      "href" : "http://localhost:8080/pictures/54ebb28eac3bca48cedfee0efd180c6d7264249de4e77fc389cf6008db87babb"
    },
    "imageFile" : {
      "href" : "http://localhost:8080/files/54ebb28eac3bca48cedfee0efd180c6d7264249de4e77fc389cf6008db87babb"
    }
  }
}
``` 

#### Download file

The file may be requested for service by file's **SHA-256** checksum, for example:

```
GET http://localhost:8080/files/97df3588b5a3f24babc3851b372f0ba71a9dcdded43b14b9d06961bfc1707d9d

HTTP/1.1 200
Accept-Ranges: bytes
Content-Type: image/jpeg
Content-Length: 9
Date: Sat, 09 Jun 2018 16:59:02 GMT

[image file binary data]
```

If a file with given checksum exists it will be returned, in headers may be found `Content-Type` header, which 
represents image's type.
