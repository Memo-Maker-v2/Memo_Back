package com.example.memo.common;

public interface ResponseMessage {

    //HTTP Status 200
    String SUCCESS = "Success";

    //HTTP Status 400
    String VALIDATION_FAILED = "Validation Failed";
    String DUPLICATE_EMAIL = "Duplicate Memberemail";
    String DUPLICATE_NAME = "Duplicate Membername";
    String NOT_EXISTED_USER = "This user does not exist";

    //HTTP Status 401
    String SIGN_IN_FAIL = "Login information mismatch.";
    String AUTHORIZATION_FAIL = "Authorization Failed.";

    //HTTP Status 403
    String NO_PERMISSION = "Do not have permission.";

    //HTTP Status 500
    String DATABASE_ERROR = "Database error.";
}
