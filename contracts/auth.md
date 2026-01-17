# Nexia API Contracts (Core + BFF)

## Core Auth
POST /api/auth/register
Request:
{ "fullName": "string", "email": "string", "password": "string" }
Response 201:
{ "accessToken": "string", "tokenType": "Bearer", "expiresInSeconds": 3600 }

POST /api/auth/login
Request:
{ "email": "string", "password": "string" }
Response 200:
{ "accessToken": "string", "tokenType": "Bearer", "expiresInSeconds": 3600 }

## Core Users
GET /api/v1/users/me (Authorization: Bearer <token>)
Response 200:
{ "id":"uuid", "email":"string", "fullName":"string", "createdAt":"instant" }

## Gateway + BFF
POST /bff/auth/login-and-me
Request:
{ "email":"string", "password":"string" }
Response 200:
{ "accessToken":"string", "tokenType":"Bearer", "expiresInSeconds":3600, "me": { ... } }

GET /bff/users/me (Authorization: Bearer <token>)
Response 200:
{ "id":"uuid", "email":"string", "fullName":"string", "createdAt":"instant" }
