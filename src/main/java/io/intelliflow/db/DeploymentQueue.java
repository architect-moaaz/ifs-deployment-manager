package io.intelliflow.db;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;

@MongoEntity(collection = "deployment_queue")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentQueue {

	private ObjectId id;

	private String buildno;
	

	private Date queuetime;
	

	private String buildstatus;
	
}
