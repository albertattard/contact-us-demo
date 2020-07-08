package demo.boot.office;

import lombok.AllArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JpaContactUsService implements ContactUsService {

  private final OfficesRepository repository;

  @Override
  public List<Office> list() {
    return mapToOffices( repository.findAll() );
  }

  @Override
  public Optional<Office> findOneByName( final String office ) {
    return repository
      .findById( office )
      .map( mapToOffice() );
  }

  @Override
  public List<Office> findAllInCountry( final String country ) {
    return mapToOffices( repository.findAllByCountryIgnoreCase( country ) );
  }

  @Override
  @Transactional
  @Retryable( ObjectOptimisticLockingFailureException.class )
  public Optional<Office> update( final Office office ) {
    return repository
      .findById( office.getName() )
      .map( updateEntity( office ) )
      .map( repository::save )
      .map( mapToOffice() );
  }

  @Override
  @Transactional
  @Retryable( ObjectOptimisticLockingFailureException.class )
  public Optional<Office> delete( final String name ) {
    return repository
      .findById( name )
      .map( deleteEntity() )
      .map( mapToOffice() );
  }

  private Function<OfficeEntity, OfficeEntity> deleteEntity() {
    return entity -> {
      repository.delete( entity );
      return entity;
    };
  }

  private List<Office> mapToOffices( final List<OfficeEntity> entities ) {
    return entities
      .stream()
      .map( mapToOffice() )
      .collect( Collectors.toList() );
  }

  private Function<OfficeEntity, Office> mapToOffice() {
    return entity -> new Office(
      entity.getName(),
      entity.getAddress(),
      entity.getPhone(),
      entity.getEmail()
    );
  }

  private Function<OfficeEntity, OfficeEntity> updateEntity( final Office office ) {
    return entity -> {
      entity.setAddress( office.getAddress() );
      entity.setPhone( office.getPhone() );
      entity.setEmail( office.getEmail() );
      return entity;
    };
  }
}
