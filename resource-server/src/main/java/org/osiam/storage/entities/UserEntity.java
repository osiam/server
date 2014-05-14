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

package org.osiam.storage.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

import com.google.common.collect.ImmutableSet;

/**
 * User Entity
 */
@Entity
@Table(name = "scim_user")
public class UserEntity extends ResourceEntity {

    public static final String JOIN_COLUMN_NAME = "user_internal_id";

    @Column(nullable = false, unique = true)
    private String userName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private NameEntity name;

    private String nickName;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    private String profileUrl;

    private String title;

    private String userType;

    private String preferredLanguage;

    private String locale;

    private String timezone;

    private Boolean active = Boolean.FALSE;

    @Column(nullable = false)
    private String password;

    private String displayName;

    private static final int BATCH_SIZE = 100;
    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<EmailEntity> emails = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<PhoneNumberEntity> phoneNumbers = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<ImEntity> ims = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<PhotoEntity> photos = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<AddressEntity> addresses = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<EntitlementEntity> entitlements = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<RoleEntity> roles = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<X509CertificateEntity> x509Certificates = new HashSet<>();

    @BatchSize(size = BATCH_SIZE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable = false)
    private Set<ExtensionFieldValueEntity> extensionFieldValues = new HashSet<>();

    public UserEntity() {
        getMeta().setResourceType("User");
    }

    /**
     * @return the name entity
     */
    public NameEntity getName() {
        return name;
    }

    /**
     * @param name
     *            the name entity
     */
    public void setName(NameEntity name) {
        this.name = name;
    }

    /**
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName
     *            the nick name
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the profile url
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * @param profileUrl
     *            the profile url
     */
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the user type
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType
     *            the user type
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * @return the preferred languages
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * @param preferredLanguage
     *            the preferred languages
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * @param locale
     *            the locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * @return the timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * @param timezone
     *            the timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * @return the active status
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active
     *            the active status
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns an immutable view of the list of emails
     * 
     * @return the emails entity
     */
    public Set<EmailEntity> getEmails() {
        return ImmutableSet.copyOf(emails);
    }

    /**
     * Adds a new email to this user
     * 
     * @param email
     *            the email to add
     */
    public void addEmail(EmailEntity email) {
        emails.add(email);
    }

    /**
     * Removes the given email from this user
     * 
     * @param email
     *            the email to remove
     */
    public void removeEmail(EmailEntity email) {
        emails.remove(email);
    }

    /**
     * Removes all email's from this user
     */
    public void removeAllEmails() {
        emails.clear();
    }

    /**
     * @return the extensions data of the user
     */
    public Set<ExtensionFieldValueEntity> getExtensionFieldValues() {
        return extensionFieldValues;
    }
    
    /**
     * Adds a new extensionFieldValue to this user
     * 
     * @param extensionFieldValue
     *            the extensionFieldValue to add
     */
    public void addExtensionFieldValue(ExtensionFieldValueEntity extensionFieldValue) {
        extensionFieldValues.add(extensionFieldValue);
    }
    
    /**
     * Removes the given extensionFieldValue from this user
     * 
     * @param extensionFieldValue
     *            the extensionFieldValue to remove
     */
    public void removeExtensionFieldValue(ExtensionFieldValueEntity extensionFieldValue) {
        extensionFieldValues.remove(extensionFieldValue);
    }
    
    /**
     * Removes all extensionFieldValues from this user
     */
    public void removeAllExtensionFieldValues(String urn) {
        ImmutableSet<ExtensionFieldValueEntity> fields = ImmutableSet.copyOf(extensionFieldValues);
        for (ExtensionFieldValueEntity extensionFieldValue : fields) {
            if(extensionFieldValue.getExtensionField().getExtension().getUrn().equals(urn)) {
                extensionFieldValues.remove(extensionFieldValue);
            }
        }
    }

    /**
     * Adds or updates an extension field value for this User. When updating, the old value of the extension field is
     * removed from this user and the new one will be added.
     * 
     * @param extensionValue
     *            The extension field value to add or update
     */
    public void addOrUpdateExtensionValue(ExtensionFieldValueEntity extensionValue) {
        if (extensionValue == null) {
            throw new IllegalArgumentException("extensionValue must not be null");
        }
        
        if (extensionFieldValues.contains(extensionValue)) {
            extensionFieldValues.remove(extensionValue);
        }
        
        extensionFieldValues.add(extensionValue);
    }
    
    
    
