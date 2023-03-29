package com.example.springsecurityreturn.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    //check if user has this role
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SOME_OTHER')")
    public void doAdminStaff() {
        System.out.println("Only admin here");
    }

}
