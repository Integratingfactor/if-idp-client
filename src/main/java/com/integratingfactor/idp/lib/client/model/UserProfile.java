package com.integratingfactor.idp.lib.client.model;

import java.io.Serializable;

public class UserProfile implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2847764599170939894L;

    private String firstName;

    private String lastName;

    private String subject;

    private String userId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserProfile [firstName=" + firstName + ", lastName=" + lastName + ", subject=" + subject + ", userId="
                + userId + "]";
    }

}
