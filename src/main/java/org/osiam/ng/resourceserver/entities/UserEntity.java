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

package org.osiam.ng.resourceserver.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import scim.schema.v2.Address;
import scim.schema.v2.MultiValuedAttribute;
import scim.schema.v2.Name;
import scim.schema.v2.User;

import javax.persistence.*;
import java.util.*;

/**
 * User Entity
 */
@Entity(name = "scim_user")
@NamedQueries({@NamedQuery(name = "getUserByUsername", query = "SELECT u FROM scim_user u WHERE u.userName = :username")})
public class UserEntity extends InternalIdSkeleton implements UserDetails {

    private static final String MAPPING_NAME = "user";
    private static final long serialVersionUID = -6535056565639057058L;

    
    @Column(nullable = false, unique = true)
    private String userName;

    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private NameEntity name;

    
    @Column
    private String nickName;

    
    @Column
    private String profileUrl;

    
    @Column
    private String title;

    
    @Column
    private String userType;

    
    @Column
    private String preferredLanguage;

    
    @Column
    private String locale;

    
    @Column
    private String timezone;

    
    @Column
    private Boolean active;

    @Column(nullable = false)
    private String password;

    
    @Column
    private String displayName;

    
    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailEntity> emails;

    
    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhoneNumberEntity> phoneNumbers;

    
    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImEntity> ims;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhotoEntity> photos;

    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AddressEntity> addresses;

    
    @OneToMany(fetch = FetchType.EAGER)
    private Set<GroupEntity> groups;

    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<EntitlementsEntity> entitlements;

    //needs to be eager fetched due to authorization decisions
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RolesEntity> roles;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<X509CertificateEntity> x509Certificates;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scim_user_additional", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "additional")
    private Set<String> any;

    public static UserEntity fromScim(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setActive(user.isActive());
        userEntity.setAddresses(scimUserAddressesToEntity(user.getAddresses()));
        userEntity.setAny(scimAnyToStringSet(user.getAny()));
        userEntity.setDisplayName(user.getDisplayName());
        userEntity.setEmails(scimEmailsToEntity(user.getEmails()));
        userEntity.setEntitlements(scimEntitlementsToEntity(user.getEntitlements()));
        userEntity.setExternalId(user.getExternalId());
        userEntity.setIms(scimImsToEntity(user.getIms()));
        userEntity.setLocale(user.getLocale());
        userEntity.setName(scimNameToEntity(user.getName()));
        userEntity.setNickName(user.getNickName());
        userEntity.setPassword(user.getPassword());
        userEntity.setPhoneNumbers(scimPhonenumbersToEntity(user.getPhoneNumbers()));
        userEntity.setPhotos(scimPhotosToEntity(user.getPhotos()));
        userEntity.setPreferredLanguage(user.getPreferredLanguage());
        userEntity.setProfileUrl(user.getProfileUrl());
        userEntity.setRoles(scimUserRolesToEntity(user.getRoles()));
        userEntity.setTimezone(user.getTimezone());
        userEntity.setTitle(user.getTitle());
        userEntity.setUsername(user.getUserName());
        userEntity.setUserType(user.getUserType());
        userEntity.setX509Certificates(scimCertificatesToEntity(user.getX509Certificates()));
        return userEntity;
    }

    private static Set<X509CertificateEntity> scimCertificatesToEntity(List<MultiValuedAttribute> x509Certificates) {
        Set<X509CertificateEntity> x509CertificateEntities = new HashSet<>();
        if (x509Certificates != null) {
            for (MultiValuedAttribute multiValuedAttribute : x509Certificates) {
                x509CertificateEntities.add(X509CertificateEntity.fromScim(multiValuedAttribute));
            }
        }
        return x509CertificateEntities;
    }

    private static Set<RolesEntity> scimUserRolesToEntity(List<MultiValuedAttribute> roles) {
        Set<RolesEntity> rolesEntities = new HashSet<>();
        if (roles != null) {
            for (MultiValuedAttribute multiValuedAttribute : roles) {
                rolesEntities.add(RolesEntity.fromScim(multiValuedAttribute));
            }
        }
        return rolesEntities;
    }

    private static Set<PhotoEntity> scimPhotosToEntity(List<MultiValuedAttribute> photos) {
        Set<PhotoEntity> photoEntities = new HashSet<>();
        if (photos != null) {
            for (MultiValuedAttribute multiValuedAttribute : photos) {
                photoEntities.add(PhotoEntity.fromScim(multiValuedAttribute));
            }
        }
        return photoEntities;
    }

    private static Set<PhoneNumberEntity> scimPhonenumbersToEntity(List<MultiValuedAttribute> phoneNumbers) {
        Set<PhoneNumberEntity> phoneNumberEntities = new HashSet<>();
        if (phoneNumbers != null) {
            for (MultiValuedAttribute multiValuedAttribute : phoneNumbers) {
                phoneNumberEntities.add(PhoneNumberEntity.fromScim(multiValuedAttribute));
            }
        }
        return phoneNumberEntities;
    }

