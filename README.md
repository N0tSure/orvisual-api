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

### Rest resources

#### Picture resource

Picture resource provides metadata for uploaded file, supply `GET`, `DELETE` methods. All `GET` operations returns
`200` status code on success. When *delete* operation completes successfully, `204` (*No content*) status will
returned.

Picture resource contains link to picture file by path `$._links.imageFile.href`.

Delete operation except that removes `Picture` record, also removes picture file from physical storage.

#### Order resource

Will be written as soon as, code will written.

### File resource

This resource has non HATEOAS syntax. Allows exchange image files between service and client. Supported operations:

| Resource mapping | HTTP Method | Status | Response type | Description |
| :--------------- | :---------- | :----- | :---------- | :---- |
| `/files`         | `POST`      | Created (`201`) | `Picture` metadata object | Saves image and metadata object |
| `/files/{checksum}` | `GET` | OK (`200`) | image binary data | Find picture by checksum |

#### Upload file

Files should uploaded as `multipart/form-data`, file data should attached to part with name `image`. File's MIME type
should present in part header. Example:

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

#### Load file

File may be requested for service by file's **SHA-256** checksum, for example:

```
GET http://localhost:8080/files/97df3588b5a3f24babc3851b372f0ba71a9dcdded43b14b9d06961bfc1707d9d

HTTP/1.1 200
Accept-Ranges: bytes
Content-Type: image/jpeg
Content-Length: 9
Date: Sat, 09 Jun 2018 16:59:02 GMT

[image file binary data]
```

If file with given checksum exists it will returned, in headers may be found `Content-Type` header, which represents
image's type.
