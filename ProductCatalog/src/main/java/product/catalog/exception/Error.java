package product.catalog.exception;

import java.io.Serializable;

public class Error implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String errorMessage;
    
    public Error(String msg) {
        this.errorMessage = msg;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

}