    /**
     * @return the phone numbers entity
     */
    public Set<PhoneNumberEntity> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Adds a new phoneNumber to this user
     * 
     * @param phoneNumber
     *            the phoneNumnber to add
     */
    public void addPhoneNumber(PhoneNumberEntity phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    /**
     * Removes the given phoneNumber from this user
     * 
     * @param phoneNumber
     *            the phoneNumber to remove
     */
    public void removePhoneNumber(PhoneNumberEntity phoneNumber) {
        phoneNumbers.remove(phoneNumber);
    }

    /**
     * Removes all phoneNumber's from this user
     */
    public void removeAllPhoneNumbers() {
        phoneNumbers.clear();
    }

    /**
     * @return the instant messaging entity
     */
    public Set<ImEntity> getIms() {
        return ims;
    }

    /**
     * Adds a new im to this user
     * 
     * @param im
     *            the im to add
     */
    public void addIm(ImEntity im) {
        ims.add(im);
    }

    /**
     * Removes the given im from this user
     * 
     * @param im
     *            the im to remove
     */
    public void removeIm(ImEntity im) {
        ims.remove(im);
    }

    /**
     * Removes all im's from this user
     */
    public void removeAllIms() {
        ims.clear();
    }

    /**
     * @return the photos entity
     */
    public Set<PhotoEntity> getPhotos() {
        return photos;
    }

    /**
     * Adds a new photo to this user
     * 
     * @param photo
     *            the photo to add
     */
    public void addPhoto(PhotoEntity photo) {
        photos.add(photo);
    }

    /**
     * Removes the given photo from this user
     * 
     * @param photo
     *            the photo to remove
     */
    public void removePhoto(PhotoEntity photo) {
        photos.remove(photo);
    }

    /**
     * Removes all photo's from this user
     */
    public void removeAllPhotos() {
        photos.clear();
    }

    /**
     * @return the addresses entity
     */
    public Set<AddressEntity> getAddresses() {
        return addresses;
    }

    /**
     * Adds a new address to this user
     * 
     * @param address
     *            the address to add
     */
    public void addAddress(AddressEntity address) {
        addresses.add(address);
    }

    /**
     * Removes the given address from this user
     * 
     * @param address
     *            the address to remove
     */
    public void removeAddress(AddressEntity address) {
        addresses.remove(address);
    }

    /**
     * Removes all addresses from this user
     */
    public void removeAllAddresses() {
        addresses.clear();
    }

    /**
     * @return the entitlements
     */
    public Set<EntitlementEntity> getEntitlements() {
        return entitlements;
    }

    /**
     * Adds a new entitlement to this user
     * 
     * @param entitlement
     *            the entitlement to add
     */
    public void addEntitlement(EntitlementEntity entitlement) {
        entitlements.add(entitlement);
    }

    /**
     * Removes the given entitlement from this user
     * 
     * @param entitlement
     *            the entitlement to remove
     */
    public void removeEntitlement(EntitlementEntity entitlement) {
        entitlements.remove(entitlement);
    }

    /**
     * Removes all entitlement's from this user
     */
    public void removeAllEntitlements() {
        entitlements.clear();
    }

    /**
     * @return the roles
     */
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    /**
     * Adds a new role to this user
     * 
     * @param role
     *            the role to add
     */
    public void addRole(RoleEntity role) {
        roles.add(role);
    }

    /**
     * Removes the given role from this user
     * 
     * @param role
     *            the role to remove
     */
    public void removeRole(RoleEntity role) {
        roles.remove(role);
    }

    /**
     * Removes all role's from this user
     */
    public void removeAllRoles() {
        roles.clear();
    }

    /**
     * @return the X509 certs
     */
    public Set<X509CertificateEntity> getX509Certificates() {
        return x509Certificates;
    }

    /**
     * Adds a new x509Certificate to this user
     * 
     * @param x509Certificate
     *            the x509Certificate to add
     */
    public void addX509Certificate(X509CertificateEntity x509Certificate) {
        x509Certificates.add(x509Certificate);
    }

    /**
     * Removes the given x509Certificate from this user
     * 
     * @param x509Certificate
     *            the x509Certificate to remove
     */
    public void removeX509Certificate(X509CertificateEntity x509Certificate) {
        x509Certificates.remove(x509Certificate);
    }

    /**
     * Removes all x509Certificate's from this user
     */
    public void removeAllX509Certificates() {
        x509Certificates.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserEntity [userName=").append(userName).append(", getId()=").append(getId()).append("]");
        return builder.toString();
    }
}
