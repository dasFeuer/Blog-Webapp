package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.UserPrincipal;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username);

        return userRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> {
                    System.out.println("User Not Found with username: " + username);
                    return new UsernameNotFoundException("User not found");
                });
    }

}
