package com.technovision.fortress.util;

import java.io.Serial;

public class CKException extends Exception {

    @Serial
    private static final long serialVersionUID = 4124107018314337603L;

    public CKException(String message) {
        super(message);
    }
}
