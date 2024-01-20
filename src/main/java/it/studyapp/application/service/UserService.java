package it.studyapp.application.service;

import it.studyapp.application.entity.Student;

/**
 * Interface used by other parts of the application to look up information about users.
 */
public interface UserService {

    public void updateUser(Student student);
    
    public void resetPassword(String userId);
}
