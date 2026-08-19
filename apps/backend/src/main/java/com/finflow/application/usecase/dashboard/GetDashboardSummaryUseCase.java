package com.finflow.application.usecase.dashboard;

import com.finflow.application.dto.dashboard.DashboardSummaryResponse;
import com.finflow.domain.model.order.OrderStatus;
import com.finflow.domain.model.payable.PayableStatus;
import com.finflow.domain.model.quote.QuoteStatus;
import com.finflow.domain.model.receivable.ReceivableStatus;
import com.finflow.domain.repository.OrderRepository;
import com.finflow.domain.repository.PayableRepository;
import com.finflow.domain.repository.QuoteRepository;
import com.finflow.domain.repository.ReceivableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** KPIs de topo do Dashboard -- consultas agregadas, sem carregar entidades inteiras. */
@Service
public class GetDashboardSummaryUseCase {

    private static final List<ReceivableStatus> OPEN_RECEIVABLE_STATUSES =
            List.of(ReceivableStatus.PENDING, ReceivableStatus.PARTIAL, ReceivableStatus.OVERDUE);
    private static final List<QuoteStatus> PENDING_QUOTE_STATUSES =
            List.of(QuoteStatus.DRAFT, QuoteStatus.SENT);

    private final ReceivableRepository receivableRepository;
    private final PayableRepository payableRepository;
    private final QuoteRepository quoteRepository;
    private final OrderRepository orderRepository;

    public GetDashboardSummaryUseCase(ReceivableRepository receivableRepository, PayableRepository payableRepository,
                                       QuoteRepository quoteRepository, OrderRepository orderRepository) {
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
        this.quoteRepository = quoteRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse execute() {
        return new DashboardSummaryResponse(
                receivableRepository.sumAmountByStatusIn(OPEN_RECEIVABLE_STATUSES),
                receivableRepository.countByStatusIn(OPEN_RECEIVABLE_STATUSES),
                receivableRepository.countByStatusIn(List.of(ReceivableStatus.OVERDUE)),
                payableRepository.sumAmountBrlByStatus(PayableStatus.PENDING),
                payableRepository.countByStatus(PayableStatus.PENDING),
                quoteRepository.countByStatusIn(PENDING_QUOTE_STATUSES),
                orderRepository.countByStatus(OrderStatus.CONFIRMED)
        );
    }
}
