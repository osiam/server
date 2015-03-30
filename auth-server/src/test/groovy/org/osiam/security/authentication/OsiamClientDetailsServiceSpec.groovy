/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.security.authentication

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osiam.auth.oauth_client.ClientDao
import org.osiam.auth.oauth_client.ClientEntity
import org.osiam.client.oauth.Scope

import spock.lang.Specification

class OsiamClientDetailsServiceSpec extends Specification {

    ClientDao clientDao = Mock()
    OsiamClientDetailsService osiamClientDetailsService = new OsiamClientDetailsService(clientDao: clientDao)
    def clientId = 'client-id'
	def String UUID = 'UUIDValue'
	def final Date newExpiryDate = new Date(System.currentTimeMillis() + 1000000)


    def 'loading client details returns a correct converted OsiamClientDetails instance'() {
        given:
        ClientEntity clientEntity = createFullClientEntity(clientId)

        when:
        OsiamClientDetails result = osiamClientDetailsService.loadClientByClientId(clientId)

        then:
        1 * clientDao.getClient(clientId) >> clientEntity
        isEqual(result, clientEntity)
    }


	def 'get Expiry Date works'(){
		given:
		ClientEntity clientEntity = createFullClientEntity(clientId)
		clientDao.getClient(clientId) >> clientEntity

		when:
		Date expectedDate = osiamClientDetailsService.getExpiryDate(clientId,UUID)

		then:
		expectedDate == newExpiryDate
	}

    void isEqual(OsiamClientDetails result, ClientEntity clientEntity) {
        assert result.getId() == clientEntity.getId()
        assert result.getClientSecret() == clientEntity.getClientSecret()
        assert result.getScope() == clientEntity.getScope()
        assert result.getGrants() == clientEntity.getGrants()
        assert result.getRedirectUri() == clientEntity.getRedirectUri()
        assert result.getAccessTokenValiditySeconds() == clientEntity.getAccessTokenValiditySeconds()
        assert result.getRefreshTokenValiditySeconds() == clientEntity.getRefreshTokenValiditySeconds()
        assert result.isImplicit() == clientEntity.isImplicit()
		assert areEqualMaps(result.getExpiryDates(), clientEntity.getExpiryDates())
        assert result.getValidityInSeconds() == clientEntity.getValidityInSeconds()
    }

    ClientEntity createFullClientEntity(clientId){

		def HashMap<String,Date> datesMap = new HashMap<String,Date>()
		datesMap.put(UUID, newExpiryDate)
		datesMap.put("anotherUUID", new Date(System.currentTimeMillis() - 100000))

        ClientEntity result = new ClientEntity()
        result.setId(clientId)
        result.setClientSecret('secret')
        result.setScope([Scope.ALL] as Set)
        result.setGrants(['grant'] as Set)
        result.setRedirectUri('redirect-uri')
        result.setAccessTokenValiditySeconds(10000)
        result.setRefreshTokenValiditySeconds(100000)
        result.setImplicit(false)
		result.setExpiryDates(datesMap)
        result.setValidityInSeconds(1000)
        return result
    }

	static boolean areEqualMaps(HashMap<String,Date> map1, HashMap<String,Date> map2){
		if(map1.size() != map2.size())
			return false;
		Iterator<Entry<String, Date>> itr = map1.entrySet().iterator();
		while(itr.hasNext()){
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) itr.next();
			if(map2.get(entry.getKey()) != entry.getValue())
				return false;
		}
		return true;
	}
}
