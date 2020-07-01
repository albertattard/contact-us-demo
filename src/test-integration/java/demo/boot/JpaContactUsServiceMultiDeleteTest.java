package demo.boot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName( "JPA contact us service (multi-delete)" )
@SpringBootTest( webEnvironment = WebEnvironment.NONE )
public class JpaContactUsServiceMultiDeleteTest {

  @Autowired
  private OfficesRepository repository;

  @Autowired
  private ContactUsService service;

  private static final OfficeEntity ENTITY = new OfficeEntity(
    "ThoughtWorks Test Office",
    "Test Address",
    "Test Country",
    "Test Phone",
    "Test Email",
    "Test Webpage"
  );

  @BeforeEach
  public void setUp() {
    repository.save( ENTITY );
  }

  @AfterEach
  public void tearDown() {
    repository.delete( ENTITY );
  }

  @Test
  @DisplayName( "should delete the office only once" )
  public void shouldHandleConcurrentDeletes() throws Exception {
    /* Will use 12 threads to bombard the delete operation */
    TestHelper
      .withThreads( 12 )
      .runTestAgainstService( service )
      .assertAllWentAsExpected()
    ;
  }

  private static class TestHelper {
    /* The number of threads to use */
    private final int numberOfThreads;

    /* Coordinate the threads to hit the delete operation all at 'the same time' */
    private final CyclicBarrier barrier;

    /* Count the number of successful deletes (which should be 0) and any errors (which should be none) */
    private final AtomicInteger deletedCount = new AtomicInteger();
    private final AtomicInteger errorCount = new AtomicInteger();

    private static TestHelper withThreads( final int numberOfThreads ) {
      return new TestHelper( numberOfThreads );
    }

    private TestHelper( final int numberOfThreads ) {
      this.numberOfThreads = numberOfThreads;
      this.barrier = new CyclicBarrier( numberOfThreads );
    }

    private TestHelper runTestAgainstService( final ContactUsService service ) throws InterruptedException {
      final Runnable deleteTask = createDeleteTask( service );
      final List<Thread> threads = runTaskOnThreads( deleteTask );
      waitForAllThreadsToFinish( threads );
      return this;
    }

    private Runnable createDeleteTask( final ContactUsService service ) {
      return () -> {
        try {
          /* Wait for all threads to reach this point */
          barrier.await( 10, TimeUnit.SECONDS );
          final Optional<Office> deleted = service.delete( ENTITY.getName() );
          deleted.ifPresent( e -> deletedCount.incrementAndGet() );
        } catch ( final Exception e ) {
          errorCount.incrementAndGet();
        }
      };
    }

    private List<Thread> runTaskOnThreads( final Runnable deleteTask ) {
      final List<Thread> threads = new ArrayList<>( numberOfThreads );

      for ( int i = 1; i <= numberOfThreads; i++ ) {
        final Thread thread = new Thread( deleteTask, String.format( "DELETE-%d", i ) );
        thread.start();
        threads.add( thread );
      }
      return threads;
    }

    private void waitForAllThreadsToFinish( final List<Thread> threads ) throws InterruptedException {
      for ( final Thread thread : threads ) {
        thread.join( TimeUnit.SECONDS.toMillis( 5 ) );
      }
    }

    private void assertAllWentAsExpected() {
      assertAllThreadsRan();
      assertOnlyOneOfficeIsDeleted();
      assertNoErrorsOccurred();
    }

    private TestHelper assertOnlyOneOfficeIsDeleted() {
      assertThat( deletedCount.intValue() )
        .describedAs( "only one deletion should succeed" )
        .isEqualTo( 1 );
      return this;
    }

    private TestHelper assertNoErrorsOccurred() {
      assertThat( errorCount.intValue() )
        .describedAs( "no errors should occur" )
        .isEqualTo( 0 );
      return this;
    }

    private TestHelper assertAllThreadsRan() {
      assertThat( barrier.getNumberWaiting() )
        .describedAs( "all threads should have ran" )
        .isEqualTo( 0 );
      return this;
    }
  }
}
