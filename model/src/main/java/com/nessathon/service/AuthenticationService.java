package com.nessathon.service;

import com.nessathon.dao.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {

    @Autowired
    private Environment env;

    @Autowired
    DataAccess dataAccess;

    public boolean validateLogin(String username, String password) {

        return dataAccess.validateLogin(username, password);
    }
    
}
