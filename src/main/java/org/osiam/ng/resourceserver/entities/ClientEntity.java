package org.osiam.ng.resourceserver.entities;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import java.util.*;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "osiam_client")
@NamedQueries({@NamedQuery(name = "getById", query = "SELECT i FROM osiam_client i WHERE i.id= :id")})
public class ClientEntity implements ClientDetails {
    @Id
    @GeneratedValue
    private long internal_id;

    @JsonProperty
    @Type(type = "pg-uuid")
    @Column(unique = true, nullable = false)
    private UUID id = UUID.randomUUID();
    @JsonProperty
    @Transient
    private final int access_token_validity = 1337;
    @JsonProperty
    @Transient
    private final int refresh_token_validity = 1337;
    @JsonProperty
    @Column(unique = true, nullable = false)
    private String redirect_uri;
    @JsonProperty
    @Transient
    private Set<String> grants = generateGrants();

    @JsonProperty("client_secret")
    @Column(name = "client_secret", unique = true, nullable = false)
    private String clientSecret = generateSecret();

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_scopes", joinColumns = @JoinColumn(name = "id"))
    @Column
    private Set<String> scope;

    private Set<String> generateGrants() {
        Set<String> result = new HashSet<>();
        Collections.addAll(result, "authorization_code", "implicit", "refresh-token");
        return result;
    }

    private String generateSecret() {
        //TODO must be improved
        return UUID.randomUUID().toString();
    }


    @Override
    public String getClientId() {
        return id.toString();
    }

    @Override
    public Set<String> getResourceIds() {
        return Collections.emptySet();
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return grants;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        Set<String> result = new HashSet<>();
        result.add(redirect_uri);
        return result;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return access_token_validity;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refresh_token_validity;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }


    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }
}
