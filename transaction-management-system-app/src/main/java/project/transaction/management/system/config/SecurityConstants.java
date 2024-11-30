package project.transaction.management.system.config;

public class SecurityConstants {
    public static final long JWT_EXPIRY_DATE = 86_400_000;
    public static final String LOGIN_PATH = "/transaction-management-system/api/v1/users/login";
    public static final String REGISTER_PATH = "/transaction-management-system/api/v1/users/register";
    public static final String ACTUATOR = "/actuator/health";
    public static final String FAVICON = "/favicon.ico";
}
