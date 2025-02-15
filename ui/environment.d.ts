namespace NodeJS {
  interface ProcessEnv {
    NEXTAUTH_URL: string;
    NEXTAUTH_SECRET: string;
    BACKEND_URL: string;
    KEYCLOAK_CLIENTID?: string;
    KEYCLOAK_CLIENTSECRET?: string;
    NEXT_PUBLIC_KEYCLOAK_URL?: string;
    NEXT_PUBLIC_PRODUCTIZED_BUILD?: "true" | "false";
    LOG_LEVEL?: "fatal" | "error" | "warn" | "info" | "debug" | "trace";
    CONSOLE_MODE?: "read-only" | "read-write";
  }
}
