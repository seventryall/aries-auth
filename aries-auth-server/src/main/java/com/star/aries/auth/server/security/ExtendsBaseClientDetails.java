package com.star.aries.auth.server.security;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.star.aries.auth.dal.entity.Role;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.Jackson2ArrayOrStringDeserializer;
import org.springframework.security.oauth2.provider.client.JacksonArrayOrStringDeserializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@JsonSerialize(
        include = JsonSerialize.Inclusion.NON_DEFAULT
)
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(
        ignoreUnknown = true
)
public class ExtendsBaseClientDetails implements ClientDetails {
    @JsonProperty("client_id")
    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    @com.fasterxml.jackson.annotation.JsonProperty("client_secret")
    private String clientSecret;
    @JsonDeserialize(
            using = JacksonArrayOrStringDeserializer.class
    )
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = Jackson2ArrayOrStringDeserializer.class
    )
    private Set<String> scope;
    @JsonProperty("resource_ids")
    @JsonDeserialize(
            using = JacksonArrayOrStringDeserializer.class
    )
    @com.fasterxml.jackson.annotation.JsonProperty("resource_ids")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = Jackson2ArrayOrStringDeserializer.class
    )
    private Set<String> resourceIds;
    @JsonProperty("authorized_grant_types")
    @JsonDeserialize(
            using = JacksonArrayOrStringDeserializer.class
    )
    @com.fasterxml.jackson.annotation.JsonProperty("authorized_grant_types")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = Jackson2ArrayOrStringDeserializer.class
    )
    private Set<String> authorizedGrantTypes;
    @JsonProperty("redirect_uri")
    @JsonDeserialize(
            using = JacksonArrayOrStringDeserializer.class
    )
    @com.fasterxml.jackson.annotation.JsonProperty("redirect_uri")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = Jackson2ArrayOrStringDeserializer.class
    )
    private Set<String> registeredRedirectUris;
    @JsonProperty("autoapprove")
    @JsonDeserialize(
            using = JacksonArrayOrStringDeserializer.class
    )
    @com.fasterxml.jackson.annotation.JsonProperty("autoapprove")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = Jackson2ArrayOrStringDeserializer.class
    )
    private Set<String> autoApproveScopes;
    private List<GrantedAuthority> authorities;
    @JsonProperty("access_token_validity")
    @com.fasterxml.jackson.annotation.JsonProperty("access_token_validity")
    private Integer accessTokenValiditySeconds;
    @JsonProperty("refresh_token_validity")
    @com.fasterxml.jackson.annotation.JsonProperty("refresh_token_validity")
    private Integer refreshTokenValiditySeconds;
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Map<String, Object> additionalInformation;

    public static List<GrantedAuthority> createAuthorityList(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(roles.length);

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    public ExtendsBaseClientDetails() {
        this.scope = Collections.emptySet();
        this.resourceIds = Collections.emptySet();
        this.authorizedGrantTypes = Collections.emptySet();
        this.authorities = Collections.emptyList();
        this.additionalInformation = new LinkedHashMap();
    }

    public ExtendsBaseClientDetails(ClientDetails prototype) {
        this();
        this.setAccessTokenValiditySeconds(prototype.getAccessTokenValiditySeconds());
        this.setRefreshTokenValiditySeconds(prototype.getRefreshTokenValiditySeconds());
        this.setAuthorities(prototype.getAuthorities());
        this.setAuthorizedGrantTypes(prototype.getAuthorizedGrantTypes());
        this.setClientId(prototype.getClientId());
        this.setClientSecret(prototype.getClientSecret());
        this.setRegisteredRedirectUri(prototype.getRegisteredRedirectUri());
        this.setScope(prototype.getScope());
        this.setResourceIds(prototype.getResourceIds());
    }

    public ExtendsBaseClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, List<Role> authorities) {
        this(clientId, resourceIds, scopes, grantTypes, authorities, (String)null);
    }

    public ExtendsBaseClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, List<Role> authorities, String redirectUris) {
        this.scope = Collections.emptySet();
        this.resourceIds = Collections.emptySet();
        this.authorizedGrantTypes = Collections.emptySet();
        this.authorities = Collections.emptyList();
        this.additionalInformation = new LinkedHashMap();
        this.clientId = clientId;
        Set scopeList;
        if (StringUtils.hasText(resourceIds)) {
            scopeList = StringUtils.commaDelimitedListToSet(resourceIds);
            if (!scopeList.isEmpty()) {
                this.resourceIds = scopeList;
            }
        }

        if (StringUtils.hasText(scopes)) {
            scopeList = StringUtils.commaDelimitedListToSet(scopes);
            if (!scopeList.isEmpty()) {
                this.scope = scopeList;
            }
        }

        if (StringUtils.hasText(grantTypes)) {
            this.authorizedGrantTypes = StringUtils.commaDelimitedListToSet(grantTypes);
        } else {
            this.authorizedGrantTypes = new HashSet(Arrays.asList(new String[]{"authorization_code", "refresh_token"}));
        }

        if (!CollectionUtils.isEmpty(authorities)) {
//            this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
//            List<String> list = authorities.stream().map(p -> p.toJsonString()).collect(Collectors.toList());
//            String[] authorityArray = authorities.stream().map(p -> p.toJsonString()).collect(Collectors.toList()).toArray(new String[]{});
            String[] authorityArray = authorities.stream().map(p -> p.getCode()).collect(Collectors.toList()).toArray(new String[]{});
            this.authorities = createAuthorityList(authorityArray);
        }

        if (StringUtils.hasText(redirectUris)) {
            this.registeredRedirectUris = StringUtils.commaDelimitedListToSet(redirectUris);
        }

    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAutoApproveScopes(Collection<String> autoApproveScopes) {
        this.autoApproveScopes = new HashSet(autoApproveScopes);
    }

    @Override
    public boolean isAutoApprove(String scope) {
        if (this.autoApproveScopes == null) {
            return false;
        } else {
            Iterator var2 = this.autoApproveScopes.iterator();

            String auto;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                auto = (String) var2.next();
            } while (!auto.equals("true") && !scope.matches(auto));

            return true;
        }
    }

    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getAutoApproveScopes() {
        return this.autoApproveScopes;
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getScope() {
        return this.scope;
    }

    public void setScope(Collection<String> scope) {
        this.scope = (Set) (scope == null ? Collections.emptySet() : new LinkedHashSet(scope));
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getResourceIds() {
        return this.resourceIds;
    }

    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = (Set) (resourceIds == null ? Collections.emptySet() : new LinkedHashSet(resourceIds));
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getAuthorizedGrantTypes() {
        return this.authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = new LinkedHashSet(authorizedGrantTypes);
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getRegisteredRedirectUri() {
        return this.registeredRedirectUris;
    }

    public void setRegisteredRedirectUri(Set<String> registeredRedirectUris) {
        this.registeredRedirectUris = registeredRedirectUris == null ? null : new LinkedHashSet(registeredRedirectUris);
    }

    @JsonProperty("authorities")
    @com.fasterxml.jackson.annotation.JsonProperty("authorities")
    private List<String> getAuthoritiesAsStrings() {
        return new ArrayList(AuthorityUtils.authorityListToSet(this.authorities));
    }

    @JsonProperty("authorities")
    @JsonDeserialize(
            using = JacksonArrayOrStringDeserializer.class
    )
    @com.fasterxml.jackson.annotation.JsonProperty("authorities")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = Jackson2ArrayOrStringDeserializer.class
    )
    private void setAuthoritiesAsStrings(Set<String> values) {
        this.setAuthorities(AuthorityUtils.createAuthorityList((String[]) values.toArray(new String[values.size()])));
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new ArrayList(authorities);
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Integer getAccessTokenValiditySeconds() {
        return this.accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Integer getRefreshTokenValiditySeconds() {
        return this.refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void setAdditionalInformation(Map<String, ?> additionalInformation) {
        this.additionalInformation = new LinkedHashMap(additionalInformation);
    }

    @Override
    @JsonAnyGetter
    @com.fasterxml.jackson.annotation.JsonAnyGetter
    public Map<String, Object> getAdditionalInformation() {
        return Collections.unmodifiableMap(this.additionalInformation);
    }

    @JsonAnySetter
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void addAdditionalInformation(String key, Object value) {
        this.additionalInformation.put(key, value);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.accessTokenValiditySeconds == null ? 0 : this.accessTokenValiditySeconds.intValue());
        result = 31 * result + (this.refreshTokenValiditySeconds == null ? 0 : this.refreshTokenValiditySeconds.intValue());
        result = 31 * result + (this.authorities == null ? 0 : this.authorities.hashCode());
        result = 31 * result + (this.authorizedGrantTypes == null ? 0 : this.authorizedGrantTypes.hashCode());
        result = 31 * result + (this.clientId == null ? 0 : this.clientId.hashCode());
        result = 31 * result + (this.clientSecret == null ? 0 : this.clientSecret.hashCode());
        result = 31 * result + (this.registeredRedirectUris == null ? 0 : this.registeredRedirectUris.hashCode());
        result = 31 * result + (this.resourceIds == null ? 0 : this.resourceIds.hashCode());
        result = 31 * result + (this.scope == null ? 0 : this.scope.hashCode());
        result = 31 * result + (this.additionalInformation == null ? 0 : this.additionalInformation.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            ExtendsBaseClientDetails other = (ExtendsBaseClientDetails) obj;
            if (this.accessTokenValiditySeconds == null) {
                if (other.accessTokenValiditySeconds != null) {
                    return false;
                }
            } else if (!this.accessTokenValiditySeconds.equals(other.accessTokenValiditySeconds)) {
                return false;
            }

            if (this.refreshTokenValiditySeconds == null) {
                if (other.refreshTokenValiditySeconds != null) {
                    return false;
                }
            } else if (!this.refreshTokenValiditySeconds.equals(other.refreshTokenValiditySeconds)) {
                return false;
            }

            if (this.authorities == null) {
                if (other.authorities != null) {
                    return false;
                }
            } else if (!this.authorities.equals(other.authorities)) {
                return false;
            }

            if (this.authorizedGrantTypes == null) {
                if (other.authorizedGrantTypes != null) {
                    return false;
                }
            } else if (!this.authorizedGrantTypes.equals(other.authorizedGrantTypes)) {
                return false;
            }

            if (this.clientId == null) {
                if (other.clientId != null) {
                    return false;
                }
            } else if (!this.clientId.equals(other.clientId)) {
                return false;
            }

            if (this.clientSecret == null) {
                if (other.clientSecret != null) {
                    return false;
                }
            } else if (!this.clientSecret.equals(other.clientSecret)) {
                return false;
            }

            if (this.registeredRedirectUris == null) {
                if (other.registeredRedirectUris != null) {
                    return false;
                }
            } else if (!this.registeredRedirectUris.equals(other.registeredRedirectUris)) {
                return false;
            }

            if (this.resourceIds == null) {
                if (other.resourceIds != null) {
                    return false;
                }
            } else if (!this.resourceIds.equals(other.resourceIds)) {
                return false;
            }

            if (this.scope == null) {
                if (other.scope != null) {
                    return false;
                }
            } else if (!this.scope.equals(other.scope)) {
                return false;
            }

            if (this.additionalInformation == null) {
                if (other.additionalInformation != null) {
                    return false;
                }
            } else if (!this.additionalInformation.equals(other.additionalInformation)) {
                return false;
            }

            return true;
        }
    }

    @Override
    public String toString() {
        return "BaseClientDetails [clientId=" + this.clientId + ", clientSecret=" + this.clientSecret + ", scope=" + this.scope + ", resourceIds=" + this.resourceIds + ", authorizedGrantTypes=" + this.authorizedGrantTypes + ", registeredRedirectUris=" + this.registeredRedirectUris + ", authorities=" + this.authorities + ", accessTokenValiditySeconds=" + this.accessTokenValiditySeconds + ", refreshTokenValiditySeconds=" + this.refreshTokenValiditySeconds + ", additionalInformation=" + this.additionalInformation + "]";
    }
}
