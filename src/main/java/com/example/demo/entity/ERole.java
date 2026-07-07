package com.example.demo.entity;

/**
 * The four roles supported by ScholarStream's RBAC model.
 * A user is assigned exactly one role at registration time; there is
 * no implicit default - the role must be set explicitly.
 */
public enum ERole {
    ROLE_USER,
    ROLE_AUTHOR,
    ROLE_REVIEWER,
    ROLE_ADMIN
}