    private static NameEntity scimNameToEntity(Name name) {
        return NameEntity.fromScim(name);
    }

    private static Set<ImEntity> scimImsToEntity(List<MultiValuedAttribute> ims) {
        Set<ImEntity> imEntities = new HashSet<>();
        if (ims != null) {
            for (MultiValuedAttribute multiValuedAttribute : ims) {
                imEntities.add(ImEntity.fromScim(multiValuedAttribute));
            }
        }
        return imEntities;
    }

    private static Set<EntitlementsEntity> scimEntitlementsToEntity(List<MultiValuedAttribute> entitlements) {
        Set<EntitlementsEntity> entitlementsEntities = new HashSet<>();
        if (entitlements != null) {
            for (MultiValuedAttribute multiValuedAttribute : entitlements) {
                entitlementsEntities.add(EntitlementsEntity.fromScim(multiValuedAttribute));
            }
        }
        return entitlementsEntities;
    }

    private static Set<AddressEntity> scimUserAddressesToEntity(List<Address> addresses) {

        Set<AddressEntity> addressEntities = new HashSet<>();
        if (addresses != null) {
            for (Address address : addresses) {
                addressEntities.add(AddressEntity.fromScim(address));
            }
        }
        return addressEntities;
    }

    private static Set<String> scimAnyToStringSet(Set<Object> any) {
        Set<String> anyStrings = new HashSet<>();
        if (any != null) {
            for (Object object : any) {
                anyStrings.add(object.toString());
            }
        }
        return anyStrings;
    }

    private static Set<EmailEntity> scimEmailsToEntity(List<MultiValuedAttribute> emails) {
        Set<EmailEntity> emailEntities = new HashSet<>();
        if (emails != null) {
            for (MultiValuedAttribute multiValuedAttribute : emails) {
                emailEntities.add(EmailEntity.fromScim(multiValuedAttribute));
            }
        }
        return emailEntities;
    }

    /**
     * @return the name entity
     */
    public NameEntity getName() {
        return name;
    }

