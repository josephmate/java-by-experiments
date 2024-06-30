import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Reproduces the corrupted TreeMap issues through Grpc rather than directly through threads.
 *
 * Demonstrates that the simplified 2 thread reproduce code isn't that unrealistic. Exceptionsm
 * get accidentally swallowed all the time especially if there are RuntimeExceptions like NPEs.
 */
public class GrpcThrowNPE {
    public static void main(String[] args) throws Exception {
        final Server server = ServerBuilder.forPort(0)
            .addService(new ReceiptProcessorThrowNPEImpl())
            .build()
            .start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdownNow();
        }));

        final int port = server.getPort();
        System.out.println("Server started, listening on " + port);

        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", port)
            .usePlaintext()
            .build();
        final ReceiptProcessorServiceGrpc.ReceiptProcessorServiceFutureStub futureStub = ReceiptProcessorServiceGrpc.newFutureStub(channel);

        Random random = new Random();
        for (int i = 0; i < 1_000; i++) {
            ReceiptProcessorServiceOuterClass.AddReceiptRequest request = ReceiptProcessorServiceOuterClass.AddReceiptRequest.newBuilder()
                .setTimestamp(random.nextInt(10_000))
                .setTotalPrice(random.nextInt(10_000))
                .build();
            futureStub.addReceipt(request);
        }

        server.shutdown();
        server.awaitTermination(1, TimeUnit.DAYS);
    }
}

class ReceiptProcessorThrowNPEImpl extends ReceiptProcessorServiceGrpc.ReceiptProcessorServiceImplBase {
    @Override
    public void addReceipt(
        ReceiptProcessorServiceOuterClass.AddReceiptRequest req,
        StreamObserver<ReceiptProcessorServiceOuterClass.AddReceiptResponse> responseObserver
    ) {
        throw new NullPointerException();
    }
}