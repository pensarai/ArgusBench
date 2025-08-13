package com.financehub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import java.util.Hashtable;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private static final String LDAP_URL = "ldap://localhost:389";
    private static final String BASE_DN = "dc=financehub,dc=com";
    
    public boolean authenticateUser(String username, String password) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LDAP_URL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            
            DirContext ctx = new InitialDirContext(env);
            
            // Construct LDAP search filter with user input concatenation
            String searchFilter = "(|(uid=" + username + ")(mail=" + username + "))";
            
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            NamingEnumeration<SearchResult> results = ctx.search(BASE_DN, searchFilter, searchControls);
            
            if (results.hasMore()) {
                SearchResult result = results.next();
                String userDN = result.getNameInNamespace();
                
                // Attempt to bind with user credentials
                Hashtable<String, String> authEnv = new Hashtable<>();
                authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                authEnv.put(Context.PROVIDER_URL, LDAP_URL);
                authEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
                authEnv.put(Context.SECURITY_PRINCIPAL, userDN);
                authEnv.put(Context.SECURITY_CREDENTIALS, password);
                
                DirContext authCtx = new InitialDirContext(authEnv);
                authCtx.close();
                ctx.close();
                return true;
            }
            
            ctx.close();
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isUserInRole(String username, String role) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LDAP_URL);
            
            DirContext ctx = new InitialDirContext(env);
            
            String roleFilter = "(&(uid=" + username + ")(memberOf=cn=" + role + ",ou=groups," + BASE_DN + "))";
            
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            NamingEnumeration<SearchResult> results = ctx.search(BASE_DN, roleFilter, searchControls);
            boolean hasRole = results.hasMore();
            
            ctx.close();
            return hasRole;
            
        } catch (Exception e) {
            return false;
        }
    }
}