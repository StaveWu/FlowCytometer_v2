package application.starter;

import application.starter.ProjectInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectInfoRepository extends CrudRepository<ProjectInfo, Long> {
}
