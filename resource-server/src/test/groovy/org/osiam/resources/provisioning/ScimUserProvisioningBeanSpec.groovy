package org.osiam.resources.provisioning

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.storage.entities.UserEntity
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.helper.ScimConverter;
import org.osiam.resources.scim.User
import org.osiam.storage.dao.UserDAO

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 19.03.13
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
class ScimUserProvisioningBeanSpec extends Specification {


    def userDao = Mock(UserDAO)
    def userEntity = Mock(UserEntity)
    def scimUser = Mock(User)
    def scimConverter = Mock(ScimConverter)
    SCIMUserProvisioningBean scimUserProvisioningBean = new SCIMUserProvisioningBean(userDao: userDao, scimConverter: scimConverter)

    def "should be possible to get an user by his id"() {
        given:
        userDao.getById("1234") >> userEntity
        userEntity.toScim() >> scimUser

        when:
        def user = scimUserProvisioningBean.getById("1234")

        then:
        user == scimUser
    }

    def "should be possible to create a user with generated UUID as internalId"() {
        given:
        def scimUser = new User.Builder('test').build()
        
        when:
        def user = scimUserProvisioningBean.create(scimUser)

        then:
        1 * scimConverter.fromScim(_) >> new UserEntity(userName:'test')
        user.id ==~ "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    }

    def "should throw exception if user name already exists"() {
        given:
        def exception = Mock(Exception)
        userDao.create(_) >> { throw exception }
        scimUser.getUserName() >> 'test'
        exception.getMessage() >> "scim_user_username_key"

        when:
        scimUserProvisioningBean.create(scimUser)

        then:
        1 * scimConverter.fromScim(_) >> new UserEntity(userName:'test')
        def e = thrown(ResourceExistsException)
        e.getMessage() == "The user with name test already exists."
    }

    def "should throw exception if externalId already exists"() {
        given:
        def exception = Mock(Exception)
        userDao.create(_) >> { throw exception }
        scimUser.getExternalId() >> "irrelevant"
        exception.getMessage() >> "scim_id_externalid_key"

        when:
        scimUserProvisioningBean.create(scimUser)

        then:
        1 * scimConverter.fromScim(_) >> new UserEntity()
        def e = thrown(ResourceExistsException)
        e.getMessage() == "The user with the externalId irrelevant already exists."
    }

    def "should get an user before update, set the expected fields, merge the result"() {
        given:
        def internalId = UUID.randomUUID()
        def scimUser = new User.Builder("test").build()
        def entity = new UserEntity()
        entity.setId(internalId)

        when:
        scimUserProvisioningBean.replace(internalId.toString(), scimUser)
        then:
        1 * userDao.getById(internalId.toString()) >> entity
        1 * userDao.update(entity) >> entity
    }

    def "should wrap IllegalAccessException to an IllegalState"() {
        given:
        GenericSCIMToEntityWrapper setUserFields = Mock(GenericSCIMToEntityWrapper)

        when:
        scimUserProvisioningBean.setFieldsWrapException(setUserFields);
        then:
        1 * setUserFields.setFields() >> { throw new IllegalAccessException("Blubb") }
        def e = thrown(IllegalStateException)
        e.message == "This should not happen."

    }

    def "should call dao delete on delete"() {
        given:
        def id = UUID.randomUUID().toString()
        when:
        scimUserProvisioningBean.delete(id)
        then:
        1 * userDao.delete(id)
    }

    def "should call dao search on search"() {
        given:
        def scimSearchResultMock = Mock(SCIMSearchResult)
        userDao.search("anyFilter", "userName", "ascending", 100, 1) >> scimSearchResultMock

        def userEntityMock = Mock(UserEntity)
        def user = Mock(User)
        def userList = [userEntityMock] as List
        scimSearchResultMock.getResources() >> userList
        scimSearchResultMock.getTotalResults() >> 1000.toLong()

        when:
        def result = scimUserProvisioningBean.search("anyFilter", "userName", "ascending", 100, 1)

        then:
        result != null
        result.getTotalResults() == 1000.toLong()
    }
}