package com.mldis.repo;
import com.mldis.model.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
    List<TestOrder> findByPatientCode(String patientCode);
}
