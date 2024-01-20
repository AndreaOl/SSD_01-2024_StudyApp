package it.studyapp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import it.studyapp.application.entity.NotificationEntity;
import it.studyapp.application.entity.Reminder;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.SessionRequest;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.entity.StudentGroupRequest;
import it.studyapp.application.repository.NotificationEntityRepository;
import it.studyapp.application.repository.ReminderRepository;
import it.studyapp.application.repository.SessionRepository;
import it.studyapp.application.repository.SessionRequestRepository;
import it.studyapp.application.repository.StudentGroupRepository;
import it.studyapp.application.repository.StudentGroupRequestRepository;
import it.studyapp.application.repository.StudentRepository;

@Service
public class DataService {

	private final StudentRepository studentRepository;
	private final StudentGroupRepository studentGroupRepository;
	private final SessionRepository sessionRepository;
	private final NotificationEntityRepository notificationEntityRepository;
	private final SessionRequestRepository sessionRequestRepository;
	private final StudentGroupRequestRepository studentGroupRequestRepository;
	private final ReminderRepository reminderRepository;
  
	public DataService(StudentRepository studentRepository, StudentGroupRepository studentGroupRepository, 
			SessionRepository sessionRepository, NotificationEntityRepository notificationEntityRepository,
			SessionRequestRepository sessionRequestRepository, StudentGroupRequestRepository studentGroupRequestRepository,
			ReminderRepository reminderRepository) {

		this.studentRepository = studentRepository;
		this.studentGroupRepository = studentGroupRepository;
		this.sessionRepository = sessionRepository;
		this.notificationEntityRepository = notificationEntityRepository;
		this.sessionRequestRepository = sessionRequestRepository;
		this.studentGroupRequestRepository = studentGroupRequestRepository;
		this.reminderRepository = reminderRepository;
	}


	/* Finding data */

	public List<Student> findAllStudents() {
		return studentRepository.findAll();
	}

	public List<StudentGroup> findAllStudentGroups() {
		return studentGroupRepository.findAll();
	}

	public List<Session> findAllSessions() {
		return sessionRepository.findAll();
	}
	
	public List<SessionRequest> findAllSessionRequests() {
		return sessionRequestRepository.findAll();
	}
	
	public Student findStudentById(Long id) {
		return studentRepository.findById(id).orElse(null);
	}
	
	public Session findSessionById(Long id) {
		return sessionRepository.findById(id).orElse(null);
	}
	
	public StudentGroup findStudentGroupById(Long id) {
		return studentGroupRepository.findById(id).orElse(null);
	}
	
	public NotificationEntity findNotificationById(Long id) {
		return notificationEntityRepository.findById(id).orElse(null);
	}
	
	public SessionRequest findSessionRequestById(Long id) {
		return sessionRequestRepository.findById(id).orElse(null);
	}
	
	public StudentGroupRequest findStudentGroupRequestById(Long id) {
		return studentGroupRequestRepository.findById(id).orElse(null);
	}
	
	public Reminder findReminderById(Long id) {
		return reminderRepository.findById(id).orElse(null);
	}

	public List<Student> searchStudent(String username){
		return studentRepository.search(username);
	}
	
	public List<Student> searchStudentsEmail(String email){
		return studentRepository.searchEmail(email);
	}
	
	public List<SessionRequest> searchSessionRequests(Long sessionId) {
		return sessionRequestRepository.search(sessionId);
	}
	
	public List<StudentGroupRequest> searchStudentGroupRequests(Long studentGroupId) {
		return studentGroupRequestRepository.search(studentGroupId);
	}
  
	/* Saving data */

	public Student saveStudent(Student student) {
		if(student == null) {
			System.err.println("User is null");
			return null;
		}
		return studentRepository.save(student);
	}

	public StudentGroup saveStudentGroup(StudentGroup studentGroup) {
		if(studentGroup == null) {
			System.err.println("Student group is null");
			return null;
		}
		return studentGroupRepository.save(studentGroup);
	}

	public Session saveSession(Session session) {
		if(session == null) {
			System.err.println("Session is null");
			return null;
		}
		return sessionRepository.save(session);
	}
	
	public NotificationEntity saveNotification(NotificationEntity notification) {
		if(notification == null) {
			System.err.println("Notification is null");
			return null;
		}
		return notificationEntityRepository.save(notification);
	}
	
	public SessionRequest saveSessionRequest(SessionRequest sessionRequest) {
		if(sessionRequest == null) {
			System.err.println("Session Request is null");
			return null;
		}
		return sessionRequestRepository.save(sessionRequest);
	}
	
	public StudentGroupRequest saveStudentGroupRequest(StudentGroupRequest studentGroupRequest) {
		if(studentGroupRequest == null) {
			System.err.println("Student Group Request is null");
			return null;
		}
		return studentGroupRequestRepository.save(studentGroupRequest);
	}
	
	public Reminder saveReminder(Reminder reminder) {
		if(reminder == null) {
			System.err.println("Reminder is null");
			return null;
		}
		return reminderRepository.save(reminder);
	}


	/* Updating data */

	public void updateStudent(Student student) {
		if(student == null) {
			System.err.println("User is null");
			return;
		}
		studentRepository.update(student.getFirstName(), student.getLastName(),
				student.getFieldOfStudy(), student.getBirthDate(), student.getYearFollowing(),
				student.getEmail(), student.getUsername(), student.getAvatar());
	}
	
	public void updateSession(Session session) {
		if(session == null) {
			System.err.println("Session is null");
			return;
		}
		sessionRepository.update(session.getSubject(), session.getDate(), session.getLocation(), session.getOwner(), session.getId());
	}
	
	/* Deleting data */

	public void deleteStudent(Student student) {
		studentRepository.delete(student);
	}

	public void deleteStudentGroup(StudentGroup studentGroup) {
		studentGroupRepository.delete(studentGroup);
	}

	public void deleteSession(Session session) {
		sessionRepository.delete(session);
	}
	
	public void deleteNotification(NotificationEntity notification) {
		notificationEntityRepository.delete(notification);
	}
	
	public void deleteSessionRequest(SessionRequest sessionRequest) {
		sessionRequestRepository.delete(sessionRequest);
	}
	
	public void deleteStudentGroupRequest(StudentGroupRequest studentGroupRequest) {
		studentGroupRequestRepository.delete(studentGroupRequest);
	}
	
	public void deleteReminder(Reminder reminder) {
		reminderRepository.delete(reminder);
	}

}