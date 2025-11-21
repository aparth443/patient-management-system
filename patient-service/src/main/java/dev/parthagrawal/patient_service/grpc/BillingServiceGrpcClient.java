package dev.parthagrawal.patient_service.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    //localhost:9001/BillingService/CreatePatientAccount
    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort) {
        log.info("Connecting to Billing gRPC Service at {}:{}", serverAddress, serverPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String patientName, String patientEmail) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(patientName)
                .setEmail(patientEmail)
                .build();
        log.info("Sending CreateBillingAccount request to Billing Service: {}", request);
        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received CreateBillingAccount response from Billing Service: {}", response);
        return response;
    }
}
