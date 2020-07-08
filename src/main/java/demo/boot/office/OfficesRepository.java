package demo.boot.office;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficesRepository extends JpaRepository<OfficeEntity, String> {

  List<OfficeEntity> findAllByCountryIgnoreCase( final String country );
}
