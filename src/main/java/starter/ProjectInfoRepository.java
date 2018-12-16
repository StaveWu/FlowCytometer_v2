package starter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectInfoRepository extends CrudRepository<ProjectInfo, Long> {
}
