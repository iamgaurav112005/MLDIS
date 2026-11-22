package com.mldis.repo;
import com.mldis.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByMedicineCode(String medicineCode);
}
