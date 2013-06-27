package org.osiam.ng.resourceserver.entities

import org.osiam.storage.entities.ClientEntity
import spock.lang.Specification

class ClientEntityTest extends Specification {
    def under_test = new ClientEntity()

    def "should generate id"(){
        when:
        def b = new ClientEntity()
        then:
        b.id
        b.id != under_test.id
    }

    def "should deliver the same grant types"(){
        when:
        def b = new ClientEntity()
        then:
        b.getAuthorizedGrantTypes() == under_test.getAuthorizedGrantTypes()
        b.getAuthorizedGrantTypes() == ["authorization_code", "implicit", "refresh-token"] as Set
    }

    def "should be able to set access_token length"(){
        when:
        under_test.setAccessTokenValiditySeconds(2342)
        then:
        under_test.getAccessTokenValiditySeconds() == 2342
   }

    def "should be able to set refresh token validity"(){
        when:
        under_test.setRefreshTokenValiditySeconds(2342)
        then:
        under_test.getRefreshTokenValiditySeconds() == 2342


    }

    def "should generate a secret"(){
        when:
        def b = new ClientEntity()
        then:
        b.clientSecret
        b.clientSecret != under_test.clientSecret
    }

    def "getClientId should return id.toString"(){
        when:
        def result = under_test.id.toString()
        then:
        under_test.getClientId() == result
    }

    def "resource ids should be empty"(){
        when:
        def ids = under_test.getResourceIds()
        then:
        ids.empty
    }

    def "isSecretRequired should be true"(){
        when:
        def result = under_test.isSecretRequired()
        then:
        result
    }

    def "isScoped should be true"(){
        when:
        def result = under_test.isScoped()
        then:
        result
    }

    def "getRegisteredRedirectUri should return a set which contains redirect_uri"(){
        given:
        under_test.setRedirect_uri("should_i_stay_or_should_i_go_now")
        when:
        def result = under_test.getRegisteredRedirectUri()
        then:
        result == [under_test.getRedirect_uri()] as Set
    }

    def "getAuthorities should be empty"(){
        when:
        def result = under_test.getAuthorities()
        then:
        result.empty
    }


    def "getAdditionalInformation should be empty"(){
        when:
        def result = under_test.getAdditionalInformation()
        then:
        !result
    }

    def "should be possible to set id"(){
        given:
        def id = UUID.randomUUID()
        when:
        under_test.setId(id)
        then:
        under_test.getId() == id
    }

    def "should be possible to set secret"(){
        given:
        def secret = "moep!"
        when:
        under_test.setClientSecret(secret)
        then:
        under_test.getClientSecret() == secret
    }

    def "should be possible to set scopes"(){
        given:
        def scopes = ['POST', 'GET'] as Set
        when:
        under_test.setScope(scopes)
        then:
        under_test.getScope() == scopes
    }

    def "should be possible to set internal_id"(){
        when:
        under_test.setInternal_id(23)
        then:
        under_test.getInternal_id() == 23
    }


}
