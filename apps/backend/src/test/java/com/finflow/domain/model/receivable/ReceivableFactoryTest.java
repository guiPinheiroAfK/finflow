package com.finflow.domain.model.receivable;

import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.model.customer.CustomerType;
import com.finflow.domain.model.order.Order;
import com.finflow.domain.model.product.Product;
import com.finflow.domain.model.product.ProductCategory;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.shared.PaymentMethod;
import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.model.supplier.SupplierCategory;
import com.finflow.domain.model.user.Role;
import com.finflow.domain.model.user.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** ADR-0001 §5 aplicado via ADR-0003: a soma das parcelas nunca diverge do total da venda. */
class ReceivableFactoryTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 7})
    void generatedInstallmentsSumToOrderTotalSale(int installments) {
        Customer customer = Customer.create(CustomerType.PESSOA_FISICA, "Ana", "111",
                "ana@ex.com", null, null, null);
        User seller = User.register("Vendedor", "v@ex.com", "hash", Role.SELLER);
        Supplier supplier = Supplier.create("Hotel X", SupplierCategory.HOTEL, null, null, null, 30, Currency.BRL);
        Product product = Product.create("Pacote", ProductCategory.PACOTE, supplier,
                new BigDecimal("100.00"), Currency.BRL, new BigDecimal("333.33"));

        Quote quote = Quote.create("ORC-2026-000001", customer, seller, null, null);
        quote.addItem(product, "item", 1, new BigDecimal("100.00"), new BigDecimal("333.33"), null, null);
        quote.markSent();

        Order order = Order.fromQuote("VND-2026-000001", quote, PaymentMethod.CARTAO_CREDITO,
                installments, currency -> BigDecimal.ONE);

        List<Receivable> receivables = ReceivableFactory.generate(order);

        assertThat(receivables).hasSize(installments);
        BigDecimal sum = receivables.stream().map(Receivable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(sum).isEqualByComparingTo(order.getTotalSale());
    }
}
