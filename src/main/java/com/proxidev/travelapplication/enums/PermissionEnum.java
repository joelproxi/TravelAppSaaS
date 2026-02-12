package com.proxidev.travelapplication.enums;

import lombok.Getter;

@Getter
public enum PermissionEnum {

    // Voyages
    VOYAGE_CREATE("voyage:create", "voyage", "Créer un voyage"),
    VOYAGE_READ("voyage:read", "voyage", "Voir les voyages"),
    VOYAGE_UPDATE("voyage:update", "voyage", "Modifier un voyage"),
    VOYAGE_DELETE("voyage:delete", "voyage", "Supprimer un voyage"),

    // Agences
    AGENCY_CREATE("agency:create", "agency", "Créer une agence"),
    AGENCY_READ("agency:read", "agency", "Voir les agences"),
    AGENCY_UPDATE("agency:update", "agency", "Modifier une agence"),
    AGENCY_DELETE("agency:delete", "agency", "Supprimer une agence"),

    // Employés
    EMPLOYEE_CREATE("employee:create", "employee", "Ajouter un employé"),
    EMPLOYEE_READ("employee:read", "employee", "Voir les employés"),
    EMPLOYEE_UPDATE("employee:update", "employee", "Modifier un employé"),
    EMPLOYEE_DELETE("employee:delete", "employee", "Supprimer un employé"),

    // Bus
    BUS_CREATE("bus:create", "bus", "Ajouter un bus"),
    BUS_READ("bus:read", "bus", "Voir les bus"),
    BUS_UPDATE("bus:update", "bus", "Modifier un bus"),
    BUS_DELETE("bus:delete", "bus", "Supprimer un bus"),

    // Rôles
    ROLE_CREATE("role:create", "role", "Créer un rôle"),
    ROLE_READ("role:read", "role", "Voir les rôles"),
    ROLE_UPDATE("role:update", "role", "Modifier un rôle"),
    ROLE_DELETE("role:delete", "role", "Supprimer un rôle"),
    ROLE_ASSIGN("role:assign", "role", "Assigner/retirer un rôle"),

    // Compagnie
    COMPANY_READ("company:read", "company", "Voir les infos compagnie"),
    COMPANY_UPDATE("company:update", "company", "Modifier la compagnie"),
    COMPANY_MANAGE("company:manage", "company", "Gérer la compagnie"),

    // Réservations
    BOOKING_CREATE("booking:create", "booking", "Créer une réservation"),
    BOOKING_READ("booking:read", "booking", "Voir les réservations"),
    BOOKING_UPDATE("booking:update", "booking", "Modifier une réservation"),
    BOOKING_CANCEL("booking:cancel", "booking", "Annuler une réservation");

    private final String code;
    private final String module;
    private final String description;

    PermissionEnum(String code, String module, String description) {
        this.code = code;
        this.module = module;
        this.description = description;
    }
}
