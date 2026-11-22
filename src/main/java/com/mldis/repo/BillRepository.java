package com.mldis.repo;
import com.mldis.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatientCode(String patientCode);
}
