#!/bin/sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $@" -X PATCH localhost:8080/authorization-server/User/cef9452e-00a9-4cec-a086-d171374ffbef -d '{"schemas":["urn:scim:schemas:core:1.1"], "externalId":"marissa","userName":"Arthur Dent","password":"1234"}' && 
echo -e "\n"
