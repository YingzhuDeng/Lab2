//package edu.sjsu.examples.helloworld;
package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code PollService} server.
*/

public class PollServer {
  private static final Logger logger = Logger.getLogger(PollServer.class.getName());

  /* The port on which the server should run */
  private int port = 50051;
  private ServerImpl server;

  private void start() throws Exception {
    server = NettyServerBuilder.forPort(port)
        //.addService(GreeterGrpc.bindService(new GreeterImpl()))
        .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
        .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        PollServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws Exception {
    final PollServer server = new PollServer();
    server.start();
  }

  private class PollServiceImpl implements PollServiceGrpc.PollService {

    @Override
    public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver) {
      //HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
      PollResponse reply = PollResponse.newBuilder().setId(req.getModeratorId()).build();
      logger.info("Moderator ID: " + req.getModeratorId());
	responseObserver.onValue(reply);
      responseObserver.onCompleted();
    }
  }
}
