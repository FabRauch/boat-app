package com.openwt.boatapp.service;

public class LoginAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super("Login is already in use!");
    }
}
