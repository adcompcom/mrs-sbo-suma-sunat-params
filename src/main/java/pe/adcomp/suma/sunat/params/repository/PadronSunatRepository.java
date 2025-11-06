package pe.adcomp.suma.sunat.params.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.adcomp.suma.sunat.params.entity.PadronSunat;

import java.util.Optional;

@Repository
public interface PadronSunatRepository extends JpaRepository<PadronSunat, Long> {

    /**
     * Busca un registro por RUC
     */
    Optional<PadronSunat> findByRuc(Long ruc);

    /**
     * Busca un registro por DNI
     */
    Optional<PadronSunat> findByDni(Long dni);

    /**
     * Busca por RUC o DNI usando una consulta personalizada
     */
    @Query("SELECT p FROM PadronSunat p WHERE p.ruc = :numero OR p.dni = :numero")
    Optional<PadronSunat> findByRucOrDni(@Param("numero") Long numero);
}