    /**
     * @param name the name entity
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
     * @param nickName the nick name
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
     * @param profileUrl the profile url
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
     * @param title the title
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
     * @param userType the user type
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
     * @param preferredLanguage the preferred languages
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
     * @param locale the locale
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
     * @param timezone the timezone
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
     * @param active the active status
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @param userName the user name
     */
    public void setUsername(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return the emails entity
     */
    public Set<EmailEntity> getEmails() {
        if (emails == null) {
            emails = new HashSet<>();
        }
        return emails;
    }

    /**
     * @param emails the emails entity
     */
    public void setEmails(Set<EmailEntity> emails) {
        this.emails = emails;
    }

    /**
     * @return the phone numbers entity
     */
    public Set<PhoneNumberEntity> getPhoneNumbers() {
        if (phoneNumbers == null) {
            phoneNumbers = new HashSet<>();
        }
        return phoneNumbers;
    }

    /**
     * @param phoneNumbers the phone numbers entity
     */
    public void setPhoneNumbers(Set<PhoneNumberEntity> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * @return the instant messaging entity
     */
    public Set<ImEntity> getIms() {
        if (ims == null) {
            ims = new HashSet<>();
        }
        return ims;
    }

    /**
     * @param ims the instant messaging entity
     */
    public void setIms(Set<ImEntity> ims) {
        this.ims = ims;
    }

    /**
     * @return the photos entity
     */
    public Set<PhotoEntity> getPhotos() {
        if (photos == null) {
            photos = new HashSet<>();
        }
        return photos;
    }

    /**
     * @param photos the photos entity
     */
    public void setPhotos(Set<PhotoEntity> photos) {
        this.photos = photos;
    }

    /**
     * @return the addresses entity
     */
    public Set<AddressEntity> getAddresses() {
        if (addresses == null) {
            addresses = new HashSet<>();
        }
        return addresses;
    }

    /**
     * @param addresses the addresses entity
     */
    public void setAddresses(Set<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    /**
     * @return the groups entity
     */
    public Set<GroupEntity> getGroups() {
        if (groups == null) {
            groups = new HashSet<>();
        }
        return groups;
    }

    /**
     * @param groups the groups entity
     */
    public void setGroups(Set<GroupEntity> groups) {
        this.groups = groups;
    }

    /**
     * @return the entitlements
     */
    public Set<EntitlementsEntity> getEntitlements() {
        if (entitlements == null) {
            entitlements = new HashSet<>();
        }
        return entitlements;
    }

    /**
     * @param entitlements the entitlements
     */
    public void setEntitlements(Set<EntitlementsEntity> entitlements) {
        this.entitlements = entitlements;
    }

    /**
     * @return the roles
     */
    public Set<RolesEntity> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }

    /**
     * @param roles the roles
     */
    public void setRoles(Set<RolesEntity> roles) {
        this.roles = roles;
    }

    /**
     * @return the X509 certs
     */
    public Set<X509CertificateEntity> getX509Certificates() {
        if (x509Certificates == null) {
            x509Certificates = new HashSet<>();
        }
        return x509Certificates;
    }

    /**
     * @param x509Certificates the X509 certs
     */
    public void setX509Certificates(Set<X509CertificateEntity> x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    /**
     * @return any
     */
    public Set<String> getAny() {
        return any;
    }

    /**
     * @param any any
     */
    public void setAny(Set<String> any) {
        this.any = any;
    }



    public User toScim() {
        return new User.Builder(getUsername()).
                setActive(getActive()).
                setAddresses(entityAddressToScim(getAddresses())).
                setAny(anyStringSetToObjectSet(getAny())).
                setDisplayName(getDisplayName()).
                setEmails(entityEmailToScim(getEmails())).
                setEntitlements(entityEntitlementsToScim(getEntitlements())).
                setGroups(entityGroupsToScim(getGroups())).
                setIms(entityImsToScim(getIms())).
                setLocale(getLocale()).
                setName(getName() != null ? getName().toScim() : null).
                setNickName(getNickName()).
                setPassword(getPassword()).
                setPhoneNumbers(entityPhonenumbersToScim(getPhoneNumbers())).
                setPhotos(entityPhotosToScim(getPhotos())).
                setPreferredLanguage(getPreferredLanguage()).
                setProfileUrl(getProfileUrl()).
                setRoles(entityRolesToScim(getRoles())).
                setTimezone(getTimezone()).
                setTitle(getTitle()).
                setUserType(getUserType()).
                setX509Certificates(entityX509CertificatesToScim(getX509Certificates())).
                setExternalId(getExternalId()).
                setId(getId().toString()).
                setMeta(getMeta().toScim()).
                build();
    }

    private List<MultiValuedAttribute> entityX509CertificatesToScim(Set<X509CertificateEntity> x509CertificateEntities) {
        List<MultiValuedAttribute> x509Certificates = new ArrayList<>();
        for (X509CertificateEntity x509CertificateEntity : x509CertificateEntities) {
            x509Certificates.add(x509CertificateEntity.toScim());
        }
        return x509Certificates;
    }

    private List<MultiValuedAttribute> entityRolesToScim(Set<RolesEntity> rolesEntities) {
        List<MultiValuedAttribute> roles = new ArrayList<>();
        for (RolesEntity rolesEntity : rolesEntities) {
            roles.add(rolesEntity.toScim());
        }
        return roles;
    }

    private List<MultiValuedAttribute> entityPhotosToScim(Set<PhotoEntity> photoEntities) {
        List<MultiValuedAttribute> photos = new ArrayList<>();
        for (PhotoEntity photoEntity : photoEntities) {
            photos.add(photoEntity.toScim());
        }
        return photos;
    }

    private List<MultiValuedAttribute> entityPhonenumbersToScim(Set<PhoneNumberEntity> phoneNumberEntities) {
        List<MultiValuedAttribute> phoneNumbers = new ArrayList<>();
        for (PhoneNumberEntity phoneNumberEntity : phoneNumberEntities) {
            phoneNumbers.add(phoneNumberEntity.toScim());
        }
        return phoneNumbers;
    }

    private List<MultiValuedAttribute> entityImsToScim(Set<ImEntity> imEntities) {
        List<MultiValuedAttribute> ims = new ArrayList<>();
        for (ImEntity imEntity : imEntities) {
            ims.add(imEntity.toScim());
        }
        return ims;
    }

    private List<MultiValuedAttribute> entityGroupsToScim(Set<GroupEntity> groupEntities) {
        List<MultiValuedAttribute> groups = new ArrayList<>();
        for (GroupEntity groupEntity : groupEntities) {
            groups.add(groupEntity.toMultiValueScim());
        }
        return groups;
    }

    private List<MultiValuedAttribute> entityEntitlementsToScim(Set<EntitlementsEntity> entitlementsEntities) {
        List<MultiValuedAttribute> entitlements = new ArrayList<>();
        for (EntitlementsEntity entitlementsEntity : entitlementsEntities) {
            entitlements.add(entitlementsEntity.toScim());
        }
        return entitlements;
    }

    private List<MultiValuedAttribute> entityEmailToScim(Set<EmailEntity> emailEntities) {
        List<MultiValuedAttribute> emails = new ArrayList<>();
        for (EmailEntity emailEntity : emailEntities) {
            emails.add(emailEntity.toScim());
        }
        return emails;
    }

    private Set<Object> anyStringSetToObjectSet(Set<String> anySet) {
        if (anySet == null) {
            return null;
        }
        Set<Object> objectSet = new HashSet<>();
        for (String any : anySet) {
            objectSet.add(any);
        }
        return objectSet;
    }

    private List<Address> entityAddressToScim(Set<AddressEntity> addressEntities) {
        List<Address> addresses = new ArrayList<>();
        for (AddressEntity addressEntity : addressEntities) {
            addresses.add(addressEntity.toScim());
        }
        return addresses;
    }

}
