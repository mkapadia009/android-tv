package com.app.itaptv.permissionHandling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation stores the provided permissions for use when the flow needs them
 * {@link #permissions} List of permissions to check
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RegisterPermission {
    String[] permissions();
}
