package org.osiam.resources.converter

import java.util.Collections;

import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.provisioning.SCIMEntities;
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.entities.extension.ExtensionEntity
import org.osiam.storage.entities.extension.ExtensionFieldEntity
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity

import spock.lang.Specification

class ExtensionConverterSpec extends Specification {

    private static String URN1 = "urn:org.osiam.extensions:Test01:1.0"
    private static String URN2 = "urn:org.osiam.extensions:Test02:1.0"

    Map fixtures = [(URN1): [
            [fieldname:'gender', valueAsString: 'male', value: 'male' , type: ExtensionFieldType.STRING],
            [fieldname:'size', valueAsString: '1.78', value: new BigDecimal('1.78') , type: ExtensionFieldType.DECIMAL],
            [fieldname:'numberChildren', valueAsString: '2', value: BigInteger.valueOf(2) , type: ExtensionFieldType.INTEGER],
            [fieldname:'birth', valueAsString: '2008-01-23T04:56:22.000Z',
                value: new Date(ISODateTimeFormat.dateTime().withZoneUTC().parseDateTime("2008-01-23T04:56:22.000Z").getMillis())
                , type: ExtensionFieldType.DATE_TIME],
            [fieldname:'newsletter', valueAsString: 'true', value: true , type: ExtensionFieldType.BOOLEAN]
        ],(URN2):[
            [fieldname:'favoredPet', valueAsString: 'doc', value: 'doc' , type: ExtensionFieldType.STRING],
        ]]

    private ExtensionConverter converter;

    def setup(){
        converter  = new ExtensionConverter()
    }

    def 'convert extensionEntity set to scim extension set works'() {
        given:
        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = getFilledExtensionEntity(fixtures, URN1, URN2)
        Set<Extension> scimExtensionSet = getFilledScimExtension(fixtures, URN1, URN2)

        when:
        Set<Extension> extensions = converter.toScim(extensionFieldValueEntitySet)

        then:
        extensions.equals(scimExtensionSet)
        extensions == scimExtensionSet
    }

    def 'convert scim extension to extensionEntity works'() {
        given:
        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = getFilledExtensionEntity(fixtures, URN1, URN2)
        Set<Extension> scimExtensionSet = getFilledScimExtension(fixtures, URN1, URN2)
        
        when:
        Set<ExtensionFieldEntity> extensions = converter.fromScim(scimExtensionSet)

        then:
        extensions == extensionFieldValueEntitySet
    }

    def 'convert empty extensionEntity set to empty extension set works'(){
        given:
        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = Collections.emptySet()

        when:
        Set<Extension> extensions = converter.toScim(extensionFieldValueEntitySet)

        then:
        extensions != null
        extensions.size() == 0
    }

    def 'convert empty scim extension to empty extensionEntity works'() {
        given:
        Set<Extension> scimExtensionSet = Collections.emptySet()

        when:
        Set<ExtensionFieldEntity> extensions = converter.fromScim(scimExtensionSet)

        then:
        extensions != null
        extensions.size() == 0
    }

    def 'passing null scim extension returns null'(){
        expect:
        converter.fromScim(null) == null
    }

    def 'passing null extensionEntity returns null'(){
        expect:
        converter.toScim(null) == null
    }

    def addNameValuePairToExtensionEntity(ExtensionEntity extensionEntity, String name, String value){
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity();
        fieldEntity.setName(name);

        ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity();
        valueEntity.setValue(value);
        valueEntity.setExtensionField(fieldEntity);

        fieldEntity.setExtension(extensionEntity);
    }

    def Set<ExtensionFieldValueEntity> getFilledExtensionEntity(Map fixtures, String... urns){

        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = new HashSet<>();

        for (urn in urns) {
            ExtensionEntity entity = new ExtensionEntity();
            entity.setUrn(urn);
            
            def fixture = fixtures.get(urn)
            for (field in fixture) {
                ExtensionFieldValueEntity valueEntity = getFieldToEntityValueSet(entity, field.get('fieldname'), field.get('valueAsString'), field.get('type'))
                extensionFieldValueEntitySet.add(valueEntity);
            }
        }

        return extensionFieldValueEntitySet;
    }

    def Set<Extension> getFilledScimExtension(Map fixtures, String... urns){

        Set<Extension> scimExtensionSet = new HashSet<>()
        
        for (urn in urns) {
            Extension extension = new Extension(urn)
            def fixture = fixtures.get(urn)
            
            for (field in fixture) {
                extension.addOrUpdateField(field.get('fieldname'), field.get('value'))
            }
            
            scimExtensionSet.add(extension)
        }

        return scimExtensionSet
    }

    def ExtensionFieldValueEntity getFieldToEntityValueSet(ExtensionEntity extensionEntity, String name, String value, ExtensionFieldType type){
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity();
        fieldEntity.setName(name);
        fieldEntity.setType(type)

        ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity();
        valueEntity.setValue(value);
        fieldEntity.setExtension(extensionEntity);
        valueEntity.setExtensionField(fieldEntity)

        return valueEntity
    }
}
