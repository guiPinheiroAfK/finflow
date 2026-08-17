package com.finflow.application.dto.banktransaction;

import java.util.UUID;

/** Exatamente um dos dois deve ser informado. */
public record ManualReconcileRequest(UUID receivableId, UUID payableId) {
}
