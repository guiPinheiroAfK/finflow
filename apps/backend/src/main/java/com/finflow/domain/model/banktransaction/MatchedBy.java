package com.finflow.domain.model.banktransaction;

/** ADR-0004 §4: auditoria da decisão de matching -- dado, não decisão perdida. */
public enum MatchedBy {
    AUTO,
    MANUAL
}
