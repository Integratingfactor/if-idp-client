package com.integratingfactor.idp.lib.rbac;

public abstract class IdpRbacException extends RuntimeException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1808991135041909125L;
    private String error;
    
    protected IdpRbacException(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return error;
    }

}
