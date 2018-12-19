package application.channel;

import application.channel.ChannelInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelInfoRepository extends CrudRepository<ChannelInfo, Long> {
}
