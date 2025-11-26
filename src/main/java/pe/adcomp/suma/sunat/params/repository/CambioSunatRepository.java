package pe.adcomp.suma.sunat.params.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.adcomp.suma.sunat.params.entity.CambioSunat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CambioSunat
 */
@Repository
public interface CambioSunatRepository extends JpaRepository<CambioSunat, LocalDate> {

    /**
     * Busca un registro por fecha exacta
     */
    Optional<CambioSunat> findByFecha(LocalDate fecha);

    /**
     * Busca registros por año y mes usando una consulta nativa
     */
    @Query(value = "SELECT * FROM cambio_sunat WHERE YEAR(fecha) = :ano AND MONTH(fecha) = :mes ORDER BY fecha DESC", 
           nativeQuery = true)
    List<CambioSunat> findByAnoAndMes(@Param("ano") int ano, @Param("mes") int mes);

}
