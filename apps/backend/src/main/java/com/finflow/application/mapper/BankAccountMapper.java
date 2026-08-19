package com.finflow.application.mapper;

import com.finflow.application.dto.bankaccount.BankAccountResponse;
import com.finflow.domain.model.bankaccount.BankAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountResponse toResponse(BankAccount bankAccount);
}
