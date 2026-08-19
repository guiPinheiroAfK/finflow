package com.finflow.domain.model.quote;

import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.model.customer.CustomerType;
import com.finflow.domain.model.product.Product;
import com.finflow.domain.model.product.ProductCategory;
import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.model.supplier.SupplierCategory;
import com.finflow.domain.model.user.Role;
import com.finflow.domain.model.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** ADR-0003 §2: a guarda de estado é o que torna approve() idempotente por construção. */
class QuoteStateMachineTest {

    private final Customer customer = Customer.create(CustomerType.PESSOA_FISICA, "Ana", "111",
            "ana@ex.com", null, null, null);
    private final User seller = User.register("Vendedor", "v@ex.com", "hash", Role.SELLER);
    private final Supplier supplier = Supplier.create("Hotel X", SupplierCategory.HOTEL, null,
            null, null, 30, Currency.BRL);
    private final Product product = Product.create("Pacote", ProductCategory.PACOTE, supplier,
            new BigDecimal("100.00"), Currency.BRL, new BigDecimal("150.00"));

    private Quote newDraftQuote() {
        return Quote.create("ORC-2026-000001", customer, seller, null, null);
    }

    @Test
    void freshQuoteIsDraftAndApprovable() {
        Quote quote = newDraftQuote();
        assertThat(quote.getStatus()).isEqualTo(QuoteStatus.DRAFT);
        assertThatCode(quote::requireApprovable).doesNotThrowAnyException();
    }

    @Test
    void sentQuoteIsAlsoApprovable() {
        Quote quote = newDraftQuote();
        quote.markSent();
        assertThat(quote.getStatus()).isEqualTo(QuoteStatus.SENT);
        assertThatCode(quote::requireApprovable).doesNotThrowAnyException();
    }

    @Test
    void approvedQuoteRejectsSecondApproval() {
        Quote quote = newDraftQuote();
        quote.markApproved();

        assertThat(quote.isApproved()).isTrue();
        assertThatThrownBy(quote::requireApprovable).isInstanceOf(InvalidQuoteStateException.class);
    }

    @Test
    void approvedQuoteCannotBeEdited() {
        Quote quote = newDraftQuote();
        quote.markApproved();

        assertThatThrownBy(() -> quote.addItem(product, "desc", 1,
                BigDecimal.TEN, BigDecimal.TEN, null, null))
                .isInstanceOf(InvalidQuoteStateException.class);
    }

    @Test
    void draftQuoteCannotBeSentTwice() {
        Quote quote = newDraftQuote();
        quote.markSent();

        assertThatThrownBy(quote::markSent).isInstanceOf(InvalidQuoteStateException.class);
    }

    @Test
    void addingItemsRecalculatesTotals() {
        Quote quote = newDraftQuote();
        quote.addItem(product, "item 1", 2, new BigDecimal("100.00"), new BigDecimal("150.00"), null, null);
        quote.addItem(product, "item 2", 1, new BigDecimal("50.00"), new BigDecimal("80.00"), null, null);

        assertThat(quote.getTotalCost()).isEqualByComparingTo("250.00");
        assertThat(quote.getTotalSale()).isEqualByComparingTo("380.00");
        assertThat(quote.getMargin()).isEqualByComparingTo("130.00");
    }
}
