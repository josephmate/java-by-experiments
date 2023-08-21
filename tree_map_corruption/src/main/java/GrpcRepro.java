/**
 * Reproduces the corrupted TreeMap issues through Grpc rather than directly through threads.
 *
 * Demonstrates that the simplified 2 thread reproduce code isn't that unrealistic. Exceptionsm
 * get accidentally swallowed all the time especially if there are RuntimeExceptions like NPEs.
 */
public class GrpcRepro {
    public static void main(String[] args) {

    }
}
