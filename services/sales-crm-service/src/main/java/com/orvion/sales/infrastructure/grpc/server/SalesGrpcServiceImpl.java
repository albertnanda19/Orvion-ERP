package com.orvion.sales.infrastructure.grpc.server;

import com.orvion.sales.domain.model.Customer;
import com.orvion.sales.domain.model.Opportunity;
import com.orvion.sales.domain.repository.CustomerRepository;
import com.orvion.sales.domain.repository.OpportunityRepository;
import com.orvion.sales.grpc.*;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class SalesGrpcServiceImpl extends SalesServiceGrpc.SalesServiceImplBase {
    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;
    private final Timer customerInfoTimer;
    private final Timer pipelineTimer;

    public SalesGrpcServiceImpl(CustomerRepository customerRepository,
                                OpportunityRepository opportunityRepository,
                                MeterRegistry registry) {
        this.customerRepository = customerRepository;
        this.opportunityRepository = opportunityRepository;
        this.customerInfoTimer = registry.timer("grpc.sales.customer_info");
        this.pipelineTimer = registry.timer("grpc.sales.pipeline");
    }

    @Override
    public void getCustomerInfo(CustomerInfoRequest request, StreamObserver<CustomerInfoResponse> responseObserver) {
        customerInfoTimer.record(() -> {
            try {
                Optional<Customer> opt = customerRepository.findById(UUID.fromString(request.getCustomerId()));
                if (opt.isEmpty()) {
                    responseObserver.onError(new RuntimeException("Customer not found: " + request.getCustomerId()));
                    return;
                }
                Customer c = opt.get();
                CustomerInfoResponse response = CustomerInfoResponse.newBuilder()
                    .setCustomerId(c.getId().toString())
                    .setName(c.getName())
                    .setCreditLimit(c.getCreditLimit() != null ? c.getCreditLimit().getAmount().toPlainString() : "0")
                    .setOutstanding(c.getOutstandingBalance() != null ? c.getOutstandingBalance().getAmount().toPlainString() : "0")
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }

    @Override
    public void getSalesPipeline(PipelineRequest request, StreamObserver<PipelineResponse> responseObserver) {
        pipelineTimer.record(() -> {
            try {
                List<Opportunity> opportunities = opportunityRepository.findAllByTenantId(request.getTenantId());
                Map<String, Integer> byStage = opportunities.stream()
                    .filter(o -> o.getStage() != null)
                    .collect(Collectors.groupingBy(
                        o -> o.getStage().name(),
                        Collectors.summingInt(o -> 1)
                    ));
                BigDecimal totalExpected = opportunities.stream()
                    .filter(o -> o.getExpectedValue() != null)
                    .map(o -> o.getExpectedValue().getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                PipelineResponse.Builder builder = PipelineResponse.newBuilder()
                    .setTotalOpportunities(opportunities.size())
                    .setTotalExpectedValue(totalExpected.toPlainString());
                byStage.forEach(builder::putByStage);

                responseObserver.onNext(builder.build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }
}
