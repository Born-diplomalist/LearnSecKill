package com.born.secKill02.config.validator;

import com.born.secKill02.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 09:26:41
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required=false;

    /**
     * Initializes the validator in preparation for
     * {isValid(Object, ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p/>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required =constraintAnnotation.required();
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p/>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required){
            return ValidatorUtils.isMobile(value);
        }
        return StringUtils.isEmpty(value)?true: ValidatorUtils.isMobile(value);
    }
}
