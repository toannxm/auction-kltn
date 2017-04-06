package hvcntt.org.shoppingweb.validator;

import hvcntt.org.shoppingweb.dao.model.UserModel;
import hvcntt.org.shoppingweb.exception.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import hvcntt.org.shoppingweb.dao.entity.User;
import hvcntt.org.shoppingweb.service.UserService;

@Component
public class UserValidator implements Validator {

	@Autowired
	private UserService userService;

	public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

	public void validate(Object o, Errors errors) {
        UserModel user = (UserModel) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmptyUsername");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        try {
            if (userService.findByUsername(user.getUsername()) != null) {
                errors.rejectValue("username", "Duplicate.userForm.username");
            }
        } catch (UserNotFoundException e) {
            e.getMessage();
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmptyPassword");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
	}
}
