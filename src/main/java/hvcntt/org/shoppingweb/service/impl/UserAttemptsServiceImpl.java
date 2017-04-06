package hvcntt.org.shoppingweb.service.impl;

import hvcntt.org.shoppingweb.dao.entity.UserAttempt;
import hvcntt.org.shoppingweb.dao.repository.UserAttemptsRepository;
import hvcntt.org.shoppingweb.dao.repository.UserRepository;
import hvcntt.org.shoppingweb.service.UserAttemptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserAttemptsServiceImpl implements UserAttemptsService {

	@Autowired
	private UserAttemptsRepository userAttemptsRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public void insertAttempts(String username) {
		UserAttempt userAttempt = new UserAttempt(username, 1, new Date());
		userAttemptsRepository.save(userAttempt);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public void updateAttempts(String username) {
		userAttemptsRepository.updateAttempts(username);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public void save(UserAttempt userAttempt) {
		userAttemptsRepository.save(userAttempt);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public void lockUser(String username) {
		userRepository.updateLocked(username, false);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public void resetAttempts(String username) {
		userRepository.updateLocked(username, true);
		userAttemptsRepository.resetAttempts(username);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public UserAttempt getAttemptsEntity(String name) {
		return userAttemptsRepository.findByUsername(name);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, readOnly = false)
	public int getCountAttempts(String username) {
		return userAttemptsRepository.getOne(username).getAttempts();
	}

	@Override
	public boolean checkAttempts(String username) {
		if(this.getAttemptsEntity(username) == null){
			return true;
		}else{
			return false;
		}
	}
}
